package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import controller.ButtonController;
import controller.EditController;
import controller.RadioButtonController;
import controller.SudokuController;
import model.Game;

/**
 * Main class of program.
 *
 * @author Eric Beijer
 */
public class Sudoku extends JFrame {
    public Sudoku() {
        super("Sudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        Game game = new Game();

        JPanel subButtonPanel = new JPanel();
        subButtonPanel.setLayout(new GridLayout(3,0,0,0));
        
        ButtonController buttonController = new ButtonController(game);
        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.setController(buttonController);
        subButtonPanel.add(buttonPanel);

        RadioButtonController rButtonController = new RadioButtonController(game);
        LanguagePanel languagePanel = new LanguagePanel();
        languagePanel.setController(rButtonController);
        subButtonPanel.add(languagePanel);
        
        EditController editController = new EditController(game);
        EditPanel editPanel = new EditPanel();
        editPanel.setController(editController);
        subButtonPanel.add(editPanel);
        
        add(subButtonPanel, BorderLayout.EAST);
        
        SudokuPanel sudokuPanel = new SudokuPanel();
        SudokuController sudokuController = new SudokuController(sudokuPanel, game);
        //sudokuPanel.setGame(game);
        sudokuPanel.setController(sudokuController);
        add(sudokuPanel, BorderLayout.CENTER);

        game.addObserver(buttonPanel);
        game.addObserver(sudokuPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Main entry point of program.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Use System Look and Feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ex) { ex.printStackTrace(); }
        new Sudoku();
    }
}