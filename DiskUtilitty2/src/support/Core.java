package support;

import java.util.Observable;

//import AppLogger;

//import memory.Core.Trap;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 * 
 *          <p> 
 *          Core is the class that acts as the physical memory.
 *          <p>
 *          Core is a base level object. It represents the memory in a virtual machine.
 * 
 *          The access to the data is handled by 3 sets of read and write operations:
 * 
 *          1) read, write, readWord, writeWord, popWord and pushWord all participate in the monitoring of the locations
 *          read for Debug Trap and written for IO trap. 2) readForIO and writeForIO are to be used for byte memory
 *          access by devices. They do not engage the Trap system. 3) readDMA and writeDMA are for burst mode reading
 *          and writing. They also do not engage the Trap system.
 * 
 *          Changed from using traps to Observer/Observable for IO/DEBUG?INVALID notification Traps for IO are triggered
 *          by the writes. Debug traps are triggered by the reads in conjunction with the isDebugEnabled flag
 * 
 *          <p>
 *          this is constructed as a singleton
 * 
 * 
 * 
 *
 */

public class Core extends Observable implements ICore {
	private static Core instance = new Core();
	private AppLogger log = AppLogger.getInstance();
	private  byte[] storage;
	private  int maxAddress;

	/**
	 * 
	 * @param size
	 *            The size of Memory to create first time
	 * @return The only instance of the core object
	 */
	public static Core getInstance() {
		return instance;
	}// getInstance


	private Core() {
		int size = 64 * 1024;
		storage = new byte[size];
		maxAddress = size - 1;
//		System.out.println("In core constructor");
	}// Constructor
		// <><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
	/**
	 * clears all memory locations to zeros
	 */
	public void initialize(){
		for ( int i = 0; i < maxAddress +1; i++){
			storage[i] = (byte)00;
		}//for
	}//initialize
	
	public  byte[] getStorage(){
		return this.storage.clone();
	}//getStorage

	/**
	 * Gets memory size
	 * 
	 * @return the size of the memory in bytes
	 */
	@Override
	public int getSize() {
		return storage.length;
	}// getSize

	/**
	 * Gets memory size in K
	 * 
	 * @return the size of the memory in K (1024)
	 */
	@Override
	public int getSizeInK() {
		return storage.length / K;
	}// getSizeInBytes

	/**
	 * Returns the value found at the specified location, and checks for DEBUG
	 * 
	 * @param location
	 *            where to get the value from
	 * @return value found at the location, or a HLT command if this is the first access to a debug marked location
	 */
//private int TARGET = 0XE9C8;
	@Override
	public byte read(int location) {
		byte result;
		if (isValidAddress(location)) {
			result= storage[location];
//			if (location==TARGET) {
//				System.out.printf("[Core.read] %04X : %02X%n", location,result);
//			}//if TARGET
		}else {
			result = (byte) 0X00;
			log.errorf("[Core.read] attempted to read from an invalid location: [%04X],(byte) 0X00 returned%n" , location);
		}//
		return   result; 
	}// read

	@Override
	public void write(int location, byte value) {
		if (isValidAddress(location)) {
			storage[location] = value;
//			if (location==TARGET) {
//				System.out.printf("[Core.write] %04X : %02X%n", location,value);
//			}//if TARGET
		}else {
			log.errorf("[Core.write] attempted to write to an invalid location: [%04X],%n" , location);
		}// if
		return; // bad address;
	}// write

	/**
	 * Confirms the location is in addressable memory.
	 * <p>
	 * Will fire an MemoryAccessError if out of addressable memory
	 * 
	 * @param location
	 *            - address to be checked
	 * @return true if address is valid
	 * 
	 */
	@Override
	public boolean isValidAddress(int location) {
		boolean checkAddress = true;
		if ((location < PROTECTED_MEMORY) | (location > maxAddress)) {
			checkAddress = false;
		}// if
		return checkAddress;
	}// isValidAddress



	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public enum Trap {
		HALT,IO, DEBUG, INVALID
	}// enum Trap

	static int K = 1024;
	static int PROTECTED_MEMORY = 0; // 100;
	static int MINIMUM_MEMORY = 16 * K;
	static int MAXIMUM_MEMORY = 64 * K;
	static int DEFAULT_MEMORY = 64 * K;

}// class Core
