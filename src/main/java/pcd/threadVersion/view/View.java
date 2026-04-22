package pcd.threadVersion.view;


import pcd.threadVersion.controller.Cmd;
import pcd.threadVersion.controller.KickCmd;
import pcd.threadVersion.model.V2d;
import pcd.threadVersion.util.*;

public class View {

	private ViewFrame frame;
	private ViewModel viewModel;
	private BoundedBuffer<Cmd> buffer;
	
	public View(ViewModel model, int w, int h, BoundedBuffer<Cmd> buffer) {
		frame = new ViewFrame(this, model, w, h);
		frame.setVisible(true);
		this.viewModel = model;
		this.buffer = buffer;
	}

	public void handleInput(V2d impulse) {
		try {
			buffer.put(new KickCmd(impulse));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	public void render() {
		frame.render();
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
}
