package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import view.SudokuPanel.SubPanel;

/**
 * This class represents a field on the SudokuPanel.
 *
 * @author Eric Beijer
 */
public class Field extends JLabel {
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
        setPreferredSize(new Dimension(40, 40));
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
    public void setNumber(int number, boolean userInput) {
        setForeground(userInput ? Color.BLUE : Color.BLACK);
        setText(number > 0 ? number + "" : "");
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