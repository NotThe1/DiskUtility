package support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * this class' function is to load core memory with the values coded in Memory Image files. There are two types. The
 * first is the Intel ".HEX" files. The other is standard mem files
 * 
 * @author Frank Martyn
 *
 */

public class MemoryLoaderFromFile {
	static private IoBuss ioBuss = IoBuss.getInstance();
	static final private int memorySize = ioBuss.getSize();
	static private byte[] result;

//	static public byte[] loadMemoryImage(File sourceFile, int size) {
//		result = new byte[size];
//		loadMemoryImage(sourceFile);
//		return result.clone();
//
//	}//

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
//		loadMemoryImage(bufferedReader);
		parseAndLoadImageMem(new Scanner(bufferedReader));
		return result.clone();
	}

//	static public void loadMemoryImage(BufferedReader bufferedReader) {
//		parseAndLoadImageMem(new Scanner(bufferedReader));
//	}// loadMemoryImage - BufferedReader

	private static void parseAndLoadImageMem(Scanner scanner) {
		int byteIndex = 0;
		String strAddress;
		int address;
		byte[] values = new byte[SIXTEEN];

		while (scanner.hasNextLine()) {
			strAddress = scanner.next();
			strAddress = strAddress.replace(":", "");
			address = Integer.valueOf(strAddress, 16);

			if ((address + 0X0F) >= memorySize) {
				JOptionPane.showMessageDialog(null, "Address out of current memory on address line: " + strAddress,
						"Out of bounds", JOptionPane.ERROR_MESSAGE);
				scanner.close();
				return;
			} // if max memory test

			for (int i = 0; i < SIXTEEN; i++) {
				values[i] = (byte) ((int) Integer.valueOf(scanner.next(), 16));
			} // for values
//			if (result == null) {
//				ioBuss.writeDMA(address, values); // avoid setting off memory traps
//			} else {
				for (int i = 0; i < SIXTEEN; i++) {
					result[byteIndex++] = values[i];
				} // for values
//			} // if memory or array
			scanner.nextLine();
		} // while - next line
		scanner.close();

		return;

	}// parseAndloadImageMem - Scanner

//	static public void loadMemoryImage(File sourceFile) {
//		String[] nameParts = sourceFile.getName().split("\\.");
//		String memoryFileType = nameParts.length > 1 ? nameParts[1] : "No File Type";
//		// String thisFileName = null;
//		switch (memoryFileType) {
//		case MEMORY_SUFFIX:
//			parseAndLoadImageMem(sourceFile);
//			// thisFileName = sourceFile.getName();
//			break;
//		case MEMORY_SUFFIX1:
//			parseAndLoadImageHex(sourceFile);
//			// thisFileName = sourceFile.getName();
//			break;
//		default:
//			JOptionPane.showMessageDialog(null,
//					"File type is not either MEM or HEX :  " + sourceFile.getAbsolutePath().toString(),
//					"Illegal file type", JOptionPane.ERROR_MESSAGE);
//			break;
//		}// switch
//		return;
//	}// loadMemoryImage

	static private void parseAndLoadImageMem(File sourceFile) {
		int byteIndex = 0;
		try {
			String strAddress ="";
			int address;
			byte[] values = new byte[SIXTEEN];
			Scanner scanner = new Scanner(sourceFile);
			while (scanner.hasNext()) { //scanner.hasNextLine()

				strAddress = scanner.next();
				
				strAddress = strAddress.replace(":", "");
				address = Integer.valueOf(strAddress, 16);

				if ((address + 0X0F) >= memorySize) {
					JOptionPane.showMessageDialog(null, "Address out of current memory on address line: " + strAddress,
							"Out of bounds", JOptionPane.ERROR_MESSAGE);
					scanner.close();
					return;
				} // if max memory test

				for (int i = 0; i < SIXTEEN; i++) {
					values[i] = (byte) ((int) Integer.valueOf(scanner.next(), 16));
				} // for values
				if (result == null) {
					ioBuss.writeDMA(address, values); // avoid setting off memory traps
				} else {
					for (int i = 0; i < SIXTEEN; i++) {
						result[byteIndex++] = values[i];
					} // for values
				} // if memory or array
				scanner.nextLine();
			} // while - next line
			scanner.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, sourceFile.getAbsolutePath() + " Not Found", "File Not Found",
					JOptionPane.ERROR_MESSAGE);
			return;
		} // try
	}// parseAndLoadImageMem

	static private void parseAndLoadImageHex(File sourceFile) {
		Scanner scanner;
		String line;
		try {
			int byteCount, address, checksum, checksumValue;
			byte recordType, value;
			byte[] values;
			scanner = new Scanner(sourceFile);
			while (scanner.hasNext()) { //scanner.hasNextLine()
				line = scanner.next();
				line = line.replace(" ", ""); // remove any spaces
				if (line.length() == 0) {
					continue; // skip the line
				} // if
				if (line.startsWith(":") == false) {
					continue; // skip the line
				} // if

				byteCount = Integer.valueOf(line.substring(HEX_COUNT_START, HEX_COUNT_END), HEX_VALUE);
				// System.out.printf("byteCount: %02X%n", byteCount);

				address = Integer.valueOf(line.substring(HEX_ADDRESS_START, HEX_ADDRESS_END), HEX_VALUE);
				// System.out.printf("address: %04X%n", address);

				if ((address + byteCount) >= memorySize) {

					JOptionPane.showMessageDialog(null, "Address out of current memory. Failing line: " + line,
							"Out of bounds", JOptionPane.ERROR_MESSAGE);
					scanner.close();
					return;
				} // if max memory test

				recordType = (byte) ((int) (Integer.valueOf(line.substring(HEX_TYPE_START, HEX_TYPE_END), HEX_VALUE)));
//				System.out.printf("recordType: %02X%n", recordType);
				
				switch (recordType) {
				case DATA_RECORD:
					values = new byte[byteCount];
					checksum = byteCount + recordType
							+ Integer.valueOf(line.substring(HEX_ADDRESS_START, HEX_ADDRESS_BYTE_BOUNDARY), HEX_VALUE)
							+ Integer.valueOf(line.substring(HEX_ADDRESS_BYTE_BOUNDARY, HEX_ADDRESS_END), HEX_VALUE);
					for (int i = 0; i < byteCount; i++) {
						value = (byte) ((int) Integer
								.valueOf(line.substring((i * 2) + HEX_DATA_START, (i * 2) + HEX_DATA_END), HEX_VALUE));
						values[i] = value;
						checksum += value;
					} // for - data

					checksumValue = Integer.valueOf(
							line.substring((byteCount * 2) + HEX_DATA_START, (byteCount * 2) + HEX_DATA_END),
							HEX_VALUE);
					// System.out.printf("checksumValue: %02X%n", checksumValue);

					checksum = checksum + checksumValue;

					if ((checksum & 0xFF) != 0) {
						String msg = String.format("checksum error on address line: %s.", line.substring(3, 7));
						JOptionPane.showMessageDialog(null, msg, "CheckSum error", JOptionPane.ERROR_MESSAGE);
						return;
					} // if - checksum test

//					if (result == null) {
//						ioBuss.writeDMA(address, values); // avoid setting off memory traps
//					} // if Memory
					ioBuss.writeDMA(address, values); // avoid setting off memory traps

					break;
				case END_OF_FILE_RECORD:
					String msg = "End of File Record found!";
					System.out.println(msg);
					// JOptionPane.showMessageDialog(null, msg, "Hex memory loader", JOptionPane.INFORMATION_MESSAGE);
					break;
				case EXTENDED_SEGMENT_ADDRESS_RECORD:
					// Not coded
					break;
				case START_SEGMENT_ADDRESS_RECORD:
					// Not coded
					break;
				case EXTENDED_LINEAR_ADDRESS_RECORD:
					// Not coded
					break;
				case START_LINEAR_ADDRESS_RECORD:
					// Not coded
					break;
				default:
					// Not coded
				}// switch - record type

			} // while - next line
			scanner.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, sourceFile.getAbsolutePath() + " Not Found", "File Not Found",
					JOptionPane.ERROR_MESSAGE);
			return;
		} // try
	}// parseAndLoadImageHex

	private final static int SIXTEEN = 16;
	private final static int HEX_VALUE = 16;
	private final static String MEMORY_SUFFIX1 = "hex";
	private final static String MEMORY_SUFFIX = "mem";

	private final static byte DATA_RECORD = (byte) 0x00;
	private final static byte END_OF_FILE_RECORD = (byte) 0x01;
	private final static byte EXTENDED_SEGMENT_ADDRESS_RECORD = (byte) 0x02;
	private final static byte START_SEGMENT_ADDRESS_RECORD = (byte) 0x03;
	private final static byte EXTENDED_LINEAR_ADDRESS_RECORD = (byte) 0x04;
	private final static byte START_LINEAR_ADDRESS_RECORD = (byte) 0x05;

	private final static int HEX_COUNT_START = 1;
	private final static int HEX_COUNT_SIZE = 2;
	private final static int HEX_COUNT_END = HEX_COUNT_START + HEX_COUNT_SIZE;

	private final static int HEX_ADDRESS_START = 3;
	private final static int HEX_ADDRESS_BYTE_BOUNDARY = HEX_ADDRESS_START + 2;
	private final static int HEX_ADDRESS_SIZE = 4;
	private final static int HEX_ADDRESS_END = HEX_ADDRESS_START + HEX_ADDRESS_SIZE;

	private final static int HEX_TYPE_START = 7;
	private final static int HEX_TYPE_SIZE = 2;
	private final static int HEX_TYPE_END = HEX_TYPE_START + HEX_TYPE_SIZE;

	private final static int HEX_DATA_START = 9;
	private final static int HEX_DATA_SIZE = 2;
	private final static int HEX_DATA_END = HEX_DATA_START + HEX_DATA_SIZE;;

}// class MemoryLoaderFromFile
