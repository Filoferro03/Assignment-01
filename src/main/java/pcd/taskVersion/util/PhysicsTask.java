package pcd.taskVersion.util;

import pcd.taskVersion.model.Board;
import java.util.concurrent.Callable;

public class PhysicsTask implements Callable<Void> {

    private final int taskId;
    private final int totalTasks;
    private long dt;
    private final Board board;

    public PhysicsTask(int taskId, int totalTasks, Board board) {
        this.taskId = taskId;
        this.totalTasks = totalTasks;
        this.board = board;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    @Override
    public Void call() {
        board.applyMovementsCyclic(taskId, totalTasks, dt);
        return null;
    }
}