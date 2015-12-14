package classifier;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Stroke {
	private List<Point> points;
	
	public Stroke(){
		this.points = new ArrayList<Point>();
	}
	
	public Stroke(List<Point> points){
		this.points = points;
	}
	
	public void addPoint(int x, int y){
		this.points.add(new Point(x, y));
	}
	
	public List<Point> getPoints(){
		return points;
	}
	
	public Point getFirstPoint(){
		return points.size() > 0 ? points.get(0) : null;
	}
	
	public Point getLastPoint(){
		return points.size() > 0 ? points.get(points.size() - 1) : null; 
	}
}
