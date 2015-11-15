package model;

import java.util.ArrayList;
import java.util.List;

public class Sketch {
	private List<Stroke> strokes;
	private int row;
	private int column;
	
	public Sketch(int row, int column){
		strokes = new ArrayList<Stroke>();
		this.row = row;
		this.column = column;
	}
	
	public List<Stroke> getStrokes(){
		return this.strokes;
	}
	
	public void addStroke(Stroke str){
		this.strokes.add(str);
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return column;
	}
}
