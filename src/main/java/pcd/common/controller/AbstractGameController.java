package pcd.common.controller;

import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

public abstract class AbstractGameController extends Thread {

    protected final Board board;
    protected final View view;
    protected final ViewModel viewModel;
    protected final BoundedBuffer<Cmd> buffer;

    public AbstractGameController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
        this.board = board;
        this.view = view;
        this.viewModel = viewModel;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int nFrames = 0;
        long t0 = System.currentTimeMillis();
        long lastUpdateTime = System.currentTimeMillis();

        while (!board.isGameOver()) {
            Cmd command;
            while ((command = buffer.poll()) != null) {
                command.execute(board);
            }
            long elapsed = System.currentTimeMillis() - lastUpdateTime;
            lastUpdateTime = System.currentTimeMillis();
            executePhysicsStep(elapsed);
            nFrames++;
            int framePerSec = 0;
            long dt = (System.currentTimeMillis() - t0);
            if (dt > 0) {
                framePerSec = (int)(nFrames * 1000 / dt);
            }
            viewModel.update(board, framePerSec);
            view.render();
        }
        onGameOver();
    }

    protected abstract void executePhysicsStep(long dt);

    protected void onGameOver() {
        System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
    }
}