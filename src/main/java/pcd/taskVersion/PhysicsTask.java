package pcd.taskVersion;

import pcd.common.model.Board;

public class PhysicsTask implements Runnable {
    private final int taskId;
    private final int totalTasks;
    private long dt;
    private final Board board;
    private CustomLatch latch;

    public PhysicsTask(int taskId, int totalTasks, Board board) {
        this.taskId = taskId;
        this.totalTasks = totalTasks;
        this.board = board;
    }

    public void setDt(long dt) { this.dt = dt; }
    public void setLatch(CustomLatch latch) { this.latch = latch; }

    @Override
    public void run() {
        board.applyMovementsCyclic(taskId, totalTasks, dt);
        if (latch != null) {
            latch.countDown();
        }
    }
}