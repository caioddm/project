package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import controller.RadioButtonController;

public class LanguagePanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JRadioButton btnArabic, btnHindi, btnChinese;
	ButtonGroup btnGroup;

	/**
	 *  Constructor
	 */
	public LanguagePanel() {
		super(new BorderLayout());
		JPanel pnlAlign = new JPanel();
        pnlAlign.setLayout(new BoxLayout(pnlAlign, BoxLayout.PAGE_AXIS));
        add(pnlAlign, BorderLayout.NORTH);

        JPanel pnlOptions = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlOptions.setBorder(BorderFactory.createTitledBorder(" Language "));
        pnlAlign.add(pnlOptions);
        
        /*btnArabic = new JRadioButton("Arabic");
        btnArabic.setFocusable(false);
        btnArabic.setSelected(true);
        btnArabic.setActionCommand(Integer.toString(Field.ARABIC));
        pnlOptions.add(btnArabic);*/
        
        btnHindi = new JRadioButton("Hindi");
        btnHindi.setFocusable(false);
        btnHindi.setSelected(false);
        btnHindi.setActionCommand(Integer.toString(Field.HINDI));
        pnlOptions.add(btnHindi);
        
        btnChinese = new JRadioButton("Chinese");
        btnChinese.setFocusable(false);
        btnChinese.setSelected(false);
        btnChinese.setActionCommand(Integer.toString(Field.CHINESE));
        pnlOptions.add(btnChinese);
        
        btnGroup = new ButtonGroup();
        //btnGroup.add(btnArabic);
        btnGroup.add(btnHindi);
        btnGroup.add(btnChinese);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setController(RadioButtonController rButtonController) {
		//btnArabic.addActionListener(rButtonController);
		btnHindi.addActionListener(rButtonController);
		btnChinese.addActionListener(rButtonController);
	}
}
