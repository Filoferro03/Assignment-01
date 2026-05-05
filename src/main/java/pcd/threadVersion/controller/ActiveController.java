package pcd.threadVersion.controller;

import pcd.threadVersion.util.*;
import pcd.threadVersion.model.Board;
import pcd.threadVersion.view.View;
import pcd.threadVersion.view.ViewModel;

import java.util.List;

public class ActiveController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private final BoundedBuffer<Cmd> buffer;
	private final MasterWorkerMonitor masterMonitor;
	private final List<PhysicsWorker> workers;

	public  ActiveController(Board board, View view, ViewModel viewModel,
							 BoundedBuffer<Cmd> buffer,  MasterWorkerMonitor masterMonitor,
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
			masterMonitor.startWorkers();

			try {
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
		}

		for (PhysicsWorker w : workers) {
			w.interrupt();
		}

		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
}
