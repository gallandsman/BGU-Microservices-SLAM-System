package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.events.*;
import bgu.spl.mics.application.messages.events.CrashedBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import java.util.concurrent.CountDownLatch;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final CountDownLatch latch;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, CountDownLatch latch) {
        super(camera.getName());
        this.camera = camera;
        this.latch = latch;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        camera.setStatus(STATUS.UP);

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast t)-> {
            StampedDetectedObjects stampedObjects = camera.handleTick(t.getCurrentTime());
            if (camera.getStatus() == STATUS.ERROR){
                LastFrameEvent<StampedDetectedObjects> e = new LastFrameEvent<>(camera.getLastFrame(), camera.getName());
                sendEvent(e);
                CrashedBroadcast c = new CrashedBroadcast(camera.getName(), camera.getErrorDescription(), t.getCurrentTime());
                sendBroadcast(c);
                terminate();
            }
            else {
                if (stampedObjects != null) {
                    DetectObjectsEvent e = new DetectObjectsEvent(stampedObjects);
                    sendEvent(e);
                }
                if (camera.getStatus() == STATUS.DOWN) {
                    TerminatedBroadcast T = new TerminatedBroadcast(this.getName(), "camera");
                    sendBroadcast(T);
                    terminate();
                }
            }

        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast t)->{
            if(t.getSensorType().equals("TimeService")) {
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(this.getName(), "camera"));
                terminate();
            }
        } );

        subscribeBroadcast(CrashedBroadcast.class, (CrashedBroadcast t)-> {
            LastFrameEvent<StampedDetectedObjects> e = new LastFrameEvent<>(camera.getLastFrame(), camera.getName());
            sendEvent(e);
            TerminatedBroadcast b = new TerminatedBroadcast(this.getName(),"camera");
            sendBroadcast(b);
            terminate();
        });
        latch.countDown();
    }
}
