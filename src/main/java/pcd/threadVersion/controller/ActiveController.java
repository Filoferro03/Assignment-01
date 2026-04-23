package pcd.threadVersion.controller;

import pcd.threadVersion.util.*;
import pcd.threadVersion.model.Board;
import pcd.threadVersion.view.View;
import pcd.threadVersion.view.ViewModel;

public class ActiveController extends Thread {

	private final Board board;
	private final View view;
	private final ViewModel viewModel;
	private BoundedBuffer<Cmd> buffer;

	public  ActiveController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		this.board = board;
		this.view = view;
		this.viewModel = viewModel;
		this.buffer = buffer;
	}
	
	public void run() {
		log("started.");
		long lastTime = System.currentTimeMillis();
		while (!board.isGameOver()) {
			long currentTime = System.currentTimeMillis();
			long dt = currentTime - lastTime;
			Cmd command = buffer.poll();
			if (command != null) {
				if (command instanceof KickCmd) {
					board.hitPlayerBall(((KickCmd)command).getImpulse());
				} else if (command instanceof BotKickCmd) {
					board.hitBotBall(((BotKickCmd) command).getImpulse());
				}
			}
			board.updateState(dt);
			int currentFps = (dt > 0) ? (int) (1000 / dt) : 0;
			viewModel.update(board, currentFps);
			view.render();
			lastTime = currentTime;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.println("Game Over! Punteggio finale: " + board.getHumanScore());
	}
	
	private void log(String msg) {
		System.out.println("[ " + System.currentTimeMillis() + "][ Controller ] " + msg);
	}
}
