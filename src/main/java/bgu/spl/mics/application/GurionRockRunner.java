package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import  java.lang.reflect.Type;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    public static ConfigData parseConfig(String filePath) {
        ConfigData runner = null;
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            runner = gson.fromJson(reader, ConfigData.class);
        } catch (IOException e) {
            System.out.println("Couldn't read config file: " + filePath);
        }
        return runner;
    }

    public static Map<String, List<StampedDetectedObjects>> parseCamera(String filePath) {
        Map<String, List<StampedDetectedObjects>> runner = null;
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {
            }.getType();
            runner = gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Couldn't read config file: " + filePath);
        }
        return runner;
    }

    public static List<Pose> parsePose(String filePath) {
        List<Pose> runner = null;
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Pose>>() {
            }.getType();
            runner = gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Couldn't read config file: " + filePath);
        }
        return runner;
    }


    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        // ConfigData data = parseConfig(args[0]);
        ConfigData data = parseConfig("configuration_file.json");
        Map<String, List<StampedDetectedObjects>> camerasDetection = parseCamera(data.getCameraDatasPath());
        int num_of_services = data.getCameras().length + data.getLiDars().length + 2;
        CountDownLatch latch = new CountDownLatch(num_of_services); // Time service will wait for every one to subscribe

        // initialize microservices
        Map<MicroService, Thread> microServices = new java.util.HashMap<>();

        //initialize cameraservices and camera's data
        Camera[] cameras = data.getCameras();
        for (Camera camera : cameras) {
            camera.initializeDetectedObjects(camerasDetection.get(camera.getName()));
            CameraService cameraService = new CameraService(camera, latch);
            microServices.put(cameraService, new Thread(cameraService, camera.getName()));
        }

        //initialize liDarservices and set database for Lidarworkers
        LiDarWorkerTracker[] lidars = data.getLiDars();
        LiDarDataBase liDarDataBase = LiDarDataBase.getInstance(data.getLidarDatasPath());
        //System.out.println(liDarDataBase.toString());
        for (LiDarWorkerTracker lidar : lidars) {
            lidar.initializeDataBase(liDarDataBase);
            lidar.setNumOfCameras(data.getCameras().length);
            LiDarService lidarService = new LiDarService(lidar, latch);
            microServices.put(lidarService, new Thread(lidarService, lidar.getName()));
        }
        // initialize poseservice and pose data
        GPSIMU gpsimu = new GPSIMU(parsePose(data.getPoseJsonFile()));
        PoseService poseService = new PoseService(gpsimu, latch);
        microServices.put(poseService, new Thread(poseService, "poseService"));

        // initialize FusionSlam and FusionSlamServices
        FusionSlam fusionSlam = FusionSlam.getInstance();
        fusionSlam.setNumOfCamerasAndLiDars(microServices.size()-1); // excludes poseservice
        FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam, latch);
        microServices.put(fusionSlamService, new Thread(fusionSlamService, "fusionSlamService"));


        // start
        for (MicroService microService : microServices.keySet()) {
            microServices.get(microService).start();
        }

        // initialize timeService
        TimeService timeService = new TimeService(data.getTickTime(), data.getDuration());
                    Thread timeServiceThread = new Thread(timeService, "timeService");
                    microServices.put(timeService, timeServiceThread);
        try {
            latch.await(); // latch.countDown
        } catch (InterruptedException e) {}

            timeServiceThread.start();

        // wait for all to finish
        try {
            for (MicroService microService: microServices.keySet()) {
                microServices.get(microService).join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



