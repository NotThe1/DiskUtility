/**
 * 
 */
package support;

/**
 * @author Frank Martyn June 2016
 ** @version 1.0
 * 
 *          <p>
 *          ICore is the interface that acts as the physical memory.
 *          <p> 
 *          ICore is a base level interface. It represents the memory in a virtual machine.
 */
public interface ICore {
	/**
	 * Gets memory size
	 * 
	 * @return the size of the memory in bytes
	 */
	public int getSize();

	/**
	 * Gets memory size in K
	 * 
	 * @return the size of the memory in K (1024)
	 */
	public int getSizeInK();

	/**
	 * Returns the value found at the specified location
	 * 
	 * @param location
	 *            where to get the value from
	 * @return value found at the location
	 */

	public byte read(int location);

	/**
	 * Places value into memory
	 * 
	 * @param location
	 *            where to put the value in memory
	 * @param value
	 *            what to put into memory
	 */
	public void write(int location, byte value);
	
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

	public boolean isValidAddress(int location);

}// interface ICore
