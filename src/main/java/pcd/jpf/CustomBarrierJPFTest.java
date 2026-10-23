package pcd.jpf;

import pcd.threadVersion.CustomBarrier;

public class CustomBarrierJPFTest {
    public static void main(String[] args) {
        final CustomBarrier barrier = new CustomBarrier(3, null);
        
        Runnable task = () -> {
            try {
                barrier.await();
                barrier.await(); 
            } catch (InterruptedException e) {}
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);
        
        t1.start(); 
        t2.start(); 
        t3.start();
        
        try {
            t1.join(); 
            t2.join(); 
            t3.join();
        } catch (InterruptedException e) {}
    }
}