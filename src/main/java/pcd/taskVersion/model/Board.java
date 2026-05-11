package pcd.taskVersion.model;

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
    private boolean playerWon = false;
    private final double CELL_SIZE = 0.025;
    private int numCols;
    private int numRows;
    private List<Ball>[][] grid;

    public Board(BoardConf conf){
        balls = conf.getSmallBalls();
        playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
        bounds = conf.getBoardBoundary();
        double holeRadius = 0.4;
        holes.add(new Hole(new V2d(bounds.x0(), bounds.y1()), holeRadius));
        holes.add(new Hole(new V2d(bounds.x1(), bounds.y1()), holeRadius));
        double width = bounds.x1() - bounds.x0();
        double height = bounds.y1() - bounds.y0();
        numCols = (int) Math.ceil(width / CELL_SIZE);
        numRows = (int) Math.ceil(height / CELL_SIZE);
        grid = new ArrayList[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                grid[r][c] = new ArrayList<>();
            }
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
                int hitter = b.getLastHitter();
                if (hitter == 1) humanScore++;
                else if (hitter == 2) botScore++;
                it.remove();
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

    public void detectCollisionsCyclic(int workerId, int totalWorkers) {
        for (int i = workerId; i < balls.size(); i += totalWorkers) {
            Ball a = balls.get(i);
            int myCol = (int) ((a.getPos().x() - bounds.x0()) / CELL_SIZE);
            int myRow = (int) ((a.getPos().y() - bounds.y0()) / CELL_SIZE);
            myCol = Math.clamp(myCol, 0, numCols - 1);
            myRow = Math.clamp(myRow, 0, numRows - 1);
            for (int r = myRow - 1; r <= myRow + 1; r++) {
                for (int c = myCol - 1; c <= myCol + 1; c++) {
                    if (r >= 0 && r < numRows && c >= 0 && c < numCols) {
                        for (Ball b : grid[r][c]) {
                            if (a.getId() < b.getId()) {
                                if (Ball.resolveCollision(a, b)) {
                                    a.setLastHitter(0);
                                    b.setLastHitter(0);
                                }
                            }
                        }
                    }
                }
            }
            if (playerBall != null && Ball.resolveCollision(playerBall, a)) {
                a.setLastHitter(1);
            }
            if (botBall != null && Ball.resolveCollision(botBall, a)) {
                a.setLastHitter(2);
            }
        }
    }

    public void applyMovementsCyclic(int workerId, int totalWorkers, long dt) {
        for (int i = workerId; i < balls.size(); i += totalWorkers) {
            balls.get(i).updateState(dt, this);
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

    public void buildSpatialGrid() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                grid[r][c].clear();
            }
        }
        for (Ball b : balls) {
            int col = (int) ((b.getPos().x() - bounds.x0()) / CELL_SIZE);
            int row = (int) ((b.getPos().y() - bounds.y0()) / CELL_SIZE);
            col = Math.clamp(col, 0, numCols - 1);
            row = Math.clamp(row, 0, numRows - 1);
            grid[row][col].add(b);
        }
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
