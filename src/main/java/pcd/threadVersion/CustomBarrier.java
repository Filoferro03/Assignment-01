package pcd.threadVersion;

public class CustomBarrier {
    private final int totalThreads;
    private int waitingThreads;
    private final Runnable barrierAction;
    private int generation;

    public CustomBarrier(int totalThreads, Runnable barrierAction) {
        this.totalThreads = totalThreads;
        this.waitingThreads = totalThreads;
        this.barrierAction = barrierAction;
        this.generation = 0;
    }

    public synchronized void await() throws InterruptedException {
        int myGeneration = this.generation;
        waitingThreads--;
        if (waitingThreads == 0) {
            if (barrierAction != null) {
                barrierAction.run();
            }
            waitingThreads = totalThreads;
            generation++;
            notifyAll();
        } else {
            while (myGeneration == this.generation) {
                wait();
            }
        }
    }
}