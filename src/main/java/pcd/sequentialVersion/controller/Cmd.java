package pcd.sequentialVersion.controller;

import pcd.sequentialVersion.model.Board;

public interface Cmd {

	void execute(Board board);
}
