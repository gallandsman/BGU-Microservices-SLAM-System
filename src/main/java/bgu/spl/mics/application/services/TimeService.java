package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.events.TerminatedBroadcast;
import bgu.spl.mics.application.messages.events.TickBroadcast;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StatisticalFolder;
import static java.lang.Thread.sleep;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int Duration;
    private int TickTime;
    private int currentTick = 0;
    private StatisticalFolder statisticalFolder;
    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        statisticalFolder= StatisticalFolder.getInstance();

        while (currentTick < Duration & statisticalFolder.getSystemStatus()!= STATUS.DOWN) {
            try {
                sleep ((long)(TickTime*1000));
            } catch (InterruptedException e) {}
            currentTick++;
            sendBroadcast(new TickBroadcast(currentTick));
            statisticalFolder.AddSystemRuntime();
        }

        sendBroadcast(new TerminatedBroadcast(this.getName(), "TimeService"));
        terminate();
    }
}
