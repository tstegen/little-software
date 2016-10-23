package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

	/**
	 * @param args Parametre de la ligne de commande
	 */
	public static void main(String[] args) {
		int portEcoute = 10309;
		ServerSocket serveurSock;	
		Socket socket;
		try {
			serveurSock = new ServerSocket(portEcoute);
			System.out.println("Le serveur a bien démarré.");
			while(true) {
				System.out.println();
				socket = serveurSock.accept();
				new Connection(socket);
			}
		} catch(IOException exc) {
			System.out.println("probleme de connexion");
		}
	}	
}
