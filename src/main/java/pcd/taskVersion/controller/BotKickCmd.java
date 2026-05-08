package pcd.taskVersion.controller;

import pcd.taskVersion.model.Board;
import pcd.taskVersion.model.V2d;

public class BotKickCmd implements Cmd {
    private final V2d impulse;

    public BotKickCmd(V2d impulse) {
        this.impulse = impulse;
    }

    public V2d getImpulse() {
        return impulse;
    }

    @Override
    public void execute(Board board) {
        board.hitBotBall(impulse);
    }
}
