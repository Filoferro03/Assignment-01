package pcd.taskVersion.view;


import pcd.taskVersion.controller.Cmd;
import pcd.taskVersion.controller.KickCmd;
import pcd.taskVersion.model.V2d;
import pcd.taskVersion.util.BoundedBuffer;

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
