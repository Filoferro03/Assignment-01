package pcd.jpf;

import pcd.common.util.BoundedBufferImpl;

public class BoundedBufferJPFTest {
    public static void main(String[] args) {
        final BoundedBufferImpl<Integer> buffer = new BoundedBufferImpl<>(2);

        Thread producer = new Thread(() -> {
            try {
                buffer.put(1);
                buffer.put(2);
                buffer.put(3);
            } catch (InterruptedException e) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                buffer.get();
                buffer.get();
                buffer.get();
            } catch (InterruptedException e) {}
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {}
    }
}