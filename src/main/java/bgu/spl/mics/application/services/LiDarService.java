package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.events.*;
import bgu.spl.mics.application.objects.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker lidarWorker;
    private final CountDownLatch latch;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, CountDownLatch latch) {
        super(LiDarWorkerTracker.getName());
        this.lidarWorker = LiDarWorkerTracker;
        this.latch = latch;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        lidarWorker.setStatus(STATUS.UP);

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)->{
            List<TrackedObject> trackedObjects = lidarWorker.handleTick(t.getCurrentTime());
            if (lidarWorker.getStatus()==STATUS.ERROR){
                sendEvent(new LastFrameEvent<List<TrackedObject>>(lidarWorker.getLastTrackedObjects(),lidarWorker.getName()));
                sendBroadcast(new CrashedBroadcast("liDar", lidarWorker.getErrorDescription(), t.getCurrentTime()));
                terminate();
            }
            if (!trackedObjects.isEmpty()) {
                TrackedObjectsEvent e = new TrackedObjectsEvent(trackedObjects);
                sendEvent(e);
            }
            if (lidarWorker.getStatus()== STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(this.getName(), "liDar"));
                terminate();
            }
        });

        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent e)-> {
           lidarWorker.handleDetectedObjects(e.getDetectedObjects());
        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast t)->{
            if(t.getSensorType().equals("camera")){
                lidarWorker.decrementNumOfCameras();
            }
            if (t.getSensorType().equals("TimeService"))
                lidarWorker.setStatus(STATUS.DOWN);
            if (lidarWorker.getStatus()== STATUS.DOWN){
                sendBroadcast(new TerminatedBroadcast(this.getName(), "liDar"));
                terminate();}

        } );

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast t)->{
            List<TrackedObject> lastTrackedObjects = lidarWorker.handleCrashed();
            LastFrameEvent<List<TrackedObject>> e = new LastFrameEvent<>(lastTrackedObjects, lidarWorker.getName());
            sendEvent(e);
            TerminatedBroadcast b = new TerminatedBroadcast(this.getName(),"liDar");
            sendBroadcast(b);
            terminate();
        });
        latch.countDown();
    }
}

