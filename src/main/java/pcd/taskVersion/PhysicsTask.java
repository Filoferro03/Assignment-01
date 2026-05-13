package pcd.taskVersion;

import pcd.common.model.Board;
import java.util.concurrent.Callable;

public class PhysicsTask implements Callable<Void> {

    private final int taskId;
    private final int totalTasks;
    private final long dt;
    private final Board board;

    public PhysicsTask(int taskId, int totalTasks, Board board,  long dt) {
        this.taskId = taskId;
        this.totalTasks = totalTasks;
        this.board = board;
        this.dt = dt;
    }


    @Override
    public Void call() {
        board.applyMovementsCyclic(taskId, totalTasks, dt);
        return null;
    }
}