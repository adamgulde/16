import java.awt.Point;

public class ConnectionLine {
	
	public int x1, x2, y1, y2;
	
	public ConnectionLine(Point start, Point end) {
		x1 = start.x;
		x2 = end.x;
		y1 = start.y;
		y2 = end.y;
	}
}
