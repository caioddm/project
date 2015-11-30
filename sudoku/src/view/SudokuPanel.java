package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import controller.SudokuController;
import model.Game;
import model.Sketch;
import model.Stroke;
import model.UpdateAction;

/**
 * This class draws the sudoku panel and reacts to updates from the model.
 *
 * @author Eric Beijer
 */


public class SudokuPanel extends JPanel implements Observer {
	public class SubPanel extends JPanel{
		private List<Sketch> sketches;
		private Stroke ongoingStroke;
		private int xOffset;
		private int yOffset;
		
		public SubPanel(int xOff, int yOff){
			super();
			sketches = new ArrayList<Sketch>();
			xOffset = xOff;
			yOffset = yOff;
		}
		
		public SubPanel(GridLayout grid, int xOff, int yOff){
			super(grid);
			sketches = new ArrayList<Sketch>();
			xOffset = xOff;
			yOffset = yOff;
		}
		
		public int getXOffset(){
			return xOffset;
		}
		
		public int getYOffset(){
			return yOffset;
		}
		
		public List<Sketch> getSketches() {
	        return sketches;
	    }

	    public void addSketch(Sketch skt) {
	        sketches.add(skt);
	    }

	    public void resetPoints() {
	    	ongoingStroke = null;
	    }
	    
	    public void setOngoingStroke(Stroke s){
	    	this.ongoingStroke = s;
	    }
	    
	    @Override
	    public void paintComponent(Graphics g) {
	        g.setColor(Color.WHITE);
	        g.fillRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.BLACK);
	        for (int i = 0; i < sketches.size(); i++) {
				for (int j = 0; j < sketches.get(i).getStrokes().size(); j++) {
					Stroke stroke = sketches.get(i).getStrokes().get(j);
					for (int k = 1; k < stroke.getPoints().size(); k++) {
			            Point p1 = stroke.getPoints().get(k - 1);
			            Point p2 = stroke.getPoints().get(k);
			            g.drawLine(p1.x, p1.y, p2.x, p2.y);
			        }
				}
			}
	        
	        //draw ongoing stroke
	        if(ongoingStroke != null){
		        for (int k = 1; k < ongoingStroke.getPoints().size(); k++) {
		            Point p1 = ongoingStroke.getPoints().get(k - 1);
		            Point p2 = ongoingStroke.getPoints().get(k);
		            g.drawLine(p1.x, p1.y, p2.x, p2.y);
		        }
	        }
	    }
	}
    // Color constant for candidates.
    private static final Color COLOR_CANDIDATE = new Color(102, 153, 255);

    private Field[][] fields;       // Array of fields.
    private SubPanel[][] panels;      // Panels holding the fields.
    
    
    /**
     * Constructs the panel, adds sub panels and adds fields to these sub panels.
     */
    public SudokuPanel() {
        super(new GridLayout(3, 3));
        panels = new SubPanel[3][3];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                panels[y][x] = new SubPanel(new GridLayout(3, 3), x, y);
                panels[y][x].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                add(panels[y][x]);
            }
        }

        fields = new Field[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                fields[y][x] = new Field(x, y, panels[y / 3][x / 3]);
                panels[y / 3][x / 3].add(fields[y][x]);
            }
        }
    }

    /**
     * Method called when model sends update notification.
     *
     * @param o     The model.
     * @param arg   The UpdateAction.
     */
    public void update(Observable o, Object arg) {
        switch ((UpdateAction)arg) {
            case NEW_GAME:
                setGame((Game)o);
                break;
            case CHECK:
                setGameCheck((Game)o);
                break;
            case SELECTED_NUMBER:
            case CANDIDATES:
            case HELP:
                setCandidates((Game)o);
                break;
        }
    }

    /**
     * Sets the fields corresponding to given game.
     *
     * @param game  Game to be set.
     */
    public void setGame(Game game) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                fields[y][x].setBackground(Color.WHITE);
                fields[y][x].setNumber(game.getNumber(x, y), game.getLang(), false);
            }
        }
    }

    /**
     * Sets fields validity according to given game.
     *
     * @param game  Current game.
     */
    private void setGameCheck(Game game) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                fields[y][x].setBackground(Color.WHITE);
                if (fields[y][x].getForeground().equals(Color.BLUE))
                    fields[y][x].setBackground(game.isCheckValid(x, y) ? Color.GREEN : Color.RED);
            }
        }
    }

    /**
     * Shows the candidates according to given game.
     *
     * @param game  Current game.
     */
    private void setCandidates(Game game) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                fields[y][x].setBackground(Color.WHITE);
                if (game.isHelp() && game.isSelectedNumberCandidate(x, y))
                    fields[y][x].setBackground(COLOR_CANDIDATE);
            }
        }
    }

    /**
     * Adds controller to all sub panels.
     *
     * @param sudokuController  Controller which controls all user actions.
     */
    public void setController(SudokuController sudokuController) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++){
                panels[y][x].addMouseListener(sudokuController);
                panels[y][x].addMouseMotionListener(sudokuController);
            }
        }
    }
    
}