package bgu.spl.mics.application.objects;
import java.util.*;

public class OutputData  {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    private Map <String, LandMark> landmarks;

    public OutputData() {
        systemRuntime = 0;
        numDetectedObjects = 0;
        numTrackedObjects = 0;
        numLandmarks = 0;
        landmarks = new HashMap<>();
    }
    public void setSystemRuntime(int systemRuntime) {
        this.systemRuntime = systemRuntime;
    }
    public void setNumDetectedObjects(int numDetectedObjects) {
        this.numDetectedObjects = numDetectedObjects;
    }
    public void setNumTrackedObjects(int numTrackedObjects) {
        this.numTrackedObjects = numTrackedObjects;
    }
    public void setNumLandmarks(int numLandmarks) {
        this.numLandmarks = numLandmarks;
    }
    public void setLandmarks( List<LandMark> landmarksList) {
        for (LandMark m : landmarksList) {
            landmarks.put(m.getId(), m);
        }
    }

    public Map <String, LandMark>getLandmarks(){
        return landmarks;
    }
    public int getSystemRuntime() {
        return systemRuntime;
    }
    public int getNumDetectedObjects() {
        return numDetectedObjects;
    }
    public int getNumTrackedObjects() {
        return numTrackedObjects;
    }
    public int getNumLandmarks() {
        return numLandmarks;
    }
}

class OutputDataError extends OutputData  {
    private String error;
    private String faultySensor;
    int crashTime = 0;
    private List<Pose> poses;
    private Map<String, List<StampedDetectedObjects>> cameraLastFrames;
    private Map<String, List<TrackedObject>> lidarLastFrames;

    public OutputDataError() {
        super();
        error = "";
        faultySensor = "";
        poses = new ArrayList<>();
        cameraLastFrames = new HashMap<>();
        lidarLastFrames = new HashMap<>();
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensors(String faultySensors) {
        this.faultySensor = faultySensors;
    }

    public void setCrashTime(int crashTime) {
        this.crashTime = crashTime;
    }

    public void setPoses(List<Pose> poses) {
        if(crashTime-1 < poses.size()) {
            this.poses = poses.subList(0, crashTime-1);
        }
        else {
            this.poses = poses;
        }
}
    public void setCameraLastFrames(Map<String, List<StampedDetectedObjects>> cameraLastFrames) {
        this.cameraLastFrames = cameraLastFrames;}
    public void setLidarLastFrames(Map<String, List<TrackedObject>> lidarLastFrames) {
        this.lidarLastFrames = lidarLastFrames;}
    public String getError() {
        return error;
    }
    public String getFaultySensor() {
        return faultySensor;
    }
    public List<Pose> getPoses() {
        return poses;
    }
    public Map<String, List<StampedDetectedObjects>> getCameraLastFrames() {
        return cameraLastFrames;
    }
    public Map<String, List<TrackedObject>> getLidarLastFrames() {
        return lidarLastFrames;
    }
}
