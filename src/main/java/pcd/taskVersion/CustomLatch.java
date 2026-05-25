package pcd.taskVersion;

public class CustomLatch {
    private int count;

    public CustomLatch(int count) {
        this.count = count;
    }

    public synchronized void countDown() {
        count--;
        if (count == 0) {
            notifyAll();
        }
    }

    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait();
        }
    }
}