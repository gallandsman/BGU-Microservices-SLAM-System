package bgu.spl.mics.application.objects;


/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
   private int time;
   private DetectedObject[] detectedObjects;

    public StampedDetectedObjects() {
    }
    public StampedDetectedObjects(int time, DetectedObject[] detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }
    public int getTime() {return time;}

    public DetectedObject[] getDetectedObjects() {return detectedObjects;}

    @Override
    public String toString() {
        String str = "Time: "+ time;
        for (DetectedObject obj : detectedObjects) {
            str = str + "\n " + obj.toString();
        }
        return str;
    }
}


