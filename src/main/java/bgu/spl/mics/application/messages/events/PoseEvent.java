package bgu.spl.mics.application.messages.events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;


public class PoseEvent implements Event<Boolean> {
    private Pose currentPose;

    public PoseEvent(Pose currentPose) {
        this.currentPose = currentPose;
    }
    public Pose getCurrentPose() {
        return currentPose;
    }

    @Override
    public String toString() {
        return "PoseEvent:" + currentPose.toString();
    }

}
