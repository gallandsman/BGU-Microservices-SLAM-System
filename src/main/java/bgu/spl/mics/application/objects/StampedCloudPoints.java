package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private int time;
    private String id;
    private List<List<Double>> cloudPoints;

    public StampedCloudPoints(String id, int time) {
        this.id= id;
        this.time= time;
    }
    public int getTime() {return time;}
    public String getId() {return id;}
    public List<List<Double>> getCloudPoints() {return cloudPoints;}
    public String toString() {return "StampedCloudPoints id:" + id + ", time:" + time+'\n'+
    "CloudPoints: "+cloudPoints.toString();}

}
