package pcd.taskVersion;

import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;

	private final ExecutorService executor;
	private final int numTasks;

	public TaskController(Board board, View view, ViewModel viewModel,
	                      BoundedBuffer<Cmd> buffer) {
		this.board = board;
		this.view = view;
		this.viewModel = viewModel;
		this.buffer = buffer;
		this.numTasks = Runtime.getRuntime().availableProcessors() + 1;
		this.executor = Executors.newFixedThreadPool(numTasks);
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
			try {
				List<Callable<Void>> physicsTasks = new ArrayList<>();
				for (int i = 0; i < numTasks; i++) {
					physicsTasks.add(new PhysicsTask(i, numTasks, board, dt));
				}
				executor.invokeAll(physicsTasks);
				board.buildSpatialGrid();
				List<Callable<Void>> collisionTasks = new ArrayList<>();
				for (int i = 0; i < numTasks; i++) {
					collisionTasks.add(new CollisionTask(i, numTasks, board));
				}
				executor.invokeAll(collisionTasks);
			} catch (InterruptedException e) {
				e.printStackTrace();
				executor.shutdownNow();
				break;
			}
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
		executor.shutdown();
	}
}
