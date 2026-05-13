package pcd.threadVersion;

import pcd.common.model.Board;

public class PhysicsWorker extends Thread {
    private final Board board;
    private final int myId;
    private final int totalWorkers;
    private final MasterWorkerMonitor masterMonitor;

    private long currentDt;
    private volatile boolean isRunning = true;

    // Rimosso CustomCyclicBarrier dal costruttore
    public PhysicsWorker(Board board, int myId, int totalWorkers, MasterWorkerMonitor masterMonitor) {
        this.board = board;
        this.myId = myId;
        this.totalWorkers = totalWorkers;
        this.masterMonitor = masterMonitor;
    }

    public void updateContext(long dt) {
        this.currentDt = dt;
    }

    @Override
    public void run() {
        int generation = 1;
        while (isRunning) {
            try {
                // Fase 1: Calcolo dei movimenti
                masterMonitor.waitForStart(generation, 1);
                if (!isRunning) break;
                board.applyMovementsCyclic(myId, totalWorkers, currentDt);
                masterMonitor.workerDone();

                // Fase 2: Calcolo delle collisioni
                masterMonitor.waitForStart(generation, 2);
                if (!isRunning) break;
                board.detectCollisionsCyclic(myId, totalWorkers);
                masterMonitor.workerDone();

                generation++;
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }
}