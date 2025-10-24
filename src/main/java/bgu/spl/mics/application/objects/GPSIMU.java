package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS Status;
    private List<Pose> PoseList;

    public GPSIMU(List<Pose> PoseList) {
        this.currentTick = 0;
        this.Status = STATUS.DOWN;
        this.PoseList = PoseList;
    }

    public Pose handleTick(int currentTick) {
        this.currentTick = currentTick;
        if (currentTick < PoseList.size())
            return PoseList.get(currentTick - 1);
        if (currentTick == PoseList.size()) {
            Status = STATUS.DOWN;
            return PoseList.get(currentTick - 1);
        }
        return null;
    }

    @Override
    public String toString() {
        String string = "GPSIMU: " +
                "currentTick " + currentTick +
                ", status: " + Status +
                ", PoseList:";
        for (Pose pose : PoseList)
            string += pose.toString();
        return string;
    }

    public void setStatus(STATUS status) {
        this.Status = status;
    }

    public STATUS getStatus() {
        return Status;
    }
}