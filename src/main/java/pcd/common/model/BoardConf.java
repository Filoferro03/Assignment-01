package pcd.common.model;

import java.util.List;

public interface BoardConf {

	Boundary getBoardBoundary();
	
	Ball getPlayerBall();

	Ball getBotBall();
	
	List<Ball> getSmallBalls();
}
