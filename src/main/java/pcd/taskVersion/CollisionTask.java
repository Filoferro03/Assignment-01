package pcd.taskVersion;

import pcd.common.model.Board;

import java.util.concurrent.Callable;

public class CollisionTask implements Callable<Void> {

    private final int taskId;
    private final int totalTasks;
    private final Board board;

    public CollisionTask(int taskId, int totalTasks, Board board) {
        this.taskId = taskId;
        this.totalTasks = totalTasks;
        this.board = board;
    }

    @Override
    public Void call() {
        board.detectCollisionsCyclic(taskId, totalTasks);
        return null;
    }
}