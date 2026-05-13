package pcd.sequentialVersion;

import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

public class SequentialController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;

	public SequentialController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		this.board = board;
		this.view = view;
		this.viewModel = viewModel;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		long lastUpdateTime = System.currentTimeMillis();
		long t0 = System.currentTimeMillis();
		int nFrames = 0;
		while (!board.isGameOver()) {
			Cmd command;
			while ((command = buffer.poll()) != null) {
				command.execute(board);
			}
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastUpdateTime;
			lastUpdateTime = currentTime;
			board.applyMovementsCyclic(0, 1, dt);
			board.buildSpatialGrid();
			board.detectCollisionsCyclic(0, 1);
			board.updateGlobalState(dt);
			nFrames++;
			int framePerSec = 0;
			long elapsedTime = (System.currentTimeMillis() - t0);
			if (elapsedTime > 0) {
				framePerSec = (int)(nFrames * 1000 / elapsedTime);
			}
			viewModel.update(board, framePerSec);
			view.render();
		}
		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
}