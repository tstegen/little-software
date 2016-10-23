package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class JInitGameDia extends JDialog {

	private static final long serialVersionUID = 1L;

	private Sudoku param = new Sudoku(3, "Facile");
	private ButtonGroup group = new ButtonGroup();
	JRadioButton jListRadio[];
	
	public JInitGameDia (JFrame frame){
		super(frame, true);
		this.initComponents();
	}
	
	private void initComponents(){
		this.setLayout(new GridLayout(0, 2));
		
		JButton jEasyButton = new JButton("Facile");
		JButton jInterButton = new JButton("Moyen");
		JButton jHardButton = new JButton("Difficile");
		JButton jDevilButton = new JButton("Diabolique");
		
		JButton jListButton [] = new JButton[] { jEasyButton, jInterButton, jHardButton, jDevilButton };
		
		for(JButton button : jListButton){
			button.addActionListener(new ValidateAction());
			this.add(button);
		}
		
		JRadioButton x2 = new JRadioButton("2 x 2");
		JRadioButton x3 = new JRadioButton("3 x 3");
		JRadioButton x4 = new JRadioButton("4 x 4");
		JRadioButton x5 = new JRadioButton("5 x 5");
		
		jListRadio = new JRadioButton[] { x2, x3, x4, x5 };
		
		x3.setSelected(true);
		for(JRadioButton button : jListRadio){
			group.add(button);
			this.add(button);
		}		
		
		this.pack();
	}
	
	public Sudoku showDialog(){
		this.setVisible(true);
		return param;
	}
	
	private class ValidateAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JButton button = (JButton) arg0.getSource();
			
			param.setLvl(button.getText());
			
			if(jListRadio[0].isSelected())
				param.setN(2);
			if(jListRadio[1].isSelected())
				param.setN(3);
			if(jListRadio[2].isSelected())
				param.setN(4);
			if(jListRadio[3].isSelected())
				param.setN(5);
				
			dispose();
		}
		
	};
}
