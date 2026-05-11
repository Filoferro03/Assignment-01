package pcd.threadVersion.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomCyclicBarrier {
    private final int totalParties;
    private int waitingThreads = 0;

    private int generation = 0;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Runnable barrierAction;

    public CustomCyclicBarrier(int parties, Runnable barrierAction) {
        this.totalParties = parties;
        this.barrierAction = barrierAction;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            int myGeneration = generation;
            waitingThreads++;

            if (waitingThreads == totalParties) {
                if (barrierAction != null) {
                    barrierAction.run();
                }
                waitingThreads = 0;
                generation++;
                condition.signalAll();
            } else {
                while (myGeneration == generation) {
                    condition.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}