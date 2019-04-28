package support;

import java.io.BufferedReader;
import java.util.Scanner;

/**
 * this class' function is to load core memory with the values coded in Memory Image files. There are two types. The
 * first is the Intel ".HEX" files. The other is standard mem files
 * 
 * @author Frank Martyn
 *
 */

public class MemoryLoaderFromFile {
//	static private IoBuss ioBuss = IoBuss.getInstance();
//	static final private int memorySize = ioBuss.getSize();
	static private byte[] result;

	/**
	 * this method will take the string representation of memory found in the file sent to it and load memory with its
	 * contents. The files may be either "hex" or "mem" file types
	 * 
	 * @param sourceFile
	 *            file that contains the string representation of memory
	 * @return byte array loaded into memory
	 */

	static public byte[] loadMemoryImage(BufferedReader bufferedReader, int size) {
		result = new byte[size];
		parseAndLoadImageMem(new Scanner(bufferedReader));
		return result.clone();
	}//loadMemoryImage

	private final static int SIXTEEN = 16;
	
	private static void parseAndLoadImageMem(Scanner scanner) {
		int byteIndex = 0;
		String strAddress;

		while (scanner.hasNextLine()) {
			strAddress = scanner.next();
			strAddress = strAddress.replace(":", "");

			for (int i = 0; i < SIXTEEN; i++) {
				result[byteIndex++] = (byte) ((int) Integer.valueOf(scanner.next(), 16));
			} // for values
			scanner.nextLine();
		} // while - next line
		scanner.close();

		return;

	}// parseAndloadImageMem - Scanner


}// class MemoryLoaderFromFile
