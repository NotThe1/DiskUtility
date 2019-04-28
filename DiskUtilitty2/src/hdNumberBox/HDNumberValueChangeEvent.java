package hdNumberBox;

import java.util.EventObject;

public class HDNumberValueChangeEvent extends EventObject  {
	private static final long serialVersionUID = 1L;
	
	int oldValue;
	int currentValue;
	/**
	 * Constructor 
	 * @param source identifies the origin of the event
	 * @param oldValue prior value for SeekPanel
	 * @param currentValue new value for the SeekPanel
	 */

	public HDNumberValueChangeEvent(Object source,int oldValue,int currentValue) {
		super(source);
		this.oldValue=oldValue;
		this.currentValue = currentValue;
		// TODO Auto-generated constructor stub
	}//Constructor
	
	public int getOldValue(){
		return this.oldValue;
	}//getOldValue

	public int getNewValue(){
		return this.currentValue;
	}//getNewValue

}//class HDNumberValueChangeEvent 
