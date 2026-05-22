package pcd.taskVersion;

import pcd.common.controller.AbstractGameController;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskController extends AbstractGameController {

	private final ExecutorService executor;
    private final List<PhysicsTask> physicsTasks;
	private final List<CollisionTask> collisionTasks;

	public TaskController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		super(board, view, viewModel, buffer);
        int numTasks = Runtime.getRuntime().availableProcessors() + 1;
		this.executor = Executors.newFixedThreadPool(numTasks);
		this.physicsTasks = new ArrayList<>(numTasks);
		this.collisionTasks = new ArrayList<>(numTasks);
		for (int i = 0; i < numTasks; i++) {
			physicsTasks.add(new PhysicsTask(i, numTasks, board));
			collisionTasks.add(new CollisionTask(i, numTasks, board));
		}
	}

	@Override
	protected void executePhysicsStep(long dt) {
		try {
			for (PhysicsTask pTask : physicsTasks) {
				pTask.setDt(dt);
			}
			executor.invokeAll(physicsTasks);
			board.buildSpatialGrid();
			executor.invokeAll(collisionTasks);
			board.updateGlobalState(dt);
		} catch (InterruptedException e) {
			e.printStackTrace();
			executor.shutdownNow();
		}
	}

	@Override
	protected void onGameOver() {
		executor.shutdown();
		super.onGameOver();
	}
}