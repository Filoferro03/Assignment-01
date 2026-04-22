package pcd.threadVersion.controller;

import pcd.threadVersion.model.Board;

public interface Cmd {

	void execute(Board board);
}
