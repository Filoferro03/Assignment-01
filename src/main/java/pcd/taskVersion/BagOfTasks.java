package pcd.taskVersion;

import java.util.LinkedList;
import java.util.Queue;

public class BagOfTasks {
    private final Queue<Runnable> tasks = new LinkedList<>();
    private boolean isShutdown = false;

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
        notifyAll();
    }

    public synchronized Runnable getTask() throws InterruptedException {
        while (tasks.isEmpty() && !isShutdown) {
            wait();
        }
        if (isShutdown && tasks.isEmpty()) {
            return null;
        }
        return tasks.poll();
    }

    public synchronized void shutdown() {
        isShutdown = true;
        notifyAll();
    }
}