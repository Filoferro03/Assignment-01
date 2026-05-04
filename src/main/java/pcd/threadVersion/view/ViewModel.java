package pcd.threadVersion.view;


import pcd.threadVersion.model.Board;
import pcd.threadVersion.model.Boundary;
import pcd.threadVersion.model.P2d;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {}

record HoleViewInfo(P2d pos, double radius) {}

public class ViewModel {

	private final ArrayList<BallViewInfo> balls;
	private final ArrayList<HoleViewInfo> holes;
	private BallViewInfo player;
	private BallViewInfo bot;
	private int framePerSec;
	private int humanScore = 0;
	private int botScore = 0;
	private boolean isGameOver = false;
	private Boundary bounds;
	private boolean playerWon = false;
	
	public ViewModel() {
		balls = new ArrayList<BallViewInfo>();
		holes = new ArrayList<HoleViewInfo>();
		framePerSec = 0;
	}
	
	public synchronized void update(Board board, int framePerSec) {
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
		}
		holes.clear();
		if (board.getHoles() != null) {
			for (var h : board.getHoles()) {
				// Convertiamo il V2d della logica nel P2d della view
				holes.add(new HoleViewInfo(new P2d(h.getPos().x(), h.getPos().y()), h.getRadius()));
			}
		}
		this.framePerSec = framePerSec;
		var p = board.getPlayerBall();
		player = new BallViewInfo(p.getPos(), p.getRadius());
		var b =  board.getBotBall();
		bot = new BallViewInfo(b.getPos(), b.getRadius());
		this.humanScore = board.getHumanScore();
		this.botScore = board.getBotScore();
		this.isGameOver = board.isGameOver();
		this.playerWon = board.hasPlayerWon();
		this.bounds = board.getBounds();
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		return new ArrayList<BallViewInfo>(this.balls);
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

	public synchronized BallViewInfo getBotBall() {
		return bot;
	}

	public synchronized int getHumanScore() {
		return humanScore;
	}

	public synchronized int getBotScore() {
		return botScore;
	}

	public synchronized boolean isGameOver() {
		return isGameOver;
	}

	public synchronized Boundary getBounds() {
		return bounds;
	}

	public synchronized boolean hasPlayerWon() {
		return playerWon;
	}

	public synchronized ArrayList<HoleViewInfo> getHoles() {
		return new ArrayList<HoleViewInfo>(holes);
	}
}
