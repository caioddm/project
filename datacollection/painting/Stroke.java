package painting;

import java.awt.Point;
import java.util.ArrayList;

public class Stroke {
	private ArrayList<Point> points;
	
	public Stroke() {
		points = new ArrayList<Point>();
	}
	
	public void addPoint(Point p) {
		this.points.add(p);
	}
	
	public ArrayList<Point> getPoints() {
		return this.points;
	}
}
