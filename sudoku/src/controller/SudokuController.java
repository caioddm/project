package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import model.Game;
import model.Sketch;
import model.Stroke;
import model.UpdateAction;
import recognizer.TemplateMatcher;
import view.Field;
import view.SudokuPanel;
import view.SudokuPanel.SubPanel;

/**
 * This class controls all user actions from SudokuPanel.
 *
 * @author Eric Beijer
 */
public class SudokuController extends MouseInputAdapter {
    private SudokuPanel sudokuPanel;    // Panel to control.
    private Game game;                  // Current Sudoku game.
    private TemplateMatcher templateMatcher;
    //private List<Sketch> sketches;
    private Stroke currentStroke;
    private Sketch currentSketch;
    private Timer timer;
    private TimerTask timerTask;
    private SubPanel currentPanel;
    private Field currentField;
    private HashMap<Field, ArrayList<Sketch>> fieldSketchMap;

    /**
     * Constructor, sets game.
     *
     * @param game  Game to be set.
     */
    public SudokuController(SudokuPanel sudokuPanel, Game game) {
        this.sudokuPanel = sudokuPanel;
        this.game = game;
        //this.sketches = new ArrayList<Sketch>();
        this.timer = new Timer();
        this.fieldSketchMap = new HashMap<Field, ArrayList<Sketch>>();
        this.templateMatcher = new TemplateMatcher(".");
    }

    /**
     * Recovers if user clicked field in game. If so it sets the selected number
     * at clicked position in game and updates clicked field. If user clicked a
     * field and used left mouse button, number at clicked position will be
     * cleared in game and clicked field will be updated.
     *
     * @param e MouseEvent.
     */
    public void mousePressed(MouseEvent e) {
        SubPanel panel = (SubPanel)e.getSource();
        Component component = panel.getComponentAt(e.getPoint());
        if (component instanceof Field) {
        	Field field = (Field)component;
            int x = field.getFieldX();
            int y = field.getFieldY();
            if(!game.isImmutableCell(x, y)){
	        	if(timerTask != null)
	        		timerTask.cancel();
	        	
	        	if(_changedField(e.getX(), e.getY(), panel) && currentSketch != null){
	        		_startNewSketch();
	        	}
	        	currentStroke = new Stroke(new ArrayList<Point>());
	        	currentStroke.addPoint(e.getX(), e.getY());        	
	        	
	            this.currentField = field;
	            /*if (e.getButton() == MouseEvent.BUTTON1 && (game.getNumber(x, y) == 0 || field.getForeground().equals(Color.BLUE))) {
	                int number = game.getSelectedNumber();
	                if (number == -1)
	                    return;
	                game.setNumber(x, y, number);
	                field.setNumber(number, game.getLang(), true);
	            } else if (e.getButton() == MouseEvent.BUTTON3 && !field.getForeground().equals(Color.BLACK)) {
	                game.setNumber(x, y, 0);
	                field.setNumber(0, game.getLang(), false);
	            }*/
	            //sudokuPanel.update(game, UpdateAction.CANDIDATES);
            }
        }
    }
    
    public void mouseDragged(MouseEvent e) {
    	SubPanel panel = (SubPanel)e.getSource();
    	currentStroke.addPoint(e.getX(), e.getY());
        panel.setOngoingStroke(currentStroke);
        panel.repaint();
    }
    
    public void mouseReleased(MouseEvent e) {
    	SubPanel panel = (SubPanel)e.getSource();
    	panel.resetPoints();
    	if(currentSketch == null)
    	{
    		int row = _getStrokeRow()+(3*panel.getYOffset());
    		int column = _getStrokeColumn()+(3*panel.getXOffset());
    		currentSketch = new Sketch(row, column);
    	}
    	
    	currentSketch.addStroke(currentStroke);
    	currentField = (Field)panel.getComponentAt(e.getPoint());
    	
    	if(!panel.getSketches().contains(currentSketch)) {
    		panel.addSketch(currentSketch);
    	}
    	else {
    		List<Sketch> sketches = panel.getSketches();
    		int idx = sketches.indexOf(currentSketch);
    		sketches.set(idx, currentSketch);	
    	}
    	
    	currentPanel = panel;
    	ArrayList<Sketch> sketchList;
    	if(fieldSketchMap.containsKey(currentField))
    		sketchList = fieldSketchMap.get(currentField);
    	else sketchList = new ArrayList<Sketch>();
    	
    	this.currentPanel = panel;
    	
    	if(!sketchList.contains(currentSketch))
    		sketchList.add(currentSketch);
		fieldSketchMap.put(currentField, sketchList);
    	
    	timerTask = new TimerTask() {
  		  @Override
  		  public void run() {
  		    _startNewSketch();
  		  }
  		};
  		
  		if(!game.isRoughModeOn())
  			timer.schedule(timerTask, 5*1000);
    }
    
    public void mouseClicked(MouseEvent e) {
    	
    	SubPanel panel = (SubPanel)e.getSource();
        Component component = panel.getComponentAt(e.getPoint());
        if (component instanceof Field) {
            Field field = (Field)component;
            int x = field.getFieldX(), y = field.getFieldY();
            
            if(game.isEraseModeOn() && !game.isImmutableCell(x, y)) {
            	//System.out.println("Setting ("+x+","+y+")");
            	game.setNumber(x, y, 0);
            	field.setNumber(0, game.getLang(), true);
            	List<Sketch> sketches = currentPanel.getSketches();
            	//System.out.println(fieldSketchMap.get(field));
            	List<Sketch> fieldSketch = fieldSketchMap.get(field);
            	for(Sketch s: fieldSketch) {
            		int idx = sketches.indexOf(s);
            		if(idx != -1)
            			sketches.remove(idx);
            		panel.repaint();
            	}
            	
            	//panel.clearSketch();
            	//currentSketch = null;
            }
            /*else {
            	if(!game.isImmutableCell(x, y)) {
	            	System.out.println("Setting ("+x+","+y+")");
	            	
	            	game.setNumber(x, y, 1);
	            	field.setNumber(1, game.getLang(), false);
            	}
            }*/
        }
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    
    private int _getStrokeRow(){
		return _getStrokeRow(currentStroke.getFirstPoint().y);
    }
    
    private int _getStrokeRow(int y){
		return y/80;
    }
    
    private int _getStrokeColumn(){
		return _getStrokeColumn(currentStroke.getFirstPoint().x);
    }
    
    private int _getStrokeColumn(int x){
		return x/80;
    }
    
    private boolean _changedField(int x, int y, SubPanel panel){
    	return currentSketch == null || currentSketch.getRow() != _getStrokeRow(y)+(3*panel.getYOffset()) || currentSketch.getColumn() != _getStrokeColumn(x)+(3*panel.getXOffset());
    }
    
    private void _startNewSketch(){
    	if(currentSketch != null){
    		if(!(game.isRoughModeOn() || game.isEraseModeOn())) {
    			_setFieldValue(_classifySketch(currentSketch));
    			//System.out.println("Recognizing");
    			List<Sketch> sketches = currentPanel.getSketches();
    	    	int idx = sketches.indexOf(currentSketch);
    	    	if(idx != -1) sketches.remove(idx);
    	    	
    	    	List<Sketch> fieldSketch = fieldSketchMap.get(currentField);
    	    	for(Sketch s: fieldSketch) {
            		int id = sketches.indexOf(s);
            		if(id != -1)
            			sketches.remove(id);
            		currentPanel.repaint();
            	}
    		}
    		else game.setPrevMove(false);
			
			
	    	currentSketch = null;
			//currentPanel.clearSketch();
			sudokuPanel.repaint();
		}
    }
    
    private int _classifySketch(Sketch sketch){
    	Sketch clonedSketch = TemplateMatcher.CloneStrokes(sketch);
    	_adjustSketch(clonedSketch);
    	return templateMatcher.Classify(clonedSketch);
    }
    private void _setFieldValue(int label){
    	Component component = currentPanel.getComponentAt(currentSketch.getStrokes().get(0).getFirstPoint());
    	Field field = (Field)component;
        int x = field.getFieldX();
        int y = field.getFieldY();
        game.setNumber(x, y, label);
        field.setNumber(label, game.getLang(), true);
        sudokuPanel.update(game, UpdateAction.CANDIDATES);
    }
    private void _adjustSketch(Sketch sketch){
    	if(sketch != null){
    		for (int i = 0; i < sketch.getStrokes().size(); i++) {
				Stroke stroke = sketch.getStrokes().get(i);
    			for (int j = 0; j < stroke.getPoints().size(); j++) {
					Point point = stroke.getPoints().get(j);
					int newX = Math.floorMod((int)point.getX(), 80);
					int newY = Math.floorMod((int)point.getY(), 80);
					point.move(newX, newY);
				}
			}
    	}
    
    }
    
}