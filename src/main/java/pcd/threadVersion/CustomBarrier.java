package pcd.threadVersion;

public class CustomBarrier {
    private final int totalThreads;
    private int waitingThreads;
    private final Runnable barrierAction;
    private int generation;

    private boolean broken;

    public CustomBarrier(int totalThreads, Runnable barrierAction) {
        this.totalThreads = totalThreads;
        this.waitingThreads = totalThreads;
        this.barrierAction = barrierAction;
        this.generation = 0;
        this.broken = false;
    }

    public synchronized void await() throws InterruptedException {
        if (broken) {
            throw new InterruptedException("Barrier is broken");
        }
        int myGeneration = this.generation;
        waitingThreads--;
        if (waitingThreads == 0) {
            if (barrierAction != null) {
                try {
                    barrierAction.run();
                } catch (Exception e) {
                    broken = true;
                    notifyAll();
                    throw e;
                }
            }
            waitingThreads = totalThreads;
            generation++;
            notifyAll();
        } else {
            while (myGeneration == this.generation && !broken) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    broken = true;
                    notifyAll();
                    throw e;
                }
            }
            if (broken) {
                throw new InterruptedException("Barrier broken by another thread");
            }
        }
    }
}