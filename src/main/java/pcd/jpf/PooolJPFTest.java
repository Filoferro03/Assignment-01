package pcd.jpf;
public class PooolJPFTest {
    static class MockBall {
        private final int id;
        public MockBall(int id) { this.id = id; }
        public int getId() { return id; }
        public static void resolveCollision(MockBall b1, MockBall b2) {
            MockBall first = (b1.getId() < b2.getId()) ? b1 : b2;
            MockBall second = (b1.getId() < b2.getId()) ? b2 : b1;
            synchronized (first) {
                synchronized (second) {
                    int dummy = first.getId() + second.getId(); 
                }
            }
        }
    }
    public static void main(String[] args) {
        MockBall b1 = new MockBall(1);
        MockBall b2 = new MockBall(2);
        Thread t1 = new Thread(() -> MockBall.resolveCollision(b1, b2));
        Thread t2 = new Thread(() -> MockBall.resolveCollision(b2, b1));
        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) {}
    }
}
