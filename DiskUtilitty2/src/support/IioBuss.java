package support;

/**
 * 
 * @author Frank Martyn
 * @version 1.0
 * 
 *          <p>
 *          IioBuss acts as the physical memory - I/O device interaction.
 *          <p> 
 *          It represents the I/O devices access to memory in a virtual machine.
 * 
 *          The distinguishing characteristics of I/O interaction with the memory is that it does not participate in any
 *          of the Trap mechanism that is memory based (I/O or Debug). It also moves data in bulk through the DMA
 *          mechanism
 * 
 *
 */
public interface IioBuss {
	/**
	 * Read consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            where to start reading
	 * @param length
	 *            how many locations to return
	 * @return the values read
	 */
	public byte[] readDMA(int location, int length);

	/**
	 * Write consecutive locations. Bypasses the memory trap apparatus
	 * 
	 * @param location
	 *            starting address for write
	 * @param values
	 *            actual values to be written
	 */
	public void writeDMA(int location, byte[] values);
}// interface IioBuss