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
		long t0 = System.currentTimeMillis();
		long lastTime = System.currentTimeMillis();
		int nFrames = 0;
		while (!board.isGameOver()) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastTime;
			Cmd command;
			while ((command = buffer.poll()) != null) {
				command.execute(board);
			}
			for (PhysicsWorker w : workers) {
				w.updateContext(dt);
			}
			masterMonitor.startWorkers();
			try {
				masterMonitor.waitForAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			board.updateGlobalState(dt);
			nFrames++;
			long elapsedTotal = System.currentTimeMillis() - t0;
			int currentFps = (elapsedTotal > 0) ? (int) (nFrames * 1000 / elapsedTotal) : 0;
			viewModel.update(board, currentFps);
			view.render();

			lastTime = currentTime;
			try {
				Thread.sleep(7);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
}
