package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private String senderId;
    private String sensorType;

    public TerminatedBroadcast(String senderId, String sensorType) {
        this.senderId = senderId;
        this.sensorType = sensorType;
    }

    public String toString() { return "TerminatedBroadcast"; }

    public String getSensorType() { return sensorType; }
}
