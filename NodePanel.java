import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class NodePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	ArrayList<ArrayList<int[]>> totalCoordsList = new ArrayList<ArrayList<int[]>>();
	ArrayList<int[]> coordsList;
	int x_offset = 0;
	int y_offset = 0;
	
	public void appendCoordsList(ArrayList<int[]> list, int x1_, int y1_) {
		coordsList = list;
		coordsList.add(0, new int[] {x1_, y1_});
		totalCoordsList.add(coordsList);
	}
	public void deleteCoordsList() {
		totalCoordsList.removeAll(totalCoordsList);
	}
	public void updateCoordsList(int mode) {
		switch (mode) {
		case 1:
			y_offset=20;
			break;
		case 2: 
			x_offset=-20;
			break;
		case 3: 
			y_offset=-20;
			break;
		case 4:
			x_offset=20;
			break;
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		for(ArrayList<int[]> cList : totalCoordsList) {
			for(int i=1;i<cList.size();i++) {
				g.drawLine(cList.get(0)[0] + x_offset, cList.get(0)[1]+ y_offset, cList.get(i)[0]+x_offset,cList.get(i)[1]+y_offset);	
			}	
		}
	}	
}
