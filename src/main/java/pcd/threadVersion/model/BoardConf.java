package pcd.threadVersion.model;

import pcd.sketch01.Ball;
import pcd.sketch01.Boundary;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	pcd.sketch01.Ball getPlayerBall();
	
	List<Ball> getSmallBalls();
}
