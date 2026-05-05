package pcd.threadVersion.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Board {

    private final List<Ball> balls;
    private final Ball playerBall;
    private final Ball botBall;
    private final Boundary bounds;
    private int humanScore = 0;
    private int botScore = 0;
    private boolean isGameOver = false;
    private final List<Hole> holes = new ArrayList<>();
    private final Map<Ball, Integer> lastHit;
    private boolean playerWon = false;

    public Board(BoardConf conf){
        balls = conf.getSmallBalls();
        playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
        bounds = conf.getBoardBoundary();
        lastHit = new ConcurrentHashMap<>();
        double holeRadius = 0.4;
        holes.add(new Hole(new V2d(bounds.x0(), bounds.y1()), holeRadius));
        holes.add(new Hole(new V2d(bounds.x1(), bounds.y1()), holeRadius));
    }

    public void detectCollisionsCyclic(int workerId, int totalWorkers) {
        for (int i = workerId; i < balls.size(); i += totalWorkers) {
            Ball b = balls.get(i);

            for (int j = i + 1; j < balls.size(); j++) {
                Ball other = balls.get(j);
                if (Ball.resolveCollision(b, other)) {
                    lastHit.remove(b);
                    lastHit.remove(other);
                }
            }

            if (playerBall != null && Ball.resolveCollision(playerBall, b)) {
                lastHit.put(b, 1);
            }

            if (botBall != null && Ball.resolveCollision(botBall, b)) {
                lastHit.put(b, 2);
            }
        }
    }

    public void applyMovementsCyclic(int workerId, int totalWorkers, long dt) {
        for (int i = workerId; i < balls.size(); i += totalWorkers) {
            balls.get(i).updateState(dt, this);
        }
    }

    public void updateGlobalState(long dt) {
        if (playerBall != null && botBall != null) {
            Ball.resolveCollision(playerBall, botBall);
        }

        if (playerBall != null) playerBall.updateState(dt, this);
        if (botBall != null) botBall.updateState(dt, this);

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
            playerWon = humanScore > botScore;
            isGameOver = true;
            System.out.println("Partita terminata: Palline esaurite.");
        }
    }

    public void hitPlayerBall (V2d impulse) {
        if (playerBall != null) {
            playerBall.kick(impulse);
        }
    }

    public void hitBotBall (V2d impulse) {
        if (botBall != null) {
            botBall.kick(impulse);
        }
    }

    private boolean isBallInHole(Ball b) {
        for (Hole h : holes) {
            if (h.checkCollision(b)) {
                return true;
            }
        }
        return false;
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

    public boolean hasPlayerWon() {
        return playerWon;
    }

    public List<Hole> getHoles() {
        return holes;
    }
}
