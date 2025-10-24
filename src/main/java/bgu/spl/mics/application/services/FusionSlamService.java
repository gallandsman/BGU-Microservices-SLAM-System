package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.events.*;
import bgu.spl.mics.application.objects.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    FusionSlam fusionSlam;
    private final CountDownLatch latch;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, CountDownLatch latch) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
        this.latch = latch;
    }
    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        fusionSlam.setStatus(STATUS.UP);

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast t)->{
            fusionSlam.handleCrashedBroadcast(t.getDescription(), t.getSensorId(), t.getCrashedTime());
        } );

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast t)->{
            if (fusionSlam.handleTerminateBroadcast(t.getSensorType())) {
                sendBroadcast(new TerminatedBroadcast("FusionSlam", "FusionSlam"));
                terminate();
            }
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
            fusionSlam.handleTick();
            if(fusionSlam.getStatus()==STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast("FusionSlam", "FusionSlam"));
                terminate();
            }
        });
        subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent t)->{
            fusionSlam.handleTrackedObjects(t.getTrackedObjects());
        });
        subscribeEvent(PoseEvent.class, (PoseEvent t)->{
            fusionSlam.handleNewPose(t.getCurrentPose());
        });
        subscribeEvent(LastFrameEvent.class, (LastFrameEvent t)->{
            if(t.getSensor().contains("camera"))
                fusionSlam.handleLastFrame(((StampedDetectedObjects)t.getLastFrame()), t.getSensor());
            else
                fusionSlam.handleLastFrame(((List<TrackedObject>) t.getLastFrame()), t.getSensor());
        });
        latch.countDown();
}
}
