package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class Client {
	int portEcouteServeur;
	BufferedReader entree;
	PrintStream sortie;
	Socket socket;
	
	public Client(){
		this.portEcouteServeur = 10309;
		try {
			socket = new Socket("localhost", portEcouteServeur);
			entree = new BufferedReader(new	InputStreamReader(socket.getInputStream()));
			sortie = new PrintStream( socket.getOutputStream());
		} catch(FileNotFoundException exc) {
			System.out.println("Fichier	introuvable");
		} catch(UnknownHostException exc) {
			System.out.println("Destinataire inconnu");
		} catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie");
		}
	}
	
	public String[] getNewGrid(int n) {
			
		sortie.println("1"+"|cmd");
		sortie.println(n+"|0");
		sortie.println("|end");
		
		String fin = "", texte = "";
		
		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end")){
				texte += fin;
			}
			System.out.println(fin);
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		return (!texte.equals(""))?texte.split(" "):null;

	}

	public boolean updateGrid(int selectedIndex, String text) {

		sortie.println("3"+"|cmd");
		sortie.println(selectedIndex+"|"+text);
		sortie.println("|end");

		String fin = "", texte = "";
		
		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end")){
				texte += fin;
			}
			System.out.println(fin);
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}
		
		return texte.contains(texte);
	}

	public boolean VerifyCase(int selectedIndex, int i) {
		String texte;
		boolean verif = true;

		sortie.println("4"+"|cmd");
		sortie.println(((i==0)?"line":(i==1)?"column":"square") + "|" + selectedIndex);
		sortie.println("|end");
		
		try{		
			while(!(texte=entree.readLine()).equals("|end")){
				verif = (texte.contains("true"))?true:false;
			}

		} catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie");
		}
		return verif;
	}

	public boolean verifyGrid() {
		String texte;
		boolean verif = true;
		try{
			
			sortie.println("5"+"|cmd");
			sortie.println("|end");
			
			while(!(texte=entree.readLine()).equals("|end")){
				verif = (texte.contains("true"))?true:false;
			}

		} catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie");
		}
		return verif;
	}
	
	public void closeServer() {
		try{
			sortie.println("disconnect");
			sortie.close();
			entree.close();
			socket.close();
			
		} catch(IOException exc) {
			System.out.println("Probleme d'entree-sortie");
		}
		
	}
}
