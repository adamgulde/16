import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class NodePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<ArrayList<int[]>> totalCoordsList = new ArrayList<ArrayList<int[]>>();
	ArrayList<int[]> coordsList;
	
	public void appendCoordsList(ArrayList<int[]> list, int x1_, int y1_) {
		coordsList = list;
		coordsList.add(0, new int[] {x1_, y1_});
		totalCoordsList.add(coordsList);
	}
	
	@Override
	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
		for(ArrayList<int[]> cList : totalCoordsList) {
			for(int i=1;i<cList.size();i++) {
				g.drawLine(cList.get(0)[0], cList.get(0)[1], cList.get(i)[0],cList.get(i)[1]);	
			}	
		}
	}
}
