package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class JBoardGame extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final int WIDTH, HEIGHT;
	
	private final int N, GRIDLEN;
	
	private ButtonGroup group;
	private JCase[] jtbCase;
			
	public JBoardGame(int n){
		super();
		
		// initialisation des paramètres.
		this.N = n;
		this.GRIDLEN = this.N*this.N;
		this.HEIGHT = this.WIDTH = this.GRIDLEN *50;
		this.jtbCase = new JCase[this.GRIDLEN*this.GRIDLEN];
		
		for(int i=(this.GRIDLEN*this.GRIDLEN)-1; i>=0; i--)
			this.jtbCase[i] = new JCase();
		
		// intialisation de la fenêtre.
		this.setSize();
		this.initComponents();
	}
	
	public int getSelectedIndex(){
		int i = 0;
		for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            
            if (button.isSelected()) {
            	return i;
            }
         
            i++;
        }
		return -1;
	}
	
	private void setSize(){
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
	}
	
	private void initComponents(){
		this.setBackground(new Color(209, 209, 224));
		this.setLayout(new GridLayout(this.N,this.N));
				
		group = new ButtonGroup();
		
		JPanel[] JpSquare = new JPanel[this.GRIDLEN];
		int [] maskSquare = new int[this.GRIDLEN*this.GRIDLEN];
				
		for(int i=0; i<this.GRIDLEN; i++){
			JpSquare[i] = new JPanel(new GridLayout(this.N,this.N));
			JpSquare[i].setBorder(new LineBorder(Color.DARK_GRAY));
		}
		
		int square=0;
		for(int i=0; i<this.GRIDLEN; i+=this.N){
			for(int j=0; j<this.GRIDLEN; j+=this.N){
				for(int k=0; k<this.N; k++){
					for(int l=0; l<this.N; l++){
						maskSquare[(i+k)*this.GRIDLEN+(j+l)] = square;
					}	
				} // fin du carre
				square++;
			}	
		}	
						
		for(int i=0; i<this.GRIDLEN*this.GRIDLEN; i++){
			this.jtbCase[i].setBorder(new LineBorder(Color.BLACK,1));
			this.jtbCase[i].setFont(new Font("Serif", Font.PLAIN, 24));
			this.jtbCase[i].setForeground(Color.BLACK);
			this.jtbCase[i].setFocusPainted(false);
			this.group.add(this.jtbCase[i]);
			this.add(this.jtbCase[i]);
		}
		
		for(int i=0; i<this.GRIDLEN; i++){
			this.add(JpSquare[i]);
		}
		
		for(int i=0; i<this.GRIDLEN*this.GRIDLEN; i++){
			Color bg = ((maskSquare[i])%2 == 0)?Color.WHITE: Color.LIGHT_GRAY;
			this.jtbCase[i].setUnlockBackground(bg);
			JpSquare[maskSquare[i]].setBackground(bg);
			JpSquare[maskSquare[i]].add(this.jtbCase[i]);
		}
		
	}
	
	void setValidityLine(int x,int y, boolean validity){
		LineBorder border = new LineBorder((validity == true)?Color.BLACK:Color.red);
		for(int i=0; i<this.GRIDLEN;i++){
			this.jtbCase[x*this.GRIDLEN + i].setBorder(border);	
		}
	}
	
	void setValidityLine(int position, boolean validity){
		this.setValidityLine(position/this.GRIDLEN,position%this.GRIDLEN,validity);
	}
	
	void setValidityColumn(int x,int y, boolean validity){
		LineBorder border = new LineBorder((validity == true)?Color.BLACK:Color.red);
		for(int i=0; i<this.GRIDLEN;i++){
			this.jtbCase[i*this.GRIDLEN + y].setBorder(border);
		}
	}
	
	void setValidityColumn(int position, boolean validity){
		this.setValidityColumn(position/this.GRIDLEN,position%this.GRIDLEN,validity);
	}
		
	void setValiditySquare(int x,int y, boolean validity){
		LineBorder border = new LineBorder((validity == true)?Color.BLACK:Color.red);
		int xSquare = x-(x%this.N) , ySquare = y-(y%this.N);
	    for (int i=xSquare ; i < xSquare+3; i++){
	        for (int j=ySquare; j < ySquare+3; j++){
	        	this.jtbCase[i*this.GRIDLEN + j].setBorder(border);
	        }
	    }
	}
	
	void setValiditySquare(int position, boolean validity){
		this.setValiditySquare(position/this.GRIDLEN,position%this.GRIDLEN,validity);
	}

	public Boolean LoadGrid(Vector<String[]> generateNewGrid) {
		int position = 0;
		
		if(generateNewGrid == null )
			return false;
		
		for(String[] line : generateNewGrid){
			for(String col : line){
				jtbCase[position].setValue(col);
				if(Integer.parseInt(col) >0) jtbCase[position].setEnabled(false);
				position++;
			}
		}
		return true;
	}

	public Boolean ResolveGrid(String[] strings) {
			
		if(strings == null )
			return false;
		
		for(int i=1; i<strings.length; i++) {
				if(!jtbCase[i-1].isEnabled() && !jtbCase[i-1].getValue().equals(strings[i]))
					return false;
				
				jtbCase[i-1].setValue(strings[i]);

		}
		return true;
	}
	
	public Boolean updateSelectedButton(String newValueButton) {
		int position = getSelectedIndex();
		
		if(position < 0)
			return false;
		
		jtbCase[position].setValue(newValueButton);
		return true;
	}

	public Boolean lockSelectedCase() {
		int position = getSelectedIndex();
		
		if(position < 0)
			return false;
		
		jtbCase[position].setLock(!jtbCase[position].isLock());
		if(jtbCase[position].isLock()) group.clearSelection();
		
		return true;
	}

	public boolean getStateSelectedCase() {
		int position = getSelectedIndex();
		
		if(position < 0)
			return false;
		
		return jtbCase[position].isLock();
	}

	public Boolean updateButton(int i, String value) {
		
		if(i< 0)
			return false;
		
		jtbCase[i].setValue(value);
		return true;
	}
	
}
