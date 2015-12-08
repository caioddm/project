package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import model.Game;

public class EditController implements ActionListener {
	private Game game;
	
	public EditController(Game game) {
		this.game = game;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		AbstractButton abstractButton = (AbstractButton) e.getSource();
        boolean selected = abstractButton.getModel().isSelected();
        if(e.getActionCommand().equals("Erase"))
        	this.game.setEraseMode(selected);
        else if(e.getActionCommand().equals("Rough")) {
        	if(this.game.isRoughModeOn())
        		game.setPrevMove(true);
        	game.setRoughMode(selected);
        }
	}
}
