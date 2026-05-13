package pcd.threadVersion;

import pcd.common.controller.Cmd;
import pcd.common.util.BoundedBuffer;
import pcd.common.model.Board;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

import java.util.List;

public class ThreadController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;
	private final MasterWorkerMonitor masterMonitor;
	private final List<PhysicsWorker> workers;

	public ThreadController(Board board, View view, ViewModel viewModel,
	                        BoundedBuffer<Cmd> buffer, MasterWorkerMonitor masterMonitor,
	                        List<PhysicsWorker> workers) {
		this.board = board;
		this.view = view;
		this.viewModel = viewModel;
		this.buffer = buffer;
		this.masterMonitor = masterMonitor;
		this.workers = workers;
	}

	public void run() {
		int nFrames = 0;
		int generation = 1;
		long t0 = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();

		while (!board.isGameOver()) {
			Cmd command;
			while ((command = buffer.poll()) != null) {
				command.execute(board);
			}

			long elapsed = System.currentTimeMillis() - lastUpdateTime;
			lastUpdateTime = System.currentTimeMillis();

			for (PhysicsWorker w : workers) {
				w.updateContext(elapsed);
			}

			try {
				masterMonitor.startPhase(generation, 1);
				masterMonitor.waitForAll();
				board.buildSpatialGrid();
				masterMonitor.startPhase(generation, 2);
				masterMonitor.waitForAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			board.updateGlobalState(elapsed);
			nFrames++;
			int framePerSec = 0;
			long dt = (System.currentTimeMillis() - t0);
			if (dt > 0) {
				framePerSec = (int)(nFrames * 1000 / dt);
			}
			viewModel.update(board, framePerSec);
			view.render();
			generation++;
		}
		for (PhysicsWorker w : workers) {
			w.interrupt();
		}
		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
}
