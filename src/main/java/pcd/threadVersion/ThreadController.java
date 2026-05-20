package pcd.threadVersion;

import pcd.common.controller.Cmd;
import pcd.common.util.BoundedBuffer;
import pcd.common.model.Board;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ThreadController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;

	private final CustomBarrier startFrameBarrier;
	private final CustomBarrier movementBarrier;
	private final CustomBarrier endFrameBarrier;
	private final List<PhysicsWorker> workers;

	public ThreadController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		this.board = board;
		this.view = view;
		this.viewModel = viewModel;
		this.buffer = buffer;

		int numWorkers = Runtime.getRuntime().availableProcessors();
		this.workers = new ArrayList<>();
		this.startFrameBarrier = new CustomBarrier(numWorkers + 1, null);
		this.endFrameBarrier = new CustomBarrier(numWorkers + 1, null);
		this.movementBarrier = new CustomBarrier(numWorkers, () -> {
			board.buildSpatialGrid();
		});
		for (int i = 0; i < numWorkers; i++) {
			PhysicsWorker worker = new PhysicsWorker(board, i, numWorkers, startFrameBarrier, movementBarrier, endFrameBarrier);
			workers.add(worker);
			worker.start();
		}
	}

	public void run() {
		int nFrames = 0;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();
		long accumulatedPhysicsTime = 0;
		while (!board.isGameOver()) {
			Cmd command;
			while ((command = buffer.poll()) != null) {
				command.execute(board);
			}
			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			if (elapsed <= 0) elapsed = 1;
			lastUpdateTime = System.currentTimeMillis();
			for (PhysicsWorker w : workers) {
				w.updateContext(elapsed);
			}
			long tPhysStart = System.currentTimeMillis();
			try {
				startFrameBarrier.await();
				endFrameBarrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			board.updateGlobalState(elapsed);
			long tPhysEnd = System.currentTimeMillis();
			accumulatedPhysicsTime += (tPhysEnd - tPhysStart);
			nFrames++;
			if (nFrames % 100 == 0) {
				long avgPhysicsTime = accumulatedPhysicsTime / 100;
				System.out.println("[THR] Media tempo Fisica (ultimi 100 frame): " + avgPhysicsTime + " ms");
				accumulatedPhysicsTime = 0;
			}
			int framePerSec = 0;
			long dt = (System.currentTimeMillis() - t0);
			if (dt > 0) {
				framePerSec = (int)(nFrames * 1000 / dt);
			}
			viewModel.update(board, framePerSec);
			view.render();
		}
		for (PhysicsWorker w : workers) {
			w.interrupt();
		}
		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
}
