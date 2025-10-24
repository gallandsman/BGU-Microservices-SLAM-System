package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int id;
    private int frequency;
    private STATUS status = STATUS.DOWN;
    private List<TrackedObject> lastTrackedObjects = new ArrayList<>();
    private int time = 0;
    private LiDarDataBase dataBase;
    private List <StampedDetectedObjects>  lastDetectedObjects = new ArrayList<>();
    private int numOfCameras;
    private String errorDescription;

    public LiDarWorkerTracker(){}

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        status = STATUS.DOWN;
        errorDescription = "";
    }

    public List<TrackedObject> handleTick(int time) {
        this.time = time;
        List<StampedCloudPoints> stamped= dataBase.getCloudPointsTime(time-frequency);
        List<TrackedObject> trackedObjects = new ArrayList<>();
        if(!checkIfError(stamped)) {
            for (int j = 0; j < lastDetectedObjects.size(); ) {
                StampedDetectedObjects objects = lastDetectedObjects.get(j);
                int detectedTime = objects.getTime();
                DetectedObject[] detectedObjects = objects.getDetectedObjects();
                if (detectedTime + this.frequency <= time) {
                    List<StampedCloudPoints> stampedCloudPoints= dataBase.getCloudPointsTime(detectedTime);
                    List<TrackedObject> tracked = track(stampedCloudPoints, detectedObjects);
                    trackedObjects.addAll(tracked);
                    lastDetectedObjects.remove(j);
                } else {
                    j++;
                }
            }
            if (lastDetectedObjects.isEmpty() & numOfCameras == 0)
                status = STATUS.DOWN;
            if (!trackedObjects.isEmpty())
                lastTrackedObjects = trackedObjects;
            StatisticalFolder.getInstance().AddNumTrackedObjects(trackedObjects.size());
        }
        return trackedObjects;
    }

    private List<TrackedObject> track(List<StampedCloudPoints> stampedCloudPoints, DetectedObject[] detectedObjects) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        for (int i = 0; i < detectedObjects.length; i++) {
            DetectedObject object = detectedObjects[i];
            String id = object.getId();
            for (StampedCloudPoints stampedCloudPoint : stampedCloudPoints) {
                if (id.equals(stampedCloudPoint.getId())) {
                    List<List<Double>> objectCloudPoints = stampedCloudPoint.getCloudPoints();
                    CloudPoint[] coordinates = new CloudPoint[(objectCloudPoints.size())];
                    int j = 0;
                    for (List<Double> point : objectCloudPoints) {
                        coordinates[j] = new CloudPoint(point.get(0), point.get(1));
                        j++;
                    }
                    TrackedObject t = new TrackedObject(id, stampedCloudPoint.getTime(), object.getDescription(), coordinates);
                    trackedObjects.add(t);
                }
            }
        }
        return trackedObjects;
    }

    public void handleDetectedObjects(StampedDetectedObjects objects) {
        lastDetectedObjects.add(objects);
    }

    public void initializeDataBase(LiDarDataBase liDarDataBase) {
        this.dataBase = liDarDataBase;
    }

    public List<TrackedObject> handleCrashed(){
        status=STATUS.DOWN;
        return lastTrackedObjects;
    }

    public boolean checkIfError(List<StampedCloudPoints> stampedCloudPoints){
            for (StampedCloudPoints stampedCloudPoint : stampedCloudPoints) {
                if (stampedCloudPoint.getId().equals("ERROR")) {
                    status = STATUS.ERROR;
                    errorDescription = "LiDarWorkerTracker"+id;
                    return true;
                }
            }
                return false;
    }

     public void decrementNumOfCameras() {
            numOfCameras--;
            if (numOfCameras == 0) {
                if (lastDetectedObjects.isEmpty())
                    status = STATUS.DOWN;
            }
        }

    public String toString(){
        return getName() + " Frequency: " + frequency + " Status: " + status;
    }

    public String getName() { return "lidar"+id;
    }
    public STATUS getStatus() {return status;}

    public void setStatus(STATUS status) {this.status = status;}

    public void setNumOfCameras(int numOfCameras) {
       this.numOfCameras=numOfCameras;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }
}
