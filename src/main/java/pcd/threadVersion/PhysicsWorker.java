package pcd.threadVersion;

import pcd.common.model.Board;

public class PhysicsWorker extends Thread {
    private final Board board;
    private final int myId;
    private final int totalWorkers;
    private final CustomBarrier startFrameBarrier;
    private final CustomBarrier movementBarrier;
    private final CustomBarrier endFrameBarrier;

    private long currentDt;
    private volatile boolean isRunning = true;

    public PhysicsWorker(Board board, int myId, int totalWorkers,
                         CustomBarrier startFrameBarrier,
                         CustomBarrier movementBarrier,
                         CustomBarrier endFrameBarrier) {
        this.board = board;
        this.myId = myId;
        this.totalWorkers = totalWorkers;
        this.startFrameBarrier = startFrameBarrier;
        this.movementBarrier = movementBarrier;
        this.endFrameBarrier = endFrameBarrier;
    }

    public void updateContext(long dt) {
        this.currentDt = dt;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                startFrameBarrier.await();
                if (!isRunning) break;
                board.applyMovementsCyclic(myId, totalWorkers, currentDt);
                movementBarrier.await();
                board.detectCollisionsCyclic(myId, totalWorkers);
                endFrameBarrier.await();
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
    }
}