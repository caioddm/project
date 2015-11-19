package painting;
 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
*
* @author meenakshi_n
*
*/

@SuppressWarnings("serial")
public class DrawArea extends JComponent {
 
  // Image in which we're going to draw
  private Image image;
  // Graphics2D object ==> used to draw on
  private Graphics2D g2;
  // Mouse coordinates
  private int currentX, currentY, oldX, oldY;
  private Figure figure;
  
  public DrawArea() {
    
	  figure = new Figure(this.getWidth(), this.getHeight());
	  initFigure();
	  
	  setDoubleBuffered(false);
	  addMouseListener(new MouseAdapter() {
	      public void mousePressed(MouseEvent e) {
	    	  // save coord x,y when mouse is pressed
	    	  figure.getStrokes(figure.getCurIter())
	    	  		.add(new Stroke());
	    	  		
		      oldX = e.getX();
		      oldY = e.getY();
	      }
	  });
 
	  addMouseMotionListener(new MouseMotionAdapter() {
	      public void mouseDragged(MouseEvent e) {
	        // coord x,y when drag mouse
	        currentX = e.getX();
	        currentY = e.getY();
	        ArrayList<Stroke> strokes = figure.getStrokes(figure.getCurIter());
	        strokes.get(strokes.size()-1).addPoint(new Point(currentX,
	        												 currentY));
	        //System.out.println("Mouse at " + currentX+","+currentY);
	        if (g2 != null) {
	          // draw line if g2 context not null
	          g2.drawLine(oldX, oldY, currentX, currentY);
	          // refresh draw area to repaint
	          repaint();
	          // store current coords x,y as olds x,y
	          oldX = currentX;
	          oldY = currentY;
	        }
	      }
	  });
    
  }
 
  protected void paintComponent(Graphics g) {
    if (image == null) {
      // image to draw null ==> we create
      image = createImage(getSize().width, getSize().height);
      g2 = (Graphics2D) image.getGraphics();
      // enable antialiasing
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
    		  			  RenderingHints.VALUE_ANTIALIAS_ON);
      // clear draw area
      clear();
    }
 
    g.drawImage(image, 0, 0, null);
  }
 
  public void reset() {
	  g2.setPaint(Color.white);
	    // draw white on entire draw area to clear
	    g2.fillRect(0, 0, getSize().width, getSize().height);
	    g2.setPaint(Color.black);
	    repaint();  
  }
  // now we create exposed methods
  public void clear() {
	  reset();
	  //for(int i = 0; i < figure.numIters; ++i)
		  //figure.getStrokes(i).clear();
	  figure = new Figure(this.getWidth(), this.getHeight());
  }

  public void initFigure() {
	  figure.setWidth(this.getWidth());
	  figure.setHeight(this.getHeight());
  }
  
  public Figure getFigure() {
	  return this.figure;
  }
 
}