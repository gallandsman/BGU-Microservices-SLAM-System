package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.MyCameras;
import bgu.spl.mics.application.objects.MyLidars;
public class ConfigData {
       private MyCameras Cameras;
       private MyLidars LiDarWorkers;
       private String poseJsonFile;
       private int TickTime;
       private int Duration;

      public ConfigData(){}

      public String getCameraDatasPath() {
            return Cameras.getCameraDatasPath();
       }

       public String getPoseJsonFile() {
            return poseJsonFile;
        }

    public int getTickTime() {return TickTime;}

    public int getDuration(){ return Duration; }

    @Override
    public String toString() {
        return "GurionRockConfiguration " +"\n" +
                Cameras.toString()+'\n' +
                " "+ LiDarWorkers.toString()+'\n' +
                " poseJsonFile: '" + poseJsonFile + '\n' +
                " tickTime: " + TickTime +
                " duration: " + Duration;
    }

    public Camera[] getCameras() { return Cameras.getCamerasList();}

    public LiDarWorkerTracker[] getLiDars() { return LiDarWorkers.getLidarsList();}

    public String getLidarDatasPath() {
          return LiDarWorkers.getLidarDatasPath();
    }
}
