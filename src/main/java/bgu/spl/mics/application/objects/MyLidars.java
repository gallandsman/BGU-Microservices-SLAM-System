package bgu.spl.mics.application.objects;

public class MyLidars {
    LiDarWorkerTracker[] LidarConfigurations;
    String lidars_data_path;

    public LiDarWorkerTracker[] getLidarsList() { return LidarConfigurations;}

    public String getLidarDatasPath() {
        return lidars_data_path;
    }

    public String toString(){
        String string = "";
        for(LiDarWorkerTracker l : LidarConfigurations){
            string += l.toString();
        }
        return string+ "path "+ lidars_data_path +"\n";
    }

}
