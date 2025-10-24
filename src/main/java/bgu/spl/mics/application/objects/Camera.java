package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private final int id;
    private final int frequency;
    private final String camera_key;
    private STATUS status;
    private int time = 0;
    private List<StampedDetectedObjects> detectedObjects;
    private StampedDetectedObjects lastDetectedObjects;
    private int firstUndetectedIndex = 0;
    int timeIndex = 0;
    private String errorDescription = "";

    public Camera(int ID, int frequency, String camera_datas_path, String camera_key) {
        this.id = ID;
        this.frequency = frequency;
        this.status = STATUS.DOWN;
        this.camera_key = camera_key;
    }

    public void initializeDetectedObjects(List<StampedDetectedObjects> stampedDetectedObjects) {
        this.detectedObjects = stampedDetectedObjects;
    }

    public StampedDetectedObjects handleTick(int currentTime) {
        this.time=currentTime;
        if(!checkIfError(currentTime)){
            StampedDetectedObjects stampedObjects=null;
            int numOfDetectedObjects=0;
            if( firstUndetectedIndex < detectedObjects.size() && detectedObjects.get(firstUndetectedIndex).getTime() + frequency <= currentTime) {
                if (detectedObjects.get(firstUndetectedIndex).getDetectedObjects().length>0){
                    stampedObjects = detectedObjects.get(firstUndetectedIndex);
                    numOfDetectedObjects = detectedObjects.get(firstUndetectedIndex).getDetectedObjects().length;
                }
                firstUndetectedIndex++;
            }
            time = currentTime;
            if (stampedObjects!=null && stampedObjects.getDetectedObjects().length> 0) {
                lastDetectedObjects = stampedObjects;
            }
            if (firstUndetectedIndex == detectedObjects.size()) {
                status = STATUS.DOWN;
            }
            StatisticalFolder.getInstance().AddNumDetectedObjects(numOfDetectedObjects);
            return stampedObjects;
        }
    return null;
    }

    @Override
    public String toString() {
        String string = getName() + " frequency: " + frequency + " status: " + status;
        if (detectedObjects != null) {
            string += " detectedobjects: " + detectedObjects.toString();
        }
        return string;
    }

    public String getName() {
        return "camera" + id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean checkIfError(int time) {
        while (timeIndex < detectedObjects.size() && detectedObjects.get(timeIndex).getTime() <= time) {
            if (detectedObjects.get(timeIndex).getTime() == time) {
                for (DetectedObject object : detectedObjects.get(timeIndex).getDetectedObjects()) {
                    if (object.getId().equals("ERROR")) {
                        status = STATUS.ERROR;
                        errorDescription = object.getDescription();
                        return true;
                    }
                }
            }
            timeIndex++;
        }
        return false;
    }

    public String getErrorDescription(){
        return errorDescription;
    }

    public StampedDetectedObjects getLastFrame(){
        return lastDetectedObjects;
    }

    // for tests
    public Camera (int ID, int frequency, String camera_key){
        this.id = ID;
        this.frequency = frequency;
        this.camera_key = camera_key;
        this.status = STATUS.DOWN;
    }

    public List<StampedDetectedObjects> getDetectedObjects() {
        return detectedObjects;
    }

    public void setTimeIndex() {
        this.timeIndex = 0;
    }
}


