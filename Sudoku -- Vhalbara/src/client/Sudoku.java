/**
 * 
 */
package client;

/**
 * @author thomas
 *
 */
public class Sudoku {

	private String lvl;
	private int n;
	
	public Sudoku(int n, String lvl){
		this.lvl = lvl;
		this.n = n;
	}
	
	/**
	 * @return the lvl
	 */
	public String getLvl() {
		return lvl;
	}
	/**
	 * @param lvl the lvl to set
	 */
	public void setLvl(String lvl) {
		this.lvl = lvl;
	}
	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}
	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		this.n = n;
	}
	
	
}
