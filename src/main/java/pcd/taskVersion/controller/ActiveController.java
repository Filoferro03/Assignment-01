package pcd.taskVersion.controller;

import pcd.taskVersion.model.Board;
import pcd.taskVersion.util.BoundedBuffer;
import pcd.taskVersion.util.PhysicsTask;
import pcd.taskVersion.view.View;
import pcd.taskVersion.view.ViewModel;
import pcd.taskVersion.util.CollisionTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActiveController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;

	private final ExecutorService executor;
	private final int numTasks;

	public  ActiveController(Board board, View view, ViewModel viewModel,
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
		List<PhysicsTask> physicsTasks = new ArrayList<>();
		List<CollisionTask> collisionTasks = new ArrayList<>();
		for (int i = 0; i < numTasks; i++) {
			physicsTasks.add(new PhysicsTask(i, numTasks, board));
			collisionTasks.add(new CollisionTask(i, numTasks, board));
		}
		while (!board.isGameOver()) {
			Cmd command;
			while ((command = buffer.poll()) != null) {
				command.execute(board);
			}
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastUpdateTime;
			lastUpdateTime = currentTime;
			for (PhysicsTask pTask : physicsTasks) {
				pTask.setDt(dt);
			}
			try {
				executor.invokeAll(physicsTasks);
				board.buildSpatialGrid();
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
