package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.events.*;
import bgu.spl.mics.application.messages.events.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

import java.util.concurrent.CountDownLatch;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    private final CountDownLatch latch;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu, CountDownLatch latch) {
        super("PoseEvent");
        this.gpsimu = gpsimu;
        this.latch = latch;

    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        gpsimu.setStatus(STATUS.UP);

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast t)->{
            if (t.getSensorType().equals("FusionSlam") || t.getSensorType().equals("TimeService")) {
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(this.getName(), "GPSIMU"));
                terminate();
            }
        } );
        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast t)->{
            gpsimu.setStatus(STATUS.DOWN);
            sendBroadcast(new TerminatedBroadcast(this.getName(), "GPSIMU"));
            terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
            Pose p = gpsimu.handleTick(t.getCurrentTime());
            PoseEvent newEvent = new PoseEvent(p);
            sendEvent(newEvent);

            if (gpsimu.getStatus() == STATUS.DOWN){
                terminate();
            }
        });
        latch.countDown();
    }
}
