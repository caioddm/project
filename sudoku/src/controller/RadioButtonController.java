package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Game;
import view.Field;

public class RadioButtonController implements ActionListener {
	private Game game;
	
	public RadioButtonController(Game game) {
		this.game = game;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//if(e.getActionCommand().equals(Integer.toString(Field.ARABIC)))
			//game.setLang(Field.ARABIC);
		if(e.getActionCommand().equals(Integer.toString(Field.HINDI)))
			game.setLang(Field.HINDI);
		else if(e.getActionCommand().equals(Integer.toString(Field.CHINESE)))
			game.setLang(Field.CHINESE);
		
	}
}
