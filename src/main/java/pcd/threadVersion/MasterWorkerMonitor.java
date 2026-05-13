package pcd.threadVersion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterWorkerMonitor {
    private final int numWorkers;
    private int workersFinished = 0;

    // Sostituiamo currentFrame con generazione e fase
    private int currentGeneration = 0;
    private int currentPhase = 0;

    private final Lock mutex = new ReentrantLock();
    private final Condition startCond = mutex.newCondition();
    private final Condition doneCond = mutex.newCondition();

    public MasterWorkerMonitor(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    // Il master avvia una specifica fase
    public void startPhase(int generation, int phase) {
        mutex.lock();
        try {
            this.currentGeneration = generation;
            this.currentPhase = phase;
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

    // I worker aspettano la loro fase esatta
    public void waitForStart(int expectedGeneration, int expectedPhase) throws InterruptedException {
        mutex.lock();
        try {
            while (currentGeneration != expectedGeneration || currentPhase != expectedPhase) {
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