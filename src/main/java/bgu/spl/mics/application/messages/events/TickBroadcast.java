package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int currentTime;

    public TickBroadcast(int currentTime) {
        this.currentTime = currentTime;
    }

    public int getCurrentTime(){
        return this.currentTime;
    }

    public String toString() { return "TickBroadcast" ; }
}
