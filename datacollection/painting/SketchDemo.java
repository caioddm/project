package painting;
 
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sketchxmldom.CreateSketchXMLDOM;

 
public class SketchDemo {
 
  JButton clearBtn, nextBtn;
  JLabel numDisplay, iterDisplay;
  DrawArea drawArea;
  String drawSymbolText;
  int count;
  List<Figure> figures;
  
  ActionListener actionListener = new ActionListener() {
 
  public void actionPerformed(ActionEvent e) {
	  boolean flag = false;
	  
	  if (e.getSource() == clearBtn) {
        drawArea.clear();
      }
      else if(e.getSource() == nextBtn) {
    	  Figure f = drawArea.getFigure();

    	  if(f.getCurIter() == f.numIters-1) {
    		  drawArea.reset();
    		  numDisplay.setText(drawSymbolText+ count + ". ");
	    	  if(count <= 9)
	    		  figures.set(count-1, f);
	    	  drawArea.clear();
	    	  count++;
	    	  
	    	  numDisplay.setText(drawSymbolText+ count + ". ");
	    	  iterDisplay.setText("Iteration #"+(f.getCurIter()+1));
    	  
    	  // If all 9 numbers are complete
	    	  if(count == 10) {
	    		  CreateSketchXMLDOM.writeToXML("TrainingData.xml", figures);
	    		  nextBtn.setEnabled(false);
	    		  //drawArea.setEnabled(false);
	    		  numDisplay.setText("All numbers complete. Please close window");
	    		  iterDisplay.setText("");
	    		  flag = true;
	    	  }
    	  }
    	  
    	  if(!flag) {
	    	  drawArea.reset();
	    	  f.incCurIter();
	    	  numDisplay.setText(drawSymbolText+ count + ". ");
	    	  iterDisplay.setText("Iteration #" + (f.getCurIter()+1));
    	  }
      }
    }
  };
 
  public SketchDemo() {
	 count = 1;
	 figures = new ArrayList<Figure>(9);
	 for(int i = 0; i < 9; ++i)
		 figures.add(null);
	 
	 drawSymbolText = "Draw the symbol for the number ";
	 drawArea = new DrawArea();
  }
  
  public static void main(String[] args) {
    new SketchDemo().show();
  }
 
  public void show() {
    // create main frame
    JFrame frame = new JFrame("Sketch - SuDoKu");
    Container content = frame.getContentPane();
    // set layout on content pane
    content.setLayout(new BorderLayout());
    // create draw area

 
    // add to content pane
    content.add(drawArea, BorderLayout.CENTER);
 
    // create controls to apply colors and call clear feature
    JPanel subPanel = new JPanel();	
    subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
    
    JPanel controls = new JPanel();
    JPanel numbers = new JPanel();
 
    clearBtn = new JButton("Clear");
    clearBtn.addActionListener(actionListener);
    nextBtn = new JButton("Next");
    nextBtn.addActionListener(actionListener);
 
    // add to panel
    controls.add(clearBtn);
    controls.add(nextBtn);
    
    count = 1;
    
    numDisplay = new JLabel();
    numDisplay.setText(drawSymbolText + count + ". ");
    iterDisplay = new JLabel();
    iterDisplay.setText("Iteration #"+(drawArea.getFigure().getCurIter()+1));
    numbers.add(numDisplay);
    numbers.add(iterDisplay);
    
    // add to content pane
    subPanel.add(controls);
    subPanel.add(numbers);
    
    content.add(subPanel, BorderLayout.NORTH);
 
    frame.setSize(600, 600);
    // can close frame
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // show the swing paint result
    frame.setVisible(true);
    
  }
  
}