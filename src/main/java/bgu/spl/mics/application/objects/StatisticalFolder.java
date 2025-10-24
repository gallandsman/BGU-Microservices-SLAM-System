package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private int systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private int numLandmarks;
    private String faultySensor;
    private String error;
    private int CrashedTime;
    private volatile STATUS systemStatus;

    private StatisticalFolder() {
        this.systemRuntime = 0;
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = 0;
        systemStatus= STATUS.DOWN;
    }

    private static class StatisticalFolderHolder {
        private static final StatisticalFolder INSTANCE = new StatisticalFolder();
    }

    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.INSTANCE;
    }

    public void AddSystemRuntime() {
        systemRuntime++;
    }

    public void AddNumDetectedObjects(int amount) {
        numDetectedObjects.addAndGet(amount);
    }

    public void AddNumTrackedObjects(int amount) {
        numTrackedObjects.addAndGet(amount);
    }

    public void AddNumLandmarks() {
        numLandmarks ++;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void setCrashedTime(int crashedTime) {CrashedTime = crashedTime;}

    public int getCrashedTime() {
        return CrashedTime;
    }

    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public int getSystemRuntime() {
        return systemRuntime;
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks;
    }

    public void setStatus(STATUS status) { this.systemStatus = status; }

    public STATUS getSystemStatus() { return systemStatus; }

}