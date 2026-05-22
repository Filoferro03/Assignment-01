package pcd.threadVersion;

import pcd.common.controller.AbstractGameController;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ThreadController extends AbstractGameController {

	private final CustomBarrier startFrameBarrier;
    private final CustomBarrier endFrameBarrier;
	private final List<PhysicsWorker> workers;

	public ThreadController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		super(board, view, viewModel, buffer);
		int numWorkers = Runtime.getRuntime().availableProcessors();
		this.workers = new ArrayList<>();
		this.startFrameBarrier = new CustomBarrier(numWorkers + 1, null);
		this.endFrameBarrier = new CustomBarrier(numWorkers + 1, null);
        CustomBarrier movementBarrier = new CustomBarrier(numWorkers, board::buildSpatialGrid);
		for (int i = 0; i < numWorkers; i++) {
			PhysicsWorker worker = new PhysicsWorker(board, i, numWorkers, startFrameBarrier, movementBarrier, endFrameBarrier);
			workers.add(worker);
			worker.start();
		}
	}

	@Override
	protected void executePhysicsStep(long dt) {
		for (PhysicsWorker w : workers) {
			w.updateContext(dt);
		}
		try {
			startFrameBarrier.await();
			endFrameBarrier.await();

		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		board.updateGlobalState(dt);
	}

	@Override
	protected void onGameOver() {
		for (PhysicsWorker w : workers) {
			w.interrupt();
		}
		super.onGameOver();
	}
}