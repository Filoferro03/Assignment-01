package pcd.threadVersion.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterWorkerMonitor {
    private final int numWorkers;
    private int workersFinished = 0;
    private int currentFrame = 0;

    private final Lock mutex = new ReentrantLock();
    private final Condition startCond = mutex.newCondition();
    private final Condition doneCond = mutex.newCondition();

    public MasterWorkerMonitor(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    public void startWorkers() {
        mutex.lock();
        try {
            currentFrame++;
            workersFinished = 0;
            startCond.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    public void waitForAll() throws InterruptedException {
        mutex.lock();
        try {
            while (workersFinished < numWorkers) {
                doneCond.await();
            }
        } finally {
            mutex.unlock();
        }
    }

    public void waitForStart(int expectedFrame) throws InterruptedException {
        mutex.lock();
        try {
            while (currentFrame < expectedFrame) {
                startCond.await();
            }
        } finally {
            mutex.unlock();
        }
    }

    public void workerDone() {
        mutex.lock();
        try {
            workersFinished++;
            if (workersFinished == numWorkers) {
                doneCond.signal();
            }
        } finally {
            mutex.unlock();
        }
    }
}