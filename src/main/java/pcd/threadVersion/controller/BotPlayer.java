package pcd.threadVersion.controller;

import pcd.threadVersion.util.BoundedBuffer;
import pcd.threadVersion.model.V2d;
import java.util.Random;

public class BotPlayer extends Thread {
    private final BoundedBuffer<Cmd> buffer;
    private final Random rand = new Random();

    public BotPlayer(BoundedBuffer<Cmd> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 + rand.nextInt(1500));

                double forceX = (rand.nextDouble() * 6) - 3;
                double forceY = (rand.nextDouble() * 6) - 3;

                buffer.put(new BotKickCmd(new V2d(forceX, forceY)));

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
