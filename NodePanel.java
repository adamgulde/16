import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Point;

public class NodePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Point origin = new Point(0,0);
	private Point endpoint = new Point(0,0);
	
	public NodePanel(Network N) {
		addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	endpoint = e.getPoint();
            	origin = e.getPoint();
            	repaint();
            } 
        });
		addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(MouseEvent e) {
	        	endpoint = e.getPoint();
	        	N.relocateNodes((endpoint.x-origin.x), 
            			(endpoint.y-origin.y));
	        	repaint();
	        	origin = endpoint;
	        	// Credit to Dylan Coben for fixing mouse code. 
	        }
	    });
	}
	
	ArrayList<ConnectionLine> lineList;
	public void retrieveLinesList(ArrayList<ConnectionLine> linesList) {
		lineList = linesList;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(ConnectionLine cLine : lineList) {
			g.drawLine(cLine.x1, cLine.y1, cLine.x2, cLine.y2);
		}
	}	
}
