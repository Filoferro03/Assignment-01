package pcd.taskVersion;

public class WorkerThread extends Thread {
    private final BagOfTasks bag;

    public WorkerThread(BagOfTasks bag) {
        this.bag = bag;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Runnable task = bag.getTask();
                if (task == null) {
                    break;
                }
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}