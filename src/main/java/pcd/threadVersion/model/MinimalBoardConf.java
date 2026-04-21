package pcd.threadVersion.model;

import pcd.sketch01.*;
import pcd.sketch01.Ball;
import pcd.sketch01.Boundary;
import pcd.sketch01.P2d;
import pcd.sketch01.V2d;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConf implements BoardConf {

	@Override
	public pcd.sketch01.Ball getPlayerBall() {
    	return new pcd.sketch01.Ball(new pcd.sketch01.P2d(0, 0), 0.06, 1, new pcd.sketch01.V2d(0,0.5));
	}

	@Override
	public List<pcd.sketch01.Ball> getSmallBalls() {
        var balls = new ArrayList<pcd.sketch01.Ball>();
    	var b1 = new pcd.sketch01.Ball(new pcd.sketch01.P2d(0, 0.5), 0.05, 0.75, new pcd.sketch01.V2d(0,0));
    	var b2 = new Ball(new P2d(0.05, 0.55), 0.025, 0.25, new V2d(0,0));
    	balls.add(b1);
    	balls.add(b2);
    	return balls;
	}

	@Override
	public pcd.sketch01.Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}

}
