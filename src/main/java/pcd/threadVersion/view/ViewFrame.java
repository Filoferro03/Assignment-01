package pcd.threadVersion.view;

import pcd.threadVersion.model.V2d;
import sketch01.RenderSynch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame {
    
    private VisualiserPanel panel;
    private ViewModel model;
    private RenderSynch sync;
	private View view;
    
    public ViewFrame(View view, ViewModel model, int w, int h){
		this.view = view;
    	this.model = model;
    	this.sync = new RenderSynch();
    	setTitle("Thread Version Game");
        setSize(w,h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w,h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
		this.setFocusable(true);
		this.requestFocusInWindow();

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				double impulseForce = 1.0;
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						view.handleInput(new V2d(0, impulseForce));
						break;
					case KeyEvent.VK_DOWN:
						view.handleInput(new V2d(0, -impulseForce));
						break;
					case KeyEvent.VK_LEFT:
						view.handleInput(new V2d(-impulseForce, 0));
						break;
					case KeyEvent.VK_RIGHT:
						view.handleInput(new V2d(impulseForce, 0));
						break;
				}
			}
		});
    }
     
    public void render(){
		long nf = sync.nextFrameToRender();
        panel.repaint();
		try {
			sync.waitForFrameRendered(nf);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
    }
        
    public class VisualiserPanel extends JPanel {
        private int ox;
        private int oy;
        private int delta;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h + 25);
            ox = w/2;
            oy = h/2;
            delta = Math.min(ox, oy);
        }

        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
    		
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
            
    		g2.setColor(Color.LIGHT_GRAY);
		    g2.setStroke(new BasicStroke(1));
    		g2.drawLine(ox,0,ox,oy*2);
    		g2.drawLine(0,oy,ox*2,oy);
    		g2.setColor(Color.BLACK);
    		
    		    g2.setStroke(new BasicStroke(1));
	    		for (var b: model.getBalls()) {
	    			var p = b.pos();
	            	int x0 = (int)(ox + p.x()*delta);
	                int y0 = (int)(oy - p.y()*delta);
	                int radiusX = (int)(b.radius()*delta);
	                int radiusY = (int)(b.radius()*delta);
	                g2.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
	    		}

				g2.setStroke(new BasicStroke(3));
				var pb = model.getPlayerBall();
				if (pb != null) {
					drawBallWithLabel(g2, pb, "h", Color.BLUE);
				}

				var bb = model.getBotBall();
				if (bb != null) {
					drawBallWithLabel(g2, bb, "b", Color.RED);
				}
    		    
    		    g2.setStroke(new BasicStroke(1));
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("Arial", Font.PLAIN, 12));
	    		g2.drawString("Num small balls: " + model.getBalls().size(), 20, 40);
	    		g2.drawString("Frame per sec: " + model.getFramePerSec(), 20, 60);

	    		sync.notifyFrameRendered();
    		
        }

		private void drawBallWithLabel(Graphics2D g2, BallViewInfo b, String label, Color color) {
			var p = b.pos();
			int x0 = (int)(ox + p.x()*delta);
			int y0 = (int)(oy - p.y()*delta);
			int r = (int)(b.radius()*delta);

			g2.setColor(color);
			g2.drawOval(x0 - r, y0 - r, r*2, r*2);

			g2.setFont(new Font("Arial", Font.PLAIN, (int)(r * 1.2)));

			FontMetrics fm = g2.getFontMetrics();
			int textX = x0 - (fm.stringWidth(label) / 2);
			int textY = y0 + ((fm.getAscent() - fm.getDescent()) / 2);

			g2.drawString(label, textX, textY);
		}
    }
}
