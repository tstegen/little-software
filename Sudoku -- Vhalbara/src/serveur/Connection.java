package serveur;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;

import serveur.tool.ExecuteCpp;

public class Connection extends Thread{
	private final String PATH = "bin/serveur";

	private final String EASY = PATH + "/grid/sudokufacile.txt";
	private final String MED = PATH + "/grid/sudokumoyen.txt";
	private final String HARD = PATH + "/grid/sudokudifficile.txt";
	private final String DEVIL = PATH + "/grid/sudokudiabolique.txt";

	private Vector<Vector<Integer>> baseGrid, fixedMask;
	private int n=3, gridLen = n*n ;

	Socket socket;
	BufferedReader entree;
	PrintStream sortie;

	public Connection (Socket socket) {
		this.socket = socket;
		Vector<Integer> line = new Vector<>();
		for(int i=0; i<this.gridLen; i++){
			line.add(new Integer(0));
		}

		this.fixedMask = new Vector<>(this.gridLen);
		this.baseGrid = new Vector<>(this.gridLen);

		for(int i=0; i<this.gridLen; i++){
			this.baseGrid.add(i,new Vector<>(line));
			this.fixedMask.add(i,new Vector<>(line));
		}
		try {
			this.entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.sortie = new PrintStream(socket.getOutputStream());
			this.start();
		} catch(IOException exc) {
			try {
				socket.close();
			}
			catch(IOException e){}
		}
	}
	synchronized int getGridAt(int x, int y){
		return baseGrid.get(x).get(y);
	}

	synchronized int getGridAt(int position){
		return this.getGridAt(position/this.gridLen, position%this.gridLen);
	}

	synchronized void setGridAt(int x, int y, int value){
		if(fixedMask.get(x).get(y) == 0){
			baseGrid.get(x).set(y,new Integer(value));
		} else {
			logger("<Serveur> La valeur de cette case (int"+ x + ",int" + y +") est fixée.");
		}
	}

	synchronized private void SendNewGrid() throws IOException{
		String fin, texte ="" ;

		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end")){
				texte += fin;
			}
			System.out.println(texte);
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		String sub [] = texte.split("[|]");

		String grid = ReadGrid(Integer.parseInt(sub[0]),sub[1]);
		
		generateNewGrid(Integer.parseInt(sub[0]),grid);

		sortie.println(grid);

		logger("<Service> New grid send.");
	}

	synchronized private void SendCurrentGrid() throws IOException{
		String texte;
		texte=entree.readLine();
		String sub [] = texte.split("[|]");

		//generateNewGrid(Integer.parseInt(sub[0]));

		sortie.println(getCurrentGrid());

		logger("<Service> Current grid send.");
	}

	synchronized private void UpdateGrid() throws IOException{
		String fin, texte ="" ;

		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end")){
				texte += fin;
			}
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		String sub [] = texte.split("[|]");
		int ind = Integer.parseInt(sub[0]);
		if(ind >= 0){
			int temp = getGridAt(ind);

			setGridAt(ind,Integer.parseInt(sub[1]));

			logger("<Service> Last value " + temp + " - New value " + getGridAt(Integer.parseInt(sub[0])) + ".");

			sortie.println(true);	
		} else {
			logger("<Service> Mise à jour impossible.");

			sortie.println(false);				
		}

	}

	synchronized private void CheckingGrid() throws IOException{
		String fin, texte ="" ;

		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end")){
				texte += fin;
			}
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		String sub [] = texte.split("[|]");

		Boolean verif = false;
		int val, pos = Integer.parseInt(sub[1]);
		
		if(fixedMask.get(pos/this.gridLen).get(pos%this.gridLen) == 0){
			val = getGridAt(pos);
			this.setGridAt(pos,0);
			
			if(sub[0].equalsIgnoreCase("line")){
				texte = "line";
				verif = CheckLine(pos/this.gridLen, val);
			} else {
				if(sub[0].equalsIgnoreCase("column")){
					texte = "column";
					verif = CheckColumn(pos%this.gridLen, val);
				} else {
					if(sub[0].equalsIgnoreCase("square")){
						texte = "square";
						verif = CheckSquare(pos,val);
					}
				}
			}
			this.setGridAt(pos,val);
		}

		logger("<Service> "+ texte + " is " + verif + ".");

		sortie.println("this " + texte + " is " + verif + ".");
	}

	synchronized private void CheckingBoardGame() throws IOException{

		String fin;

		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end"))
				System.out.println(fin);
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		Boolean verif = VerifyBoardGame();

		logger("<Service> this boardgame is " + verif + ".");
		sortie.println(verif);
	}
	
	synchronized private void ResolveGrid() throws IOException{

		String fin;

		try {
			while(!(fin=entree.readLine()).equalsIgnoreCase("|end"))
				System.out.println(fin);
		} catch (IOException e) {
			System.out.println("Probleme d'entree-sortie");
		}

		String reponse =ExecuteCpp.StartCommand("SudokuSolver.exe " + this.getCurrentGrid().replaceAll("\\s", "-"));
		
		sortie.println(reponse);
	}

	private void SendErrorCode() throws IOException{
		logger("<Service> Error.");
		sortie.println(25);
	}

	public void logger(String log) {
		System.out.println(log);
	}

	public void run() {
		int chx;
		String texte, sub [];
		try {

			while((texte=entree.readLine())!=null && !(texte).equalsIgnoreCase("disconnect")){
				sub = texte.split("[|]");
				System.out.println(texte);

				try{
					chx = Integer.parseInt(sub[0]);
				} catch(NumberFormatException e) {
					chx = -1;
				}

				switch(chx){
				case 1:
					this.SendNewGrid();
					break;
				case 2:
					this.SendCurrentGrid();
					break;
				case 3:
					this.UpdateGrid();
					break;
				case 4:
					this.CheckingGrid();
					break;
				case 5:
					this.CheckingBoardGame();
					break;
				case 6:
					this.ResolveGrid();
				default:
					this.SendErrorCode(); // code d'erreur (25)
				}

				sortie.println("|end");
			}
			sortie.close();
			entree.close();
			socket.close();
		}	catch(IOException e) {
			System.exit(0);
		}
	}

	synchronized void setGridAt(int position, int value){
		this.setGridAt(position/this.gridLen, position%this.gridLen, value);
	}
	
	synchronized void generateNewGrid(int n, String grid){
		this.n=n; 
		this.gridLen = n*n ;
		String sub[] = grid.split(" ");
		this.fixedMask = new Vector<>(this.gridLen);
		this.baseGrid = new Vector<>(this.gridLen);
		Vector<Integer> line = new Vector<>(this.gridLen), lineMask = new Vector<>(this.gridLen);

		for(int i=0; i<this.gridLen; i++){
			for(int j=0; j<this.gridLen; j++){
				line.add(j,new Integer(sub[i*this.gridLen+j]));
				lineMask.add(j,new Integer((Integer.parseInt(sub[i*this.gridLen+j])>0)?1:0));
			}
			this.baseGrid.add(i,new Vector<>(line));
			this.fixedMask.add(i,new Vector<>(line));
		}
	}

	synchronized private String ReadGrid(int n, String lvl){
		
		// Selectionne le fichier à lire
		String file = lvl.equalsIgnoreCase("facile")?this.EASY:lvl.equalsIgnoreCase("intermediare")?this.MED:lvl.equalsIgnoreCase("difficile")?this.HARD:this.DEVIL;
		
		try{
			InputStream ips=new FileInputStream(file); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line,grid="";
			if((line=br.readLine())!=null){
				grid += line;
				while ((line=br.readLine())!=null){
					grid += " " + line;
				}
			}
			br.close(); 
			return grid;
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		
		return null;
	}	
	
	synchronized String getCurrentGrid(){
		String grid = Integer.toString(getGridAt(0,0));
		for(int i=0; i<this.gridLen; i++){
			for(int j=(i>0)?0:1; j<this.gridLen; j++){
				grid += " " + Integer.toString(getGridAt(i,j));
			}
		}
		logger("<Serveur> the grid is " + grid + ".");
		return grid;
	}

	synchronized Boolean CheckLine(int x, int value){
		for(int i=0; i<this.gridLen;i++){
			if(getGridAt(x,i) == value) 
				return false;		
		}
		return true;
	}

	synchronized Boolean CheckColumn(int y, int value){
		for(int i=0; i<this.gridLen;i++){
			if(getGridAt(i,y) == value) 
				return false;	
		}
		return true;
	}

	synchronized Boolean CheckSquare(int x,int y, int value){
		int xSquare = x-(x%this.n) , ySquare = y-(y%this.n);
		for (int i=xSquare ; i < xSquare+3; i++){
			for (int j=ySquare; j < ySquare+3; j++){
				if (this.getGridAt(i, j) == value)
					return false;
			}
		}
		return true;
	}

	synchronized Boolean CheckSquare(int position, int value){
		return this.CheckSquare(position/this.gridLen,position%this.gridLen, value);
	}

	synchronized Boolean VerifyBoardGame(){
		int val;
		for(int i=0; i<this.gridLen*this.gridLen;i++){
			if(fixedMask.get(i/this.gridLen).get(i%this.gridLen) == 0){
				val = getGridAt(i);
				this.setGridAt(i,0);
				
				if(!CheckLine(i/this.gridLen,val) || !CheckColumn(i%this.gridLen,val) || !CheckSquare(i,val)){
					return false;
				}		
			
				this.setGridAt(i,val);
			}
		}
		return true;
	}
}
