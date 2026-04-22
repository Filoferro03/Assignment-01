package pcd.threadVersion.model;

import java.util.List;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int humanScore = 0;
    private boolean isGameOver = false;
    
    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
    	bounds = conf.getBoardBoundary();
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
    	} 
    	   	    	
    }

    public void hitPlayerBall (V2d impulse) {
        if (playerBall != null) {
            V2d currentVelocity = playerBall.getVel();
            playerBall.kick(new V2d(currentVelocity.x() + impulse.x(), currentVelocity.y() + impulse.y()));
        }
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }
    
    public  Boundary getBounds(){
        return bounds;
    }

    public int getHumanScore() {
        return humanScore;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
