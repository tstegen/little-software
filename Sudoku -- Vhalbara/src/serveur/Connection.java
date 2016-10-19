package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;

public class Connection extends Thread{
	
	private final String FACILE = "0 8 7 9 0 0 0 0 0 0 0 0 0 7 1 0 6 0 0 0 0 4 8 0 0 5 7 0 1 2 5 0 0 6 7 0 3 0 0 0 1 0 0 0 4 0 5 9 0 0 4 1 2 0 8 9 0 0 4 2 0 0 0 0 2 0 7 5 0 0 0 0 0 0 0 0 0 8 2 3 0";
	
	private Vector<Vector<Integer>> baseGrid, fixedMask;
	private int n=3, gridLen = n*n ;
	
	Socket socket;
	BufferedReader entree;
	PrintStream sortie;
	
	public Connection() {
		
	}
	
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
		
		generateNewGrid(Integer.parseInt(sub[0]));
		
		sortie.println(getCurrentGrid());
		
		logger("<Service> New grid send.");
	}
	
	synchronized private void SendCurrentGrid() throws IOException{
		String texte;
		texte=entree.readLine();
		String sub [] = texte.split("[|]");
		
		generateNewGrid(Integer.parseInt(sub[0]));
		
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
		
		int temp = getGridAt(Integer.parseInt(sub[0]));
		
		setGridAt(Integer.parseInt(sub[0]),Integer.parseInt(sub[1]));
		
		logger("<Service> Last value " + temp + " - New value " + getGridAt(Integer.parseInt(sub[0])) + ".");
		
		sortie.println(true);
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
		if(sub[0].equalsIgnoreCase("line")){
			texte = "line";
			verif = CheckLine(Integer.parseInt(sub[1]));
		} else {
			if(sub[0].equalsIgnoreCase("column")){
				texte = "column";
				verif = CheckColumn(Integer.parseInt(sub[1]));
			} else {
				if(sub[0].equalsIgnoreCase("square")){
					texte = "square";
					verif = CheckSquare(Integer.parseInt(sub[1]));
				}
			}
		}
		
		logger("<Service> "+ texte + " " + verif + ".");
		
		sortie.println(verif);
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
			
			while(!(texte=entree.readLine()).equalsIgnoreCase("disconnect")){
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
	
	synchronized void generateNewGrid(int n){
		this.n=n; 
		this.gridLen = n*n ;
		String sub[] = FACILE.split(" ") ;
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
	
	synchronized Boolean CheckLine(int x, int num){
		for(int i=0; i<this.gridLen;i++){
			if(getGridAt(x,i) == num) 
				return false;		
		}
		return true;
	}
	
	synchronized Boolean CheckLine(int position){
		return this.CheckLine(position/this.gridLen,position%this.gridLen);
	}
	
	synchronized Boolean CheckColumn(int x,int y){
		for(int i=0; i<this.gridLen;i++){
			if(getGridAt(i,y) == getGridAt(x,y) && i!=x) 
				return false;	
		}
		return true;
	}
	
	synchronized Boolean CheckColumn(int position){
		return this.CheckColumn(position/this.gridLen,position%this.gridLen);
	}
		
	synchronized Boolean CheckSquare(int x,int y){
		int xSquare = x-(x%this.n) , ySquare = y-(y%this.n);
	    for (int i=xSquare ; i < xSquare+3; i++){
	        for (int j=ySquare; j < ySquare+3; j++){
	        	if (this.getGridAt(i, j) == this.getGridAt(x, y) && (i!=x || j!=y))
	            	return false;
	        }
	    }
	    return true;
	}
	
	synchronized Boolean CheckSquare(int position){
		return this.CheckSquare(position/this.gridLen,position%this.gridLen);
	}
	
	synchronized Boolean VerifyBoardGame(){
		for(int i=0; i<this.gridLen*this.gridLen;i++){
			if(!CheckLine(i) || !CheckColumn(i) || !CheckSquare(i)){
				return false;
			}
		}
		return true;
	}
}
