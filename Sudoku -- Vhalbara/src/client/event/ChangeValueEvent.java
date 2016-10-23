package client.event;

import java.util.EventObject;

public class ChangeValueEvent extends EventObject {
	
	private String newValue;
	 
	public ChangeValueEvent(Object source, String newValue){
		super(source);
		this.newValue = newValue;
	}
 
	public String getNewValueButton(){
		return newValue;
	}
}
