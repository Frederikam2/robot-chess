package com.frederikam.robotchess.mech;

public class NanosecondExecutor {

    private final Runnable runnable;
    private final int times;
    private final long interval; // Nanos
    private boolean stopped = false;

    public NanosecondExecutor(Runnable runnable, int times, long interval) {
        this.runnable = runnable;
        this.times = times;
        this.interval = interval;
    }

    public void stop() {
        stopped = true;
    }

    public void run() throws InterruptedException {
        long startTime = System.nanoTime();

        for (int i = 0; i < times; i++) {
            runnable.run();
            if (stopped) return;
            long sleepEndTime = startTime + interval * i;
            long diff = sleepEndTime - System.nanoTime();
            if (diff < 0) continue;
            Thread.sleep(diff / 1000000, (int) (diff % 1000000));
        }
    }
}
