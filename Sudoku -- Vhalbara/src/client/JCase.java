package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

public class JCase extends JToggleButton {
	
	private static final long serialVersionUID = -5421486791715699876L;

	private String value;
	
	private boolean lock = false;
	
	private Color borderColor = Color.BLACK;
	private Color unlockBackground = Color.WHITE;
	private Color lockBackground = Color.CYAN;
	private Color colorDisableFont = new Color(0, 102, 204);
	private Color colorEnableFont = Color.BLACK;
	
	public JCase(){
		this.setBackground(Color.WHITE);
	}
	
	/**
	 * @return the lock
	 */
	public boolean isLock() {
		return lock;
	}

	/**
	 * @param lock the lock to set
	 */
	public void setLock(boolean lock) {
		if(lock) this.setSelected(false);
		this.lock = lock;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return (this.value.equals(""))?"0":value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		if(!this.isLock()){
			this.value = (value.equals("0"))?"":value;
			this.setText(this.value);			
		}
	}
	
	/**
	 * @return the unlockBackground
	 */
	public Color getUnlockBackground() {
		return unlockBackground;
	}

	/**
	 * @param unlockBackground the unlockBackground to set
	 */
	public void setUnlockBackground(Color unlockBackground) {
		this.unlockBackground = unlockBackground;
	}

	/**
	 * @return the lockBackground
	 */
	public Color getLockBackground() {
		return lockBackground;
	}

	/**
	 * @param lockBackground the lockBackground to set
	 */
	public void setLockBackground(Color lockBackground) {
		this.lockBackground = lockBackground;
	}
	
	

	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @param colorDisableFont the colorDisableFont to set
	 */
	public void setColorDisableFont(Color colorDisableFont) {
		this.colorDisableFont = colorDisableFont;
	}

	/**
	 * @return the colorEnableFont
	 */
	public Color getColorFont() {
		return (this.isEnabled())?colorEnableFont:colorDisableFont;
	}

	/**
	 * @param colorEnableFont the colorEnableFont to set
	 */
	public void setColorEnableFont(Color colorEnableFont) {
		this.colorEnableFont = colorEnableFont;
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#getBackground()
	 */
	@Override
	public Color getBackground() {
		return this.isLock()?this.getLockBackground():this.getUnlockBackground();
	}
		
	public void paint(Graphics g){
		paintComponents(g);
		paintBorder(g);
		super.paintChildren(g);
	}
		
	public void paintComponents(Graphics g){
		super.paintComponents(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.clearRect(0, 0, this.getWidth(),this.getHeight());

		g2.setColor((this.isSelected())?Color.YELLOW:(this.isLock())?getLockBackground():getUnlockBackground());
		g2.fillRect(0, 0, this.getWidth(),this.getHeight());
		
	    String text = this.getText(); 
	    if(!text.equals("")){
		    Font font = this.getFont();
		    
	    	int textWidth  = getStringWidth(g2,font,text);
		    int textHeight = getStringHeight(g2,font,text);
		    int textAscent = getStringAscent(g2,font,text);

		    int x = (this.getWidth() - textWidth)/2;
		    int y = (this.getHeight() - textHeight)/2 + textAscent;
		    
		    g2.setColor(this.getColorFont());
		    g2.setFont(font);
		    g2.drawString(text, x, y);	
	    }
	}

	public void paintBorder(Graphics g){
		Border border = BorderFactory.createLineBorder(borderColor);
		
		if (border != null) {
				border.paintBorder(this, g, 0, 0, this.getWidth(),this.getHeight());
		}
		
	}
	
	public int getStringWidth(Graphics area, Font f, String s) {
	    // Find the size of string s in the font of the Graphics context "page"
	    FontMetrics fm   = area.getFontMetrics(f);
	    java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, area);
	    return (int)Math.round(rect.getWidth());
	  }

	  public int getStringHeight(Graphics area, Font f, String s) {
	    // Find the size of string s in the font of the Graphics context "page"
	    FontMetrics fm   = area.getFontMetrics(f);
	    java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, area);
	    return (int)Math.round(rect.getHeight());
	  }

	  public int getStringAscent(Graphics area, Font f, String s) {
	    // Find the size of string s in the font of the Graphics context "page"
	    FontMetrics fm   = area.getFontMetrics(f);
	    return fm.getAscent();
	  }
}
