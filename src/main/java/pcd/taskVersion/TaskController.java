package pcd.taskVersion;

import pcd.common.controller.AbstractGameController;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskController extends AbstractGameController {

	private final int numTasks;
	private final List<PhysicsTask> physicsTasks;
	private final List<CollisionTask> collisionTasks;
	private final BagOfTasks bagOfTasks;
	private final List<WorkerThread> workers;

	public TaskController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		super(board, view, viewModel, buffer);
		this.numTasks = Runtime.getRuntime().availableProcessors() + 1;
		this.physicsTasks = new ArrayList<>(numTasks);
		this.collisionTasks = new ArrayList<>(numTasks);
		this.bagOfTasks = new BagOfTasks();
		this.workers = new ArrayList<>(numTasks);

		for (int i = 0; i < numTasks; i++) {
			physicsTasks.add(new PhysicsTask(i, numTasks, board));
			collisionTasks.add(new CollisionTask(i, numTasks, board));
		}

		for (int i = 0; i < numTasks; i++) {
			WorkerThread worker = new WorkerThread(bagOfTasks);
			workers.add(worker);
			worker.start();
		}
	}

	@Override
	protected void executePhysicsStep(long dt) {
		try {
			CustomLatch moveLatch = new CustomLatch(numTasks);
			for (PhysicsTask pTask : physicsTasks) {
				pTask.setDt(dt);
				pTask.setLatch(moveLatch);
				bagOfTasks.addTask(pTask);
			}
			moveLatch.await();
			board.buildSpatialGrid();
			CustomLatch collLatch = new CustomLatch(numTasks);
			for (CollisionTask cTask : collisionTasks) {
				cTask.setLatch(collLatch);
				bagOfTasks.addTask(cTask);
			}
			collLatch.await();
			board.updateGlobalState(dt);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	protected void onGameOver() {
		bagOfTasks.shutdown();
		super.onGameOver();
	}
}