package serveur.tool;

import java.io.InputStream;

public class ExecuteCpp {
	
	public static String StartCommand(String command) {
		try {
			//creation du processus
			Process p = Runtime.getRuntime().exec(command);
			InputStream in = p.getInputStream();

			//on recupere le flux de sortie du programme

			StringBuilder build = new StringBuilder();
			char c = (char) in.read();		

			while (c != (char) -1) {
				build.append(c);
				c = (char) in.read();
			}

			String response = build.toString();

			//on l'affiche
			return response;
		}
		catch (Exception e) {
			System.out.println("\n" + command + ": commande inconnu ");
		}
		return null;
	} 
}
