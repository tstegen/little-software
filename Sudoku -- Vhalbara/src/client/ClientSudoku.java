package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.tools.DiagnosticCollector;

import client.event.ChangeValueEvent;
import client.event.ChangeValueListener;
import client.tool.Chrono;

/**
 * @author thomas
 *
 */
public class ClientSudoku extends JFrame{

	private static final long serialVersionUID = 1L;

	// paramètre du client
	int portEcouteServeur;
	BufferedReader entree;
	PrintStream sortie;
	Socket socket;

	// paramètre de la fenêtre
	private ImageIcon trophee = new ImageIcon("bin/img/trophee.png");
	private JBoardGame boardGame;
	private JOption joption;

	// paramètre
	private Chrono chrono;
	
	public ClientSudoku (){
		super();
		
		// paramètre
		
		this.chrono = new Chrono();
		
		// paramètrage du client
		
		this.portEcouteServeur = 10309;
		try {
			if((socket = new Socket("localhost", portEcouteServeur))!=null){
				entree = new BufferedReader(new	InputStreamReader(socket.getInputStream()));
				sortie = new PrintStream( socket.getOutputStream());
			}
		} catch(FileNotFoundException exc) {
			System.out.println("Fichier	introuvable");
		} catch(UnknownHostException exc) {
			System.out.println("Destinataire inconnu");
		} catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie");
		}
		
		// paramètrage de la fenêtre	
		
		this.initNewGame(3,"Facile");
		this.initComponents();
		this.initMenu();
		
	}

	/* ****************************************************** *
	 
    	  Fonction de gestion des composantes graphiques

	 * ****************************************************** */
	
	private void initNewGame(int i, String lvl) {
		
		if(this.boardGame != null) this.remove(this.boardGame);
		if(this.joption != null) this.remove(this.joption);
		
		this.boardGame = new JBoardGame(i);
		this.boardGame.LoadGrid(generateNewGrid(i,lvl));
		
		this.joption = new JOption(i);
		this.joption.addChangeValueListener(new ChangeValueButtonAction());
		this.joption.addLockListener(new LockButtonAction());
		
		this.add(this.boardGame, BorderLayout.CENTER);
		this.add(this.joption, BorderLayout.SOUTH);
		
		this.revalidate();
		
		this.chrono.start();
	}

	private void initComponents(){	
		this.setResizable(false);
		this.pack();
	} 

	private void initMenu(){
		this.addWindowListener (new WindowAdapter(){
			public void windowClosing (WindowEvent e){
				dispose();
				try {
					if(socket != null) socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JMenuBar menubar = new JMenuBar();

		JMenu menu = new JMenu("Jeu");

		JMenuItem menuItem = new JMenuItem("Nouvelle partie");
		menuItem.addActionListener(new NewGameMenuAction(this));
		menu.add(menuItem);

		menuItem = new JMenuItem("Resoudre");
		menuItem.addActionListener(new ResolveGridAction());
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Verifier");
		menuItem.addActionListener(new VerifyGridAction());
		menu.add(menuItem);

		menubar.add(menu);

		this.setJMenuBar(menubar);

	}
	
	/* ****************************************************** *
	 
	  	  Fonction de gestion du réseau

	 * ****************************************************** */
	
	public String[] sendToServeur(String[] message){
		if(socket != null){
			for(String texte : message){
				sortie.println(texte);
			}

			String line = "";
			
			Vector<String> texte = new Vector<>();
			
			try {
				while(!(line=entree.readLine()).equalsIgnoreCase("|end"))
					texte.add(line);
					
				return texte.toArray(new String[texte.size()]);
				
			} catch (IOException e) {
				System.out.println("Probleme d'entree-sortie");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Imposible de se connecter au serveur.", "Serveur non joignable", JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}
	
	/* ****************************************************** *
	 
	  	  Fonction propre à la classe

	 * ****************************************************** */
	
	private Vector<String[]> generateNewGrid(int i, String lvl){
		String[] reply = sendToServeur(new String[] {"1"+"|cmd",i+"|"+lvl,"|end"});
		
		Vector<String[]> grid = new Vector<>();
		
		if(reply != null){
			for(String texte : reply){
				grid.add(texte.split(" "));
			}
			return grid;
		}
		
		return null;
	}

	private Boolean updateGrid(String newValueButton){
		if(!this.boardGame.getStateSelectedCase()){
			String[] reply = sendToServeur(new String[] {"3"+"|cmd",boardGame.getSelectedIndex()+"|"+newValueButton,"|end"});
			
			if(reply != null && reply[0].contains("true")){
				return this.boardGame.updateSelectedButton(newValueButton);
			}
					
		}

		return false;	
	}
	
	/* ****************************************************** *
	 
	 	  Lanceur de l'application
	 
	 * ****************************************************** */
	public static void main(String[] argv){

		ClientSudoku controller = new ClientSudoku();

		controller.setVisible(true);

	}

	/* ****************************************************** *
	 
    	  Classe de gestion des évènements

	 * ****************************************************** */
	
	private class NewGameMenuAction implements ActionListener{

		JFrame frame;
		
		NewGameMenuAction (JFrame frame){
			this.frame = frame;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JInitGameDia dialog = new JInitGameDia(null);
			Sudoku sudo = dialog.showDialog();
			initNewGame(sudo.getN(),sudo.getLvl());
			
		}
		
	}
	
	private class ChangeValueButtonAction implements ChangeValueListener{

		@Override
		public void ChangeValueButton(ChangeValueEvent event) {
			
			System.out.println(event.getNewValueButton());
			
			if(!updateGrid(event.getNewValueButton()))
				JOptionPane.showMessageDialog(null, "Une erreur est survenue.", "Valeur incorrecte", JOptionPane.ERROR_MESSAGE);
			
		}

	};
	
	private class LockButtonAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			boardGame.lockSelectedCase();
			
		}

	};

	private class ResolveGridAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] reply = sendToServeur(new String[] {"6"+"|cmd","resolve toi.","|end"});
			
			String strings[] = reply[0].split(" ");
					
			for(int i=1; i<strings.length; i++) {
				reply = sendToServeur(new String[] {"3"+"|cmd",i-1+"|"+strings[i],"|end"});
				
				if(reply != null && reply[0].contains("true")){
					boardGame.updateButton(i-1,strings[i]);
				}

			}
			
		}

	};
	
	private class VerifyGridAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			chrono.pause();
			String[] reply = sendToServeur(new String[] {"5"+"|cmd","VerifyMyGridPlease","|end"});
			
			if(reply != null && reply[0].contains("true")){
				chrono.stop();
				JOptionPane.showMessageDialog(null, "Félicitation, tu as fini en " + chrono.getDureeSec() + "sec.", "Valeur incorrecte", JOptionPane.INFORMATION_MESSAGE, trophee);
			} else {
				chrono.resume();
			}
			
		}

	};
	
}
