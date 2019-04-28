package hexEditPanel;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/* HexEditor Utility */
public class HEUtility {

	 static SimpleAttributeSet addressAttributes;
	 static SimpleAttributeSet dataAttributes;
	 static SimpleAttributeSet asciiAttributes;

	public static void makeStyles() {
		SimpleAttributeSet baseAttributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(baseAttributes, "Courier New");
		StyleConstants.setFontSize(baseAttributes, 16);

		addressAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(addressAttributes, Color.GRAY);

		dataAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(dataAttributes, Color.BLACK);

		asciiAttributes = new SimpleAttributeSet(baseAttributes);
		StyleConstants.setForeground(asciiAttributes, Color.BLUE);

	}// makeStyles1

	public static int getAsciiDot(int hexDot) {
		int position = hexDot % COLUMNS_PER_LINE; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_DATA;
		int lineNumber = hexDot / COLUMNS_PER_LINE;
		int rawByteIndex = pastMidLine ? position - 1 : position; // >=
		int byteIndex = rawByteIndex / CHARS_PER_BYTE_DATA;

		int lineStart = lineNumber * COLUMNS_PER_LINE;

		int otherByteIndex = (byteIndex * CHARS_PER_BYTE_ASCII) + ASCII_COL_START;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return lineStart + otherByteIndex;

	}// getAsciiDot

	public static int getDataDot(int asciiDot) {
		int lineNumber = asciiDot / COLUMNS_PER_LINE;
		int position = asciiDot % COLUMNS_PER_LINE; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		int rawByteIndex = pastMidLine ? position - 1 : position; // >=

		int byteIndex = (rawByteIndex - ASCII_COL_START) / CHARS_PER_BYTE_ASCII;

		int lineStart = lineNumber * COLUMNS_PER_LINE;
		int otherByteIndex = byteIndex * CHARS_PER_BYTE_DATA;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return lineStart + otherByteIndex;
	}// getDataDot

	public static int getDataDotEnd(int startDataDot) {
		int position = startDataDot % COLUMNS_PER_LINE; // Calculate the column position
		boolean pastMidSpace = position > MID_LINE_SPACE_DATA;
		position = pastMidSpace ? position - 1 : position;
		return (2 - (position % CHARS_PER_BYTE_DATA)) + startDataDot;
	}// getDataDotEnd

	public static int getAddressDot(int asciiDot) {
		int lineNumber = asciiDot / COLUMNS_PER_LINE;
		return lineNumber * COLUMNS_PER_LINE_ADDR;
	}// getAddressDot

	public static int getIndexDot(int asciiDot) {
		int position = asciiDot % COLUMNS_PER_LINE; // Calculate the column position
		boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		int rawByteIndex = pastMidLine ? position - 1 : position; // >=
		int byteIndex = (rawByteIndex - ASCII_COL_START) / CHARS_PER_BYTE_ASCII;
		int otherByteIndex = byteIndex * CHARS_PER_BYTE_ADDR;
		otherByteIndex = pastMidLine ? otherByteIndex + 1 : otherByteIndex;// >=

		return otherByteIndex;

	}// getIndexDot

	public static int getAsciiSourceIndex(int asciiDot) {
		 int position = asciiDot % COLUMNS_PER_LINE; // Calculate the column position
		 boolean pastMidLine = position > MID_LINE_SPACE_ASCII;
		 int lineNumber = asciiDot / COLUMNS_PER_LINE;
		 int rawByteIndex = pastMidLine? position - 1 : position;
		 int byteIndex = rawByteIndex / CHARS_PER_BYTE_ASCII;
		
		 return ((lineNumber * BYTES_PER_LINE) + byteIndex) - ASCII_COL_START;
	}// getAsciiSourceIndex

//	public static int getDataSourceIndex(int dataDot) {
//		 int position = dataDot % COLUMNS_PER_LINE; // Calculate the column position
//		 boolean pastMidLine = position > MID_LINE_SPACE_DATA;
//		 int lineNumber = dataDot / COLUMNS_PER_LINE;
//		 int rawByteIndex = pastMidLine? position - 1 : position; 
//		 int byteIndex = rawByteIndex / CHARS_PER_BYTE_DATA;
//		
//		 return (lineNumber * BYTES_PER_LINE) + byteIndex;
//
//	}// getAsciiSourceIndex
	
	public static int getSourceIndex(int dot) {
		 int lineNumber = dot / COLUMNS_PER_LINE;
		 int position = dot % COLUMNS_PER_LINE; // Calculate the column position
		 boolean pastMidLine;
		 int charPerByte;
		 int colStart;
		 if (position >= ASCII_COL_START) {//Ascii
			 pastMidLine = position > MID_LINE_SPACE_ASCII;
			 charPerByte = CHARS_PER_BYTE_ASCII;
			 colStart = ASCII_COL_START;
		 }else {//Data
			 pastMidLine = position > MID_LINE_SPACE_DATA;
			 charPerByte = CHARS_PER_BYTE_DATA;
			 colStart = DATA_COL_START;
		 }//if data or ascii
		 int rawByteIndex = pastMidLine? position - 1 : position;
		 int byteIndex = rawByteIndex/ charPerByte;
		 return ((lineNumber * BYTES_PER_LINE) + byteIndex) - colStart;


		
	}//getSourceIndex

	/* Constants */

	public static final int BYTES_PER_LINE = 16; // Number of bytes from source file to display on each line
	public static final int MID_LINE_START = BYTES_PER_LINE/2; // 
	/* Data Text Constants */

	public static final int DATA_COL_START = 0;
	public static final int CHARS_PER_BYTE_DATA = 3; // Number of chars used to display on each byte on a line
	public static final int MID_LINE_SPACE_DATA = (BYTES_PER_LINE / 2) * CHARS_PER_BYTE_DATA;
	public static final int LAST_COLUMN_DATA = (BYTES_PER_LINE * CHARS_PER_BYTE_DATA + 1) - 1;

	/* Ascii Text Constants */
	public static final int ASCII_COL_START = LAST_COLUMN_DATA + 1;
	public static final int CHARS_PER_BYTE_ASCII = 1; // Number of chars used to display on each byte on a line
	public static final int MID_LINE_SPACE_ASCII = ((BYTES_PER_LINE / 2) * CHARS_PER_BYTE_ASCII) + ASCII_COL_START;
	public static final int LAST_COLUMN_ASCII = (BYTES_PER_LINE * CHARS_PER_BYTE_ASCII + 1) + ASCII_COL_START;
	public static final int COLUMNS_FOR_ASCII = LAST_COLUMN_ASCII-ASCII_COL_START-1;

	public static final int COLUMNS_PER_LINE = LAST_COLUMN_ASCII + 2;// CR,LF

	/* Address Text Constants */
	public static final int BYTES_PER_LINE_ADDR = 8; // Number of bytes from source file to display on each line
	public static final int CHARS_PER_BYTE_ADDR = 3; // Number of chars used to display on each byte on a line
	public static final int CHARS_PER_LINE_ADDR = (BYTES_PER_LINE_ADDR) + 1; // Number of chars displayed in the Address
																				// text pane
	public static final int COLUMNS_PER_LINE_ADDR = CHARS_PER_LINE_ADDR + 2; // actual length of the line< includes /n/r

	public static final String UNPRINTABLE = ".";

	/* @formatter:off */
	public static final String PRINTABLES = " !\"#$%&'()*+,-./" +
	                                        "0123456789:;<=>?" +
			                                "@ABCDEFGHIJKLMNO" +
	                                        "PQRSTUVWXYZ[\\]^_" +
			                                "`abcdefghijklmno" +
	                                        "pqrstuvwxyz{|}~" ;
/* @formatter:on  */
}// Class TextCell
