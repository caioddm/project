package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import view.SudokuPanel.SubPanel;

/**
 * This class represents a field on the SudokuPanel.
 *
 * @author Eric Beijer
 */
public class Field extends JLabel {
	
	public static class NumberSet {
		private static final String[][] numbers = {
				{"1", "2", "3", "4", "5", "6", "7", "8", "9"},
				{"\u0967", "\u0968", "\u0969", "\u096a", "\u096b",
				 "\u096c", "\u096d", "\u096e", "\u096f"},
				{"\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94",
				 "\u516d", "\u4e03", "\u516b", "\u4e5d"}
		};
	}
	
	public static final int ARABIC = 0;
	public static final int HINDI = 1;
	public static final int CHINESE = 2;
    
	private int x;      // X position in game.
    private int y;      // Y position in game.
    private SubPanel parentPanel;
    /**
     * Constructs the label and sets x and y positions in game.
     *
     * @param x     X position in game.
     * @param y     Y position in game.
     */
    public Field(int x, int y, SubPanel parent) {
        super("", CENTER);
        this.x = x;
        this.y = y;
        this.parentPanel = parent;
        setPreferredSize(new Dimension(80, 80));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        setOpaque(false);
    }

    /**
     * Sets number and foreground color according to userInput.
     *
     * @param number        Number to be set.
     * @param userInput     Boolean indicating number is user input or not.
     */
    public void setNumber(int number, int lang, boolean userInput) {
        setForeground(userInput ? Color.BLUE : Color.BLACK);
        String num = number > 0? NumberSet.numbers[lang][number-1] + "" : "";
        setText(num);
    }

    /**
     * Returns x position in game.
     *
     * @return  X position in game.
     */
    public int getFieldX() {
        return x;
    }

    /**
     * Return y position in game.
     *
     * @return  Y position in game.
     */
    public int getFieldY() {
        return y;
    }
    
    public SubPanel getParentPanel(){
    	return parentPanel;
    }
    
    
}