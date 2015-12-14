package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import controller.EditController;

public class EditPanel extends JPanel implements Observer {

	JToggleButton btnEdit, btnRough;

	public EditPanel() {
		super(new BorderLayout());
		JPanel pnlAlign = new JPanel();
        pnlAlign.setLayout(new BoxLayout(pnlAlign, BoxLayout.PAGE_AXIS));
        add(pnlAlign, BorderLayout.NORTH);

        JPanel pnlOptions = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlOptions.setBorder(BorderFactory.createTitledBorder(" Edit "));
        pnlAlign.add(pnlOptions);
        
        btnEdit = new JToggleButton("Erase");
        pnlOptions.add(btnEdit);
        
        btnRough = new JToggleButton("Rough");
        pnlOptions.add(btnRough);
        
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setController(EditController editController) {
		btnEdit.addActionListener(editController);
		btnRough.addActionListener(editController);
	}
}
