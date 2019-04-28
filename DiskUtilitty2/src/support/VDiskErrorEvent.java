package support;

import java.util.EventObject;

public class VDiskErrorEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long value;
	private String errorMessage;

	public VDiskErrorEvent(Object source, long value, String errorMessage) {
		super(source);
		this.value=value;
		this.errorMessage=errorMessage;
		// TODO Auto-generated constructor stub
	}//Constructor
	
	public long getValue(){
		return value;
	}//getValue
	
	public String getMessage(){
		return String.format("Error type: %s%n value: %d - 0X%X", errorMessage,value,value);
	}//getMessage

}//class VDiskError
