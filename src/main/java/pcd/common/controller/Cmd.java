package pcd.common.controller;

import pcd.common.model.Board;

public interface Cmd {

	void execute(Board board);
}
