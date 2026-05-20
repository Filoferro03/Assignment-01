package pcd.threadVersion;

public class CustomBarrier {
    private final int totalThreads;
    private int waitingThreads;
    private final Runnable barrierAction;

    public CustomBarrier(int totalThreads, Runnable barrierAction) {
        this.totalThreads = totalThreads;
        this.waitingThreads = totalThreads;
        this.barrierAction = barrierAction;
    }

    public synchronized void await() throws InterruptedException {
        waitingThreads--;
        if (waitingThreads == 0) {
            if (barrierAction != null) {
                barrierAction.run();
            }
            waitingThreads = totalThreads;
            notifyAll();
        } else {
            wait();
        }
    }
}