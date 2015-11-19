package painting;

import java.util.List;
import java.util.ArrayList;

public class Figure {
	public final int numIters = 10;
	
	// Variable list of strokes for 10 iterations of each number
	private List<ArrayList<Stroke>> strokes;
	private int height, width;
	private int curIter;
	
	public Figure() {
		curIter = 0;
		strokes = new ArrayList<ArrayList<Stroke>>(numIters);
		for(int i = 0; i < numIters; ++i)
			strokes.add(new ArrayList<Stroke>());
	}
	
	public Figure(int w, int h) {
		curIter = 0;
		strokes = new ArrayList<ArrayList<Stroke>>(numIters);
		for(int i = 0; i < numIters; ++i)
			strokes.add(new ArrayList<Stroke>());
		
		this.width = w;
		this.height = h;
	}
	
	public ArrayList<ArrayList<Stroke>> getStrokeList() {
		  return (ArrayList<ArrayList<Stroke>>) this.strokes;
	}
	
	public void setCurIter(int i) {
		curIter = i;
	}
	
	public int getCurIter() {
		return curIter;
	}
	
	public void incCurIter() {
		curIter = (++curIter)%numIters;
	}
	
	public ArrayList<Stroke> getStrokes(int i) {
		return (ArrayList<Stroke>) this.strokes.get(i);
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setHeight(int h) {
		this.height = h;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setWidth(int w) {
		this.width = w;
	}
}
