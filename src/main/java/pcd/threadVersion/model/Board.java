package pcd.threadVersion.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Board {

    private List<Ball> balls;
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private int humanScore = 0;
    private int botScore = 0;
    private boolean isGameOver = false;
    private final double holeRadius = 0.4;
    private Map<Ball, Integer> lastHit = new HashMap<>();
    private boolean playerWon = false;
    
    public Board(){} 
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
        if (botBall != null) {
            botBall.updateState(dt, this);
        }

        for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                if (Ball.resolveCollision(balls.get(i), balls.get(j))) {
                    lastHit.remove(balls.get(i));
                    lastHit.remove(balls.get(j));
                }
            }
        }
    	for (var b: balls) {
    		if (Ball.resolveCollision(playerBall, b)) {
                lastHit.put(b, 1);
            }
    	}

        if (botBall != null) {
            for (var b: balls) {
                if (Ball.resolveCollision(botBall, b)) {
                    lastHit.put(b, 2);
                }
            }
            Ball.resolveCollision(playerBall, botBall);
        }

        Iterator<Ball> it = balls.iterator();
        while (it.hasNext()) {
            Ball b = it.next();
            if (isBallInHole(b)) {
                Integer hitter = lastHit.get(b);
                if (hitter != null) {
                    if (hitter == 1) humanScore++;
                    else if (hitter == 2) botScore++;
                }
                it.remove();
                lastHit.remove(b);
            }
        }

        if (isBallInHole(playerBall)) {
            playerWon = false;
            isGameOver = true;
            System.out.println("Partita terminata: Human è caduto in buca. Vince il BOT!");
        } else if (botBall != null && isBallInHole(botBall)) {
            playerWon = true;
            isGameOver = true;
            System.out.println("Partita terminata: Bot è caduto in buca. Vince HUMAN!");
        } else if (balls.isEmpty()) {
            if (humanScore > botScore) {
                playerWon = true;
            } else {
                playerWon = false;
            }
            isGameOver = true;
            System.out.println("Partita terminata: Palline esaurite.");
        }
    }

    public void hitPlayerBall (V2d impulse) {
        if (playerBall != null) {
            V2d currentVelocity = playerBall.getVel();
            playerBall.kick(new V2d(currentVelocity.x() + impulse.x(), currentVelocity.y() + impulse.y()));
        }
    }

    public void hitBotBall (V2d impulse) {
        if (botBall != null) {
            V2d currentVelocity = botBall.getVel();
            botBall.kick(new V2d(currentVelocity.x() + impulse.x(), currentVelocity.y() + impulse.y()));
        }
    }

    private boolean isBallInHole(Ball b) {
        double hole1X = bounds.x0();
        double hole1Y = bounds.y1();

        double hole2X = bounds.x1();
        double hole2Y = bounds.y1();

        double dist1 = Math.hypot(b.getPos().x() - hole1X, b.getPos().y() - hole1Y);
        double dist2 = Math.hypot(b.getPos().x() - hole2X, b.getPos().y() - hole2Y);

        return dist1 < holeRadius || dist2 < holeRadius;
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }

    public Ball getBotBall() {return botBall;}
    
    public  Boundary getBounds(){
        return bounds;
    }

    public int getHumanScore() {
        return humanScore;
    }

    public int getBotScore() {return botScore;}

    public boolean isGameOver() {
        return isGameOver;
    }

    public double getHoleRadius() {
        return holeRadius;
    }

    public boolean hasPlayerWon() {
        return playerWon;
    }
}
