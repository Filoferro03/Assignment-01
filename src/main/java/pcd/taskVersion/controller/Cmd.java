package pcd.taskVersion.controller;

import pcd.taskVersion.model.Board;

public interface Cmd {

	void execute(Board board);
}
