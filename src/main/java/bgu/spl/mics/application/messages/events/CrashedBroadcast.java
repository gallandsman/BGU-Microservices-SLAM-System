package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Broadcast;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CrashedBroadcast implements Broadcast {
   private String sensorId;
    private String description;
    private int crashedTime;

    public CrashedBroadcast(String sensorId, String description, int crashedTime) {
        this.sensorId = sensorId;
        this.description = description;
        this.crashedTime = crashedTime;
    }

    public String getSensorId() {
        return sensorId;
    }
    public String getDescription() {
        return description;
    }
    public int getCrashedTime() {
        return crashedTime;
    }

    public String toString() {return "CrashedBroadcast sensorId:" + sensorId + ", description:" + description + ", crashedTime:" + crashedTime + "\n";}
}
