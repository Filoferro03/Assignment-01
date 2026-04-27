package pcd.threadVersion.view;


import pcd.threadVersion.model.Board;
import pcd.threadVersion.model.Boundary;
import pcd.threadVersion.model.P2d;

import java.util.ArrayList;

record BallViewInfo(P2d pos, double radius) {}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private BallViewInfo bot;
	private int framePerSec;
	private int humanScore = 0;
	private int botScore = 0;
	private boolean isGameOver = false;
	private Boundary bounds;
	private double holeRadius;
	private boolean playerWon = false;
	
	public ViewModel() {
		balls = new ArrayList<BallViewInfo>();
		framePerSec = 0;
	}
	
	public synchronized void update(Board board, int framePerSec) {
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
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
		this.holeRadius = board.getHoleRadius();
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		var copy = new ArrayList<BallViewInfo>();
		copy.addAll(balls);
		return copy;
		
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

	public synchronized double getHoleRadius() {
		return holeRadius;
	}

	public synchronized boolean hasPlayerWon() {
		return playerWon;
	}
	
}
