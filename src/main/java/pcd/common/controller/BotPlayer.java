package pcd.common.controller;

import pcd.common.model.Ball;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.model.V2d;
import java.util.Random;

public class BotPlayer extends Thread {
    private final BoundedBuffer<Cmd> buffer;
    private final Board board;
    private final Random rand = new Random(2);

    public BotPlayer(BoundedBuffer<Cmd> buffer,  Board board) {
        this.buffer = buffer;
        this.board = board;
    }

    @Override
    public void run() {
        long lastKickTime = System.currentTimeMillis();

        while (!board.isGameOver()) {
            try {
                Ball bb = board.getBotBall();
                if (bb != null) {
                    if (bb.getVel().abs() < 0.05 && (System.currentTimeMillis() - lastKickTime > 2000)) {
                        double angle = rand.nextDouble() * Math.PI * 0.25;
                        V2d v = new V2d(Math.cos(angle), Math.sin(angle)).mul(1.5);
                        buffer.put(new BotKickCmd(v));
                        lastKickTime = System.currentTimeMillis();
                    }
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

