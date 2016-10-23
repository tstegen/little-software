package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.EventListenerList;

import client.event.ChangeValueEvent;
import client.event.ChangeValueListener;

public class JOption extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final int WIDTH, HEIGHT;
	private final int GRIDLEN;
	
	private JButton ButtonLock;
	private JButton ButtonValue[];
			
	private EventListenerList  listeners = new EventListenerList();
	
	public JOption(int n){
		super();
		
		// initialisation des paramètres.
		this.GRIDLEN = n*n;
		this.WIDTH = this.GRIDLEN * 50;
		this.HEIGHT = 70;
		
		// intialisation de la fenêtre.
		this.setSize();
		this.initComponents();
		
	}

	public void setSize(){
		this.setPreferredSize(new Dimension(this.WIDTH, this.HEIGHT));
		this.setMinimumSize(new Dimension(this.WIDTH, this.HEIGHT));
		this.setMaximumSize(new Dimension(this.WIDTH, this.HEIGHT));
	}
	
	private void initComponents(){
		this.setBackground(new Color(209, 209, 224));
		
		JPanel panel = new JPanel();
		
		this.ButtonValue = new JButton[this.GRIDLEN+1] ;
		
		this.ButtonValue[0] = new JButton("");
		this.ButtonValue[0].setFocusPainted(false);
		this.ButtonValue[0].addActionListener(new NewValueAction());
		this.ButtonValue[0].setPreferredSize(new Dimension(50, 50));;
		panel.add(this.ButtonValue[0]);
		
		for(int i=1; i<=this.GRIDLEN;i++){
			this.ButtonValue[i] = new JButton(Integer.toString(i));
			this.ButtonValue[i].setPreferredSize(new Dimension(50, 50));
			this.ButtonValue[i].addActionListener(new NewValueAction());
			this.ButtonValue[i].setFocusPainted(false);
			panel.add(this.ButtonValue[i]);
		}
		
		// table unicon : 1f512 - outils-javascript.aliasdmc.fr/encodage-icone-symbole

		this.ButtonLock = new JButton(new ImageIcon("./bin/img/cadena-ouvert.png"));
		this.ButtonLock.setPreferredSize(new Dimension(50, 50));
		this.add(this.ButtonLock);
		
		JScrollPane scrollpane = new JScrollPane();
		panel.setPreferredSize(new Dimension(this.WIDTH-70, 115));
		scrollpane.setViewportView(panel);
		scrollpane.setPreferredSize(new Dimension(this.WIDTH-60, 60));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollpane);
	}
	
	public void addLockListener (ActionListener li){
		this.ButtonLock.addActionListener(li);
	}
	
	public void addChangeValueListener (ChangeValueListener li){
		listeners.add(ChangeValueListener.class,li);
	}
 
	public void removeChangeValueListener (ChangeValueListener li){
		 listeners.remove(ChangeValueListener.class, li);
	}
 
	private void fireValueChanged(String str){
		ChangeValueListener[] listenerList = (ChangeValueListener[])listeners.getListeners(ChangeValueListener.class);
 
		for(ChangeValueListener listener : listenerList){
			listener.ChangeValueButton(new ChangeValueEvent(this, str));
		}
	}
	
	private class NewValueAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton button = (JButton)arg0.getSource();
			fireValueChanged((button.getText().equals(""))?"0":button.getText());
		}
		
	};
	
}
