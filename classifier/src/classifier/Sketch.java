package classifier;

import java.util.ArrayList;
import java.util.List;

public class Sketch {
	private int lang = 0;
	private int label;
	private List<Stroke> strokes;
	private int row;
	private int column;
	
	public Sketch(int row, int column){
		strokes = new ArrayList<Stroke>();
		this.row = row;
		this.column = column;
		this.label = 0;
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
	
	public int getLabel(){
		return label;
	}
	
	public void setLabel(int label){
		this.label = label;
	}

	public int getLang() {
		return lang;
	}

	public void setLang(int lang) {
		this.lang = lang;
	}
}
