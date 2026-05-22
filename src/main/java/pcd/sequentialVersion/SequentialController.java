package pcd.sequentialVersion;

import pcd.common.controller.AbstractGameController;
import pcd.common.controller.Cmd;
import pcd.common.model.Board;
import pcd.common.util.BoundedBuffer;
import pcd.common.view.View;
import pcd.common.view.ViewModel;

public class SequentialController extends AbstractGameController {

	public SequentialController(Board board, View view, ViewModel viewModel, BoundedBuffer<Cmd> buffer) {
		super(board, view, viewModel, buffer);
	}

	@Override
	protected void executePhysicsStep(long dt) {
		board.applyMovementsCyclic(0, 1, dt);
		board.buildSpatialGrid();
		board.detectCollisionsCyclic(0, 1);
		board.updateGlobalState(dt);
	}
}