package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Event;

public class LastFrameEvent<T> implements Event<Boolean> {

    private T lastFrame;
    private String sensor;

    public LastFrameEvent(T lastFrame, String sensor) {
        this.lastFrame = lastFrame;
        this.sensor = sensor;
    }

    public T getLastFrame() {
        return lastFrame;
    }
    public String getSensor() {
        return sensor;
    }
}
