package hexEditPanel;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

//import hexEditDisplay.HexEditDisplayPanel;

public class HexFilter extends DocumentFilter {
	private HexEditDisplayPanel host;
	private AsciiForms asciiForms = AsciiForms.getInstance();
	private boolean dataChanged;

	public HexFilter() {
	}// Constructor

	public HexFilter(HexEditDisplayPanel host) {
		this.host = host;
		HEUtility.makeStyles();
		dataChanged = false;
	}// Constructor

	public boolean isDataChanged() {
		return dataChanged;
	}// isDataChanged

	public void setDataChanged(boolean state) {
		dataChanged = state;
	}// setDataChanged

	// @Override
	// public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
	// throws BadLocationException {
	// fb.insertString(offset, string.toUpperCase(), attr);
	// }// insertString

	// public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
	// fb.remove(offset, length);
	// }// remove

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {

		int dataDot;

		if ((offset % COLUMNS_PER_LINE) < LAST_COLUMN_DATA) { // data
			Matcher m = onehexPattern.matcher(text);
			if ((length == 0) && m.matches()) { // replace one Hex digit char
				fb.replace(offset, 1, text.toUpperCase(), HEUtility.dataAttributes);

				/* HexString representation */
				Document doc = fb.getDocument();
				int offsetTemp = offset == 0 ? offset : offset - 1;
				String dataString = doc.getText(offsetTemp, 3).trim();
				asciiForms.setString(dataString);

				/* ascii representation */
				int asciiDot = HEUtility.getAsciiDot(offset);
				fb.replace(asciiDot, length + 1, asciiForms.getAsciiForm(), HEUtility.asciiAttributes);

				/* replace value in original map */
				host.updateValue(offset, asciiForms.getByteForm(),EditAtom.SOURCE_DATA);
				dataChanged = true;
				/* replace value in original map */

			} else {
				return; // do nothing
			} // if data
		} else {// Ascii
			if (asciiForms.setAscii(text)) {
				dataDot = HEUtility.getDataDot(offset);
				fb.replace(dataDot, 2, asciiForms.getStringForm(), HEUtility.dataAttributes);
				fb.replace(offset, length + 1, text, HEUtility.asciiAttributes);

				/* replace value in original map */
				host.updateValue(offset, asciiForms.getByteForm(),EditAtom.SOURCE_ASCII);
				dataChanged = true;
				/* replace value in original map */
			} else {
				// do nothing
			} // if printable
		} // if

		host.setDot(offset + 1); // reset the caret for highlighting
	}// replace

	Pattern onehexPattern = Pattern.compile("[0123456789ABCDEFabcdef]{1}");
	// private static final String UNPRINTABLE = HEUtility.UNPRINTABLE;
	public static final String PRINTABLES = HEUtility.PRINTABLES;
	private static final int LAST_COLUMN_DATA = HEUtility.LAST_COLUMN_DATA;
	private static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE;

}// class HexDocumentFilter
