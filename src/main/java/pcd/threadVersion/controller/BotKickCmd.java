package pcd.threadVersion.controller;

import pcd.threadVersion.model.Board;
import pcd.threadVersion.model.V2d;

public class BotKickCmd implements Cmd{
    private final V2d impulse;

    public BotKickCmd(V2d impulse) {
        this.impulse = impulse;
    }

    public V2d getImpulse() {
        return impulse;
    }

    @Override
    public void execute(Board board) {
        return;
    }
}
