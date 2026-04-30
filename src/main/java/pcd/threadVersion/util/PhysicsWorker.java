package pcd.threadVersion.util;

import pcd.threadVersion.model.Board;


public class PhysicsWorker extends Thread {
    private final Board board;
    private final int myId;
    private final int totalWorkers;
    private final MasterWorkerMonitor masterMonitor;
    private final CustomCyclicBarrier internalBarrier;

    private long currentDt;
    private volatile boolean isRunning = true;

    public PhysicsWorker(Board board, int myId, int totalWorkers,
                         MasterWorkerMonitor masterMonitor, CustomCyclicBarrier internalBarrier) {
        this.board = board;
        this.myId = myId;
        this.totalWorkers = totalWorkers;
        this.masterMonitor = masterMonitor;
        this.internalBarrier = internalBarrier;
    }

    public void updateContext(long dt) {
        this.currentDt = dt;
    }

    @Override
    public void run() {
        int frame = 1;
        while (isRunning) {
            try {
                masterMonitor.waitForStart(frame);
                if (!isRunning) break;
                board.detectCollisionsCyclic(myId, totalWorkers);
                internalBarrier.await();
                board.applyMovementsCyclic(myId, totalWorkers, currentDt);
                masterMonitor.workerDone();
                frame++;
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }
}