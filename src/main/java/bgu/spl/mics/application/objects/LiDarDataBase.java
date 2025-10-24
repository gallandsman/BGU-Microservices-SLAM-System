package bgu.spl.mics.application.objects;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private List<StampedCloudPoints> LiDarData;

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private static class LidarDataBaseHolder {
        private static LiDarDataBase dataBase = null;

        private static void initializeData(String filePath) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(filePath)) {
                 Type listType= new TypeToken<List<StampedCloudPoints>>(){}.getType();
                List<StampedCloudPoints> list = gson.fromJson(reader, listType);
                dataBase = new LiDarDataBase(list);
            } catch (IOException e){}
        }
    }
    private LiDarDataBase(List<StampedCloudPoints> LiDarData) {
        this.LiDarData = LiDarData;
    }

    public static LiDarDataBase getInstance(String filePath){
        LidarDataBaseHolder.initializeData(filePath);
        return LidarDataBaseHolder.dataBase;
    }

    public List<StampedCloudPoints> getCloudPointsTime(int time) {
        List<StampedCloudPoints> cloudPoints = new ArrayList<>();
        for (StampedCloudPoints objectCloudPoints : LiDarData) {
            if(objectCloudPoints.getTime() > time)
                break;
            if(objectCloudPoints.getTime() == time)
                cloudPoints.add(objectCloudPoints);
        }
        return cloudPoints;
    }
    public String toString(){
        String s = "";
        for (StampedCloudPoints objectCloudPoints : LiDarData) {
            s += objectCloudPoints.toString();
        }
        return s;
    }
}
