package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<LandMark> landmarks;
    private List<Pose> poses;
    private List<TrackedObject> lastTrackedObjects;
    private Map<String, List<StampedDetectedObjects>> cameraLastFrames;
    private Map<String, List<TrackedObject>> lidarLastFrames;
    private STATUS systemStatus = STATUS.DOWN;
    private int numOfCamerasAndLiDars;
    private StatisticalFolder statisticalFolder;
    
    private FusionSlam() {
        landmarks = new ArrayList<>();
        poses = new ArrayList<>();
        lastTrackedObjects = new ArrayList<>();
        cameraLastFrames =  new HashMap<>();
        lidarLastFrames = new HashMap<>();
        statisticalFolder= StatisticalFolder.getInstance();
    }

    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    public static FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;
    }

    public void handleTick() {
        if(systemStatus == STATUS.UP) {
            // going through each TrackedObject from the event and creating new LandMark.
            Iterator<TrackedObject> iterator = lastTrackedObjects.iterator();
            while (iterator.hasNext()) {
                TrackedObject o = iterator.next();
                int time = o.getTime();
                if (time-1 < poses.size()) {
                    Pose p = poses.get(time - 1);
                    List<CloudPoint> coordinates = new ArrayList<>();
                    // for each object calculate the new coordinates for each of the coordinates in the cloud point array.
                    for (CloudPoint c : o.getCoordinates()) {
                        double Xglobal = (Math.cos(((p.getYaw()) * (Math.PI / 180))) * c.getX()) - (Math.sin(((p.getYaw()) * (Math.PI / 180))) * c.getY()) + p.getX();
                        double Yglobal = (Math.sin(((p.getYaw()) * (Math.PI / 180))) * c.getX()) + (Math.cos(((p.getYaw()) * (Math.PI / 180))) * c.getY()) + p.getY();
                        CloudPoint newC = new CloudPoint(Xglobal, Yglobal);
                        coordinates.add(newC);
                    }
                    LandMark oldLandMark = getLandMark(o.getId());
                    if (oldLandMark == null) {
                        LandMark newLandMark = new LandMark(o.getId(), o.getDescription(), coordinates);
                        landmarks.add(newLandMark);
                        statisticalFolder.AddNumLandmarks();
                    } else {
                        List<CloudPoint> AvgCoordinates = new ArrayList<>();
                        landmarks.remove(oldLandMark);
                        List<CloudPoint> OldList = oldLandMark.getCoordinates();
                        int i;
                        for (i = 0; i < OldList.size() & i < coordinates.size(); i++) {
                            AvgCoordinates.add(new CloudPoint((OldList.get(i).getX() + coordinates.get(i).getX()) / 2, (OldList.get(i).getY() + coordinates.get(i).getY()) / 2));
                        }
                        for (int j = i; j < OldList.size(); j++) {
                            AvgCoordinates.add(new CloudPoint(OldList.get(j)));
                        }
                        for (int k = i; k < coordinates.size(); k++) {
                            AvgCoordinates.add(new CloudPoint(coordinates.get(k)));
                        }
                        LandMark newLandMark2 = new LandMark(o.getId(), o.getDescription(), AvgCoordinates);
                        landmarks.add(newLandMark2);
                    }
                    iterator.remove();
                }
            }
        }
        if (lastTrackedObjects.isEmpty() && numOfCamerasAndLiDars==0){
            setStatus(STATUS.DOWN);
            writeOutputFile();
        }
    }
    public void handleTrackedObjects(List<TrackedObject> t) {
        if (systemStatus == STATUS.UP)
            lastTrackedObjects.addAll(t);
    }

    public void handleNewPose(Pose p){
        if (systemStatus==STATUS.UP) {
            poses.add(p);
        }
    }

    public void handleCrashedBroadcast(String description, String id, int time){
        numOfCamerasAndLiDars--;
        setStatus(STATUS.ERROR);
        statisticalFolder.setError(description);
        statisticalFolder.setFaultySensor(id);
        statisticalFolder.setCrashedTime(time);

    }
    public boolean handleTerminateBroadcast(String sender){
        if(sender.equals("GPSIMU"))
            return false;
        if (sender.equals("TimeService")){
            if (systemStatus == STATUS.UP)
                setStatus(STATUS.DOWN);
            return false;
        }
        numOfCamerasAndLiDars--;
        if (numOfCamerasAndLiDars==0 && systemStatus== STATUS.DOWN ){
            writeOutputFile();
            return true;
        }
        if (numOfCamerasAndLiDars == 0 && lastTrackedObjects.isEmpty() && systemStatus != STATUS.ERROR ){
            setStatus( STATUS.DOWN);
            writeOutputFile();
            return true;
        }
        if(numOfCamerasAndLiDars == 0 && systemStatus == STATUS.ERROR){
            setStatus( STATUS.DOWN);
            writeOutputFileError();
            return true;
        }
        return false;
    }

    public LandMark getLandMark(String id) {
       for (LandMark landmark : landmarks)
           if (landmark.getId().equals(id))
            return landmark;
     return null;
}

    public void setStatus(STATUS status) {
        this.systemStatus = status;
        this.statisticalFolder.setStatus(status);
    }
    public STATUS getStatus() {return systemStatus;}

    public void writeOutputFile() {
        OutputData outputData = new OutputData();
        outputData.setSystemRuntime(statisticalFolder.getSystemRuntime());
        outputData.setNumDetectedObjects(statisticalFolder.getNumDetectedObjects());
        outputData.setNumTrackedObjects(statisticalFolder.getNumTrackedObjects());
        outputData.setNumLandmarks(statisticalFolder.getNumLandmarks());
        outputData.setLandmarks(landmarks);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file.json")) {
            gson.toJson(outputData, writer);

        } catch (IOException e) {}    }

    public void writeOutputFileError() {
        OutputDataError outputData = new OutputDataError();
        outputData.setSystemRuntime(statisticalFolder.getCrashedTime());
        outputData.setNumDetectedObjects(statisticalFolder.getNumDetectedObjects());
        outputData.setNumTrackedObjects(statisticalFolder.getNumTrackedObjects());
        outputData.setNumLandmarks(statisticalFolder.getNumLandmarks());
        outputData.setLandmarks(landmarks);
        outputData.setCrashTime(statisticalFolder.getCrashedTime());
        outputData.setPoses(poses);
        outputData.setError(statisticalFolder.getError());
        outputData.setFaultySensors(statisticalFolder.getFaultySensor());
        outputData.setCameraLastFrames(cameraLastFrames);
        outputData.setLidarLastFrames(lidarLastFrames);

       Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output_file_Error.json")) {
            gson.toJson(outputData, writer);

        } catch (IOException e) {}
    }

    public void handleLastFrame(StampedDetectedObjects lastFrame, String sender) {
        List<StampedDetectedObjects> list = new ArrayList<>();
        if (lastFrame != null) {
            list.add(lastFrame);
        }
        cameraLastFrames.put(sender, list);
    }
    public void handleLastFrame (List<TrackedObject> lastFrame, String sender){
       if ( lastFrame!=null){
           lidarLastFrames.put(sender,  lastFrame);
       }
    }

    public void setNumOfCamerasAndLiDars(int numOfCamerasAndLiDars) {
        this.numOfCamerasAndLiDars = numOfCamerasAndLiDars;
    }
    public List<LandMark> getLandMarks() {
        return landmarks;
    }

    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }

     public void addlandmark (LandMark mark){
        landmarks.add(mark);
     }

     public void resetTests(){
        landmarks= new ArrayList<>();
        poses= new ArrayList<>();
        lastTrackedObjects= new ArrayList<>();
        setStatus(STATUS.DOWN);

     }
}
