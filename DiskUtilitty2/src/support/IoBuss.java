package support;

public class IoBuss  implements ICore, IioBuss {
	static IoBuss instance = new IoBuss();
	static Core core = Core.getInstance();

	
	public static IoBuss getInstance() {
		return instance;
	}// getInstance
	
	private IoBuss() {
	}// Constructor

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Read from a location. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to get the returned value
	 * @return value at specified location
	 */
	@Override
	public synchronized byte read(int location) {
		return core.read(location);
	}// readForIO

	/**
	 * Read consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to start reading
	 * @param length
	 *            how many locations to return
	 * @return the values read
	 */
	public synchronized byte[] readDMA(int location, int length) {
		byte[] readDMA = new byte[length];
		if (isValidAddressDMA(location, length)) {
			for (int i = 0; i < length; i++) {
				readDMA[i] = core.read(location + i);
			}// for
		} else {
			readDMA = null;
		}// if
		return readDMA;
	}// readDMA

	/**
	 * Write to a location. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to put the value
	 * @param value
	 *            what to put into specified location
	 */
	@Override
	public synchronized void write(int location, byte value) {
		core.write(location, value);
	}// writeForIO

	/**
	 * Write consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            starting address for write
	 * @param values
	 *            actual values to be written
	 */
	@Override
	public synchronized void writeDMA(int location, byte[] values) {
		int numberOfBytes = values.length;
		if (isValidAddressDMA(location, numberOfBytes)) {
			for (int i = 0; i < numberOfBytes; i++) {
				core.write(location + i, values[i]);
			}// for
		}// if
	}// writeDMA

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Gets memory size
	 * 
	 * @return the size of the memory in bytes
	 */
	@Override
	public int getSize() {
		return core.getSize();
	}// getSize

	/**
	 * Gets memory size in K
	 * 
	 * @return the size of the memory in K (1024)
	 */
	@Override
	public int getSizeInK() {
		return core.getSizeInK();
	}// getSizeInK

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
		return core.isValidAddress(location);
	}// isValidAddress

	/**
	 * 
	 * @param location
	 *            - starting address to be checked
	 * @param length
	 *            - for how many locations
	 * @return true if address range is valid
	 */

	private boolean isValidAddressDMA(int location, int length) {
		boolean isValidAddressDMA;
		if (!isValidAddress(location)) {	// starting location
			isValidAddressDMA = false;
		} else if (!isValidAddress(location + length-1)) {	// ending location
			isValidAddressDMA = false;
		} else {		// all is well
			isValidAddressDMA = true;
		}// if
		return isValidAddressDMA;
	}// checkAddressDMA

}// class IoBuss
