package hexEditPanel;
/*
 * @version 1.1
 * @date  2018-06-12
 * 
 * Adding a ChangeEvent to manage a dataChange one-time event
 * 
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.SortedMap;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import appLogger.AppLogger;
import hdNumberBox.HDNumberBox;
import hdNumberBox.HDNumberValueChangeEvent;
import hdNumberBox.HDNumberValueChangeListener;



/*
 *     2018-08-18  - added limit for address box
 *     2018-09-01  - added access to EditCaretaker list
*/


public class HexEditDisplayPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private AppLogger log = AppLogger.getInstance();

	private AdapterHexEditDisplay adapterHexEditDisplay = new AdapterHexEditDisplay();
	protected ByteBuffer source;
	protected int dataSize;

	private int currentMax;
	private int currentLineStart;
	private int currentExtent;

	protected StyledDocument addrDoc;
	protected StyledDocument indexDoc;

	protected StyledDocument hexDoc;
	protected HexFilter hexFilter = new HexFilter(this);
	protected HexNavigation hexNavigation;

	protected SortedMap<Integer, Byte> changes;
	EditCaretaker editCaretaker = new EditCaretaker();

	private SimpleAttributeSet addressAttributes;
	private SimpleAttributeSet dataAttributes;
	private SimpleAttributeSet asciiAttributes;
	
	private boolean dataChanged;
	private String name;
	
	@Override
	public void run() {
		clearAllDocs();
		currentLineStart = 0;
		setUpScrollBar();
		fillPane();
	}// run
	
	public List<EditAtom> getAllEdits(){
		return editCaretaker.getAllEdits();
	}//getAllEdits
	
	public void clearAllEdits() {
		editCaretaker.clear();
		setDataChanged(false);
	}//clearAllEdits

	private boolean isVisible(int address) {
		boolean ans = false;
		int lastVisibleAddress = currentLineStart + (currentExtent * LINE_SIZE);
		if ((address >= currentLineStart) && (address < lastVisibleAddress)) {
			ans = true;
		} // at the end
		return ans;
	}// isVisible

	public void setDataChanged(boolean state) {
		dataChanged = false;
		hexFilter.setDataChanged(state);
	}// setDataChanged
	
	public boolean isDataChanged() {
		return dataChanged;
	}//isDataChanged
	
	public void setName(String name) {
		this.name =name;
	}//setName
	
	public String getName() {
		return name;
	}//getName

	public int getCurrentLineStart() {
		return this.currentLineStart;
	}// getCurrentLineStart

	public void updateValue(int dot, byte newValue, String panelSource) {
		if (!dataChanged) {
			dataChanged = true;
			fireChangeEvent();
		}//if
		int location = HEUtility.getSourceIndex(dot) + currentLineStart;
		byte oldValue = source.get(location);

		EditAtom editAtom = new EditAtom(location, oldValue, newValue, panelSource);
		editCaretaker.addEdit(editAtom);

		log.infof(String.format("%s  location: %#X, from %02X, to %02X%n", editAtom.getEditType(), location, oldValue,newValue));
		source.put(location, newValue);
	}// updateSource

	public boolean undo() {
		boolean ans;
		if (editCaretaker.canUndo()) {
			changeSource(editCaretaker.getUndo());
			ans = true;
		} else {
			ans = false;
		} // if
		return ans;
	}// undo

	public boolean redo() {
		boolean ans;
		if (editCaretaker.canRedo()) {
			changeSource(editCaretaker.getRedo());
			ans = true;
		} else {
			ans = false;
		} // if
		return ans;
	}// redo

	public void changeSource(EditAtom change) {
		if (!change.getEditType().equals(EditType.UNDO_REDO)) {
			return; // not valid
		} // if proper type
		int location = change.getLocation();
		source.put(location, change.getTo());
		convertLocationToCaretPosition(location);
	}// changeSource

	public int convertLocationToCaretPosition(int location, String source) {
		if (!isVisible(location)) {
			currentLineStart = location;
			scrollBar.setValue(location / LINE_SIZE);
		} // if visible

		int displacement = location - currentLineStart;
		int lineStart = displacement / LINE_SIZE * HEUtility.COLUMNS_PER_LINE;
		int bytePosition = displacement % LINE_SIZE;
		int midLineAdjusment = bytePosition >= HEUtility.MID_LINE_START ? 1 : 0;
		int position;
		if (source.equals(EditAtom.SOURCE_DATA)) {
			position = lineStart + (bytePosition * HEUtility.CHARS_PER_BYTE_DATA) + midLineAdjusment;
		} else {
			position = lineStart + HEUtility.ASCII_COL_START + (bytePosition * HEUtility.CHARS_PER_BYTE_ASCII)
					+ midLineAdjusment;
		} // if data or ascii
		fillPane();
		textHex.setCaretPosition(position);

		return position;
	}// convertLocationToCaretPosition

	public int convertLocationToCaretPosition(int location) {
		return convertLocationToCaretPosition(location, EditAtom.SOURCE_DATA);
	}// convertLocationToCaretPosition

	public void setDot(int position) {
		textHex.setCaretPosition(position);
	}// setDot
	
	public void refresh() {
		fillPane();
	}//refresh

	void fillPane() {
		if (currentExtent == 0) {
			return;
		} // if nothing to display

		setTextPanesCaretListeners(false);
		clearAllDocs();

		int sourceIndex = currentLineStart;// address to display
		int linesToDisplay = Math.min(currentExtent, currentMax - (currentLineStart / LINE_SIZE));

		int bytesToRead = LINE_SIZE;
		source.position(sourceIndex);
		for (int i = 0; i < linesToDisplay; i++) {
			byte[] activeLine = new byte[bytesToRead];
			source.get(activeLine, 0, bytesToRead);
			addHexLineToDocument(sourceIndex, activeLine);
			sourceIndex += bytesToRead;
			bytesToRead = Math.min(source.remaining(), LINE_SIZE);
			if (bytesToRead == 0) {
				bytesToRead = LINE_SIZE;
				break;
			} // if
		} // for

		hexNavigation.setLimits(sourceIndex - currentLineStart);
		setTextPanesCaretListeners(true);
		textHex.setCaretPosition(0);
	}// fillPane

	public void clear() {
		clearAllDocs();
		if (source != null) {
			source.clear();
		} // if
		setDataChanged(false);
		editCaretaker.clear();
	}// clear

	private void clearAllDocs() {
		try {
			addrDoc.remove(0, addrDoc.getLength());
			hexDoc.remove(0, hexDoc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		} // try

	}// clearAllDocs

	private void setTextPanesCaretListeners(boolean status) {
		/* status true = on/enables; false = off/disabled */
		if (status) {
			textAddr.addCaretListener(adapterHexEditDisplay);
			textHex.addCaretListener(adapterHexEditDisplay);
		} else {
			textAddr.removeCaretListener(adapterHexEditDisplay);
			textHex.removeCaretListener(adapterHexEditDisplay);
		} //if

	}// setTextPanesEnabled

	private void addHexLineToDocument(int currentAddress, byte[] data) {
		String strAddress = getAddress(currentAddress);
		String strData = getDataLine(data);
		String strAscii = getAsciiLine(data);

		try {
			addrDoc.insertString(addrDoc.getLength(), strAddress, addressAttributes);
			addrDoc.insertString(addrDoc.getLength(), System.lineSeparator(), null);

			hexDoc.insertString(hexDoc.getLength(), strData, dataAttributes);
			hexDoc.insertString(hexDoc.getLength(), strAscii, asciiAttributes);
			hexDoc.insertString(hexDoc.getLength(), System.lineSeparator(), null);
		} catch (BadLocationException e) {
			log.errorf("Bad Insert - line at : %s",strAddress);
			e.printStackTrace();
		} // try

	}// addLineToDocument

	private String getAddress(int currentAddress) {
		return String.format(FORMAT_ADDR, currentAddress);
	}// getAddress

	private String getDataLine(byte[] data) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (; i < data.length; i++) {
			if (i == HEUtility.MID_LINE_START) {
				sb.append(SPACE);
			} // if
			sb.append(String.format(FORMAT_DATA, data[i]));
		} // for

		int emptySpace = LAST_COLUMN_DATA - (sb.length() - 1);
		if (emptySpace != 0) {
			String spaceFormat = "%" + emptySpace + "s";
			sb.append(String.format(spaceFormat, SPACE));
		} // if - need to pad

		return sb.toString();

	}// getData

	private String getAsciiLine(byte[] data) {
		StringBuilder sb = new StringBuilder();

		byte b;
		int i = 0;
		for (; i < data.length; i++) {
			if (i == HEUtility.MID_LINE_START) {
				sb.append(SPACE);
			} // if
			b = data[i];
			if ((b >= 0X20) && (b <= 126)) {
				sb.append((char) b);
			} else {
				sb.append(UNPRINTABLE);
			} // if printable
		} // for

		int emptySpace = COLUMNS_FOR_ASCII - (sb.length() - 1);
		if (emptySpace != 0) {
			String spaceFormat = "%" + emptySpace + "s";
			sb.append(String.format(spaceFormat, SPACE));
		} // if - need to pad

		return sb.toString();
	}// getAscii

	public void setData(ByteBuffer data) {
		source = data.duplicate();
		source.rewind();
		dataChanged= false;
		setAddressLimits();
	}// setData

	public void setData(byte[] data) {
		dataSize = data.length;
		int bufferSize = dataSize;
		if ((dataSize % LINE_SIZE) != 0) {
			bufferSize = (LINE_SIZE - (dataSize % LINE_SIZE)) + dataSize;
		} // buffer Size
		source = ByteBuffer.allocate(bufferSize);
		source.put(data);
		source.rewind();
		dataChanged= false;
		setAddressLimits();
	}// setData
	
	public void setAddressLimits() {
		hdAddress.setMinValue(0);
		hdAddress.setMaxValue(source.capacity());
	}//setAddressLimits
	
	public byte[] getData() {
		byte[] ans = new byte[dataSize];
		source.rewind();
		source.get(ans);
		return ans;
	}//getData

	protected void setUpScrollBar() {
		if (source == null) {
			return;
		} // if
			// javax.swing.SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
		currentMax = maximumNumberOfRows(textHex);
		currentExtent = calcExtent(textHex);

		BoundedRangeModel model = scrollBar.getModel();
		model.setMinimum(0);
		model.setMaximum(currentMax);
		model.setValue(0);
		model.setExtent(currentExtent);

		scrollBar.setBlockIncrement(currentExtent - 2);
		// }// run
		// });
		scrollBar.updateUI();
	}// setUpScrollBar

	private int calcFontHeight(JTextPane thisPane) {
		Font font = thisPane.getFont();
		return thisPane.getFontMetrics(font).getHeight();
	}// calcFontHeight

	private int calcUsableHeight(JTextPane thisPane) {
		Insets insets = thisPane.getInsets();
		Dimension dimension = thisPane.getSize();
		return dimension.height - (insets.bottom + insets.top);
	}// calcUsableHeight

	private int calcExtent(JTextPane thisPane) {
		int useableHeight = calcUsableHeight(thisPane);
		int fontHeight = calcFontHeight(thisPane);
		return (int) (useableHeight / fontHeight);
	}// calcExtent

	private int maximumNumberOfRows(JTextPane thisTextPane) {
		return (source.capacity() / LINE_SIZE) + 1;
	}// maximumNumberOfRows

	private void makeStyles() {
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
		//////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------------------
	EventListenerList hedListenerList= new EventListenerList();
	
	public void addChangeListener(ChangeListener changeListener) {
		hedListenerList.add(ChangeListener.class, changeListener);
	}//addChangeListener
	
	public void removeChangeListener(ChangeListener changeListener) {
		hedListenerList.remove(ChangeListener.class, changeListener);
	}//removeChangeListener
	
	protected void fireChangeEvent() {
		Object[] listeners = hedListenerList.getListenerList();
		//process
		ChangeEvent changeEvent = new ChangeEvent(this);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i]==ChangeListener.class) {
				((ChangeListener) listeners[i+1]).stateChanged(changeEvent);
			}//if
		}//for
	}//fireChangeEvent
	
	//--------------------------------------------------

	public HexEditDisplayPanel() {
		setPreferredSize(new Dimension(790, 500));
		setMaximumSize(new Dimension(790, 32767));
		setMinimumSize(new Dimension(790, 500));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		initialize();
		appInit();
	}// Constructor

	private void appInit() {

		hexDoc = textHex.getStyledDocument();

		((AbstractDocument) hexDoc).setDocumentFilter(hexFilter);
		hexNavigation = new HexNavigation();
		textHex.getCaret().setVisible(false);

		textHex.setNavigationFilter(hexNavigation);

		makeStyles();

		addrDoc = textAddr.getStyledDocument();
		indexDoc = textIndex.getStyledDocument();
		try {
			indexDoc.remove(0, indexDoc.getLength());
			indexDoc.insertString(0, INDEX_DATA, addressAttributes);
		} catch (Exception e) {
			e.printStackTrace();
		} // try
		setDataChanged(false);
	}// appInit

	private void initialize() {
		setMinimumSize(new Dimension(500, 0));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 92, 750, 17 };
		gridBagLayout.rowHeights = new int[] { 25, 21 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
		setLayout(gridBagLayout);

		hdAddress = new HDNumberBox();
		hdAddress.addHDNumberValueChangedListener(adapterHexEditDisplay);
		hdAddress.setFont(new Font("Courier New", Font.BOLD, 16));
		hdAddress.setMaximumSize(new Dimension(95, 14));
		hdAddress.setPreferredSize(new Dimension(95, 14));
		hdAddress.setMinimumSize(new Dimension(95, 14));
		GridBagConstraints gbc_hdAddress = new GridBagConstraints();
		gbc_hdAddress.insets = new Insets(0, 0, 0, 0);
		gbc_hdAddress.fill = GridBagConstraints.BOTH;
		gbc_hdAddress.gridx = 0;
		gbc_hdAddress.gridy = 0;
		add(hdAddress, gbc_hdAddress);
		GridBagLayout gbl_hdAddress = new GridBagLayout();
		gbl_hdAddress.columnWidths = new int[] { 0 };
		gbl_hdAddress.rowHeights = new int[] { 0 };
		gbl_hdAddress.columnWeights = new double[] { Double.MIN_VALUE };
		gbl_hdAddress.rowWeights = new double[] { Double.MIN_VALUE };
		hdAddress.setLayout(gbl_hdAddress);

		textIndex = new JTextPane();
		textIndex.setEditable(false);
		textIndex.setText("00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F  01234567 ABCDEF00");
		textIndex.setMaximumSize(new Dimension(0, 0));
		textIndex.setMinimumSize(new Dimension(0, 0));
		textIndex.setPreferredSize(new Dimension(750, 0));
		textIndex.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textIndex.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_textIndex = new GridBagConstraints();
		gbc_textIndex.fill = GridBagConstraints.BOTH;
		gbc_textIndex.insets = new Insets(0, 0, 5, 5);
		gbc_textIndex.gridx = 1;
		gbc_textIndex.gridy = 0;
		add(textIndex, gbc_textIndex);
		textIndex.setFont(new Font("Courier New", Font.BOLD, 16));

		textAddr = new JTextPane();
		textAddr.setEditable(false);
		textAddr.setName("textAddr");
		textAddr.addMouseWheelListener(adapterHexEditDisplay);
		textAddr.setMaximumSize(new Dimension(90, 2147483647));
		textAddr.setPreferredSize(new Dimension(92, 0));
		textAddr.setMinimumSize(new Dimension(92, 0));
		GridBagConstraints gbc_textAddr = new GridBagConstraints();
		gbc_textAddr.insets = new Insets(0, 0, 0, 5);
		gbc_textAddr.fill = GridBagConstraints.BOTH;
		gbc_textAddr.gridx = 0;
		gbc_textAddr.gridy = 1;
		add(textAddr, gbc_textAddr);

		textHex = new JTextPane();
		textHex.setName(TEXT_HEX);
		textHex.addCaretListener(adapterHexEditDisplay);
		textHex.addComponentListener(adapterHexEditDisplay);
		textHex.addMouseWheelListener(adapterHexEditDisplay);
		textHex.setPreferredSize(new Dimension(410, 0));
		textHex.setMinimumSize(new Dimension(410, 0));
		textHex.setFont(new Font("Courier New", Font.BOLD, 16));
		GridBagConstraints gbc_textHex = new GridBagConstraints();
		gbc_textHex.fill = GridBagConstraints.BOTH;
		gbc_textHex.insets = new Insets(0, 0, 0, 5);
		gbc_textHex.gridx = 1;
		gbc_textHex.gridy = 1;
		add(textHex, gbc_textHex);
		textHex.setBorder(null);

		scrollBar = new JScrollBar();
		scrollBar.addAdjustmentListener(adapterHexEditDisplay);
		scrollBar.addMouseWheelListener(adapterHexEditDisplay);
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.fill = GridBagConstraints.VERTICAL;
		gbc_scrollBar.gridx = 2;
		gbc_scrollBar.gridy = 1;
		add(scrollBar, gbc_scrollBar);
	}// initialize

	// private static final String FORMAT_DATA = "%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X
	// %02X %02X ";
	private static final String FORMAT_DATA = "%02X ";
	private static final String FORMAT_ADDR = "%08X:";

	private static final int LINE_SIZE = HEUtility.BYTES_PER_LINE;
	private static final int LAST_COLUMN_DATA = HEUtility.LAST_COLUMN_DATA;
	private static final int COLUMNS_FOR_ASCII = HEUtility.COLUMNS_FOR_ASCII;
	private static final int COLUMNS_PER_LINE = HEUtility.COLUMNS_PER_LINE;
	public static final int BYTES_PER_LINE_ADDR = HEUtility.BYTES_PER_LINE_ADDR;

	private static final String UNPRINTABLE = HEUtility.UNPRINTABLE;
	private static final String SPACE = " ";

	private static final String TEXT_HEX = "textHex";
	// private static final String TEXT_ASCII = "textAscii";
	private static final String INDEX_DATA = "00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F";

	private JScrollBar scrollBar;
	private JTextPane textHex;
	private JTextPane textAddr;
	private JTextPane textIndex;
	private HDNumberBox hdAddress;

	///////////////////////////////////////////////////////////

	class AdapterHexEditDisplay implements AdjustmentListener, ComponentListener, MouseWheelListener, CaretListener,
			HDNumberValueChangeListener {// ,ChangeListener{

		/* ----------------- AdjustmentListener --------------- */
		@Override
		public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
			if (adjustmentEvent.getValueIsAdjusting()) {
				return;
			} // if
			if (adjustmentEvent.getAdjustmentType() != AdjustmentEvent.TRACK) {
				return;
			} // if
			currentLineStart = adjustmentEvent.getValue() * HEUtility.BYTES_PER_LINE;
			fillPane();
		}// adjustmentValueChanged

		/* ----------------- ComponentListener --------------- */

		@Override
		public void componentResized(ComponentEvent componentEvent) {
			setUpScrollBar();
		}// componentResized

		@Override
		public void componentHidden(ComponentEvent componentEvent) {
			// Not Coded
		}// componentHidden

		@Override
		public void componentMoved(ComponentEvent componentEvent) {
			// Not Coded
		}// componentMoved

		@Override
		public void componentShown(ComponentEvent componentEvent) {
			// Not Coded
		}// componentShown

		/* ----------------- MouseWheelListener --------------- */
		@Override
		public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
			scrollBar.setValue(scrollBar.getValue() + mouseWheelEvent.getWheelRotation());
		}// mouseWheelMoved

		// /* ----------------- CaretListener ---------------*/
		private Highlighter highlighterSource;
		private Highlighter highlighterAddress;
		private Highlighter highlighterIndex;
		private Highlighter.HighlightPainter highlightPainterYellow = new DefaultHighlighter.DefaultHighlightPainter(
				Color.YELLOW);
		private Highlighter.HighlightPainter highlightPainterPink = new DefaultHighlighter.DefaultHighlightPainter(
				Color.PINK);
		private Highlighter.HighlightPainter highlightPainterGray = new DefaultHighlighter.DefaultHighlightPainter(
				Color.LIGHT_GRAY);

		private Highlighter.HighlightPainter highlightPainterAscii;
		private Highlighter.HighlightPainter highlightPainterData;

		@Override
		public void caretUpdate(CaretEvent caretEvent) {
			int caretDot = caretEvent.getDot();

			int startAscii, endAscii, startData, endData;
			highlighterIndex = textIndex.getHighlighter();
			highlighterAddress = textAddr.getHighlighter();
			highlighterSource = ((JTextComponent) caretEvent.getSource()).getHighlighter();

			if ((caretDot % COLUMNS_PER_LINE) < LAST_COLUMN_DATA) { // data
				highlightPainterAscii = highlightPainterGray;
				highlightPainterData = highlightPainterYellow;
				startData = caretDot;
				startAscii = HEUtility.getAsciiDot(startData);
			} else {// ascii
				highlightPainterAscii = highlightPainterYellow;
				highlightPainterData = highlightPainterGray;

				startAscii = caretDot;
				startData = HEUtility.getDataDot(startAscii);
			} // if data v ascii
			endAscii = startAscii + 1;
			endData = HEUtility.getDataDotEnd(startData);

			int startAddressDot = HEUtility.getAddressDot(startAscii);
			int endAddressDot = startAddressDot + BYTES_PER_LINE_ADDR;

			int startIndexDot = HEUtility.getIndexDot(startAscii);
			int endIndexDot = startIndexDot + 2;

			try {
				clearHighlights(highlighterSource);
				highlighterSource.addHighlight(startAscii, endAscii, highlightPainterAscii);
				highlighterSource.addHighlight(startData, endData, highlightPainterData);

				clearHighlights(highlighterAddress);
				clearHighlights(highlighterIndex);
				highlighterAddress.addHighlight(startAddressDot, endAddressDot, highlightPainterPink);
				highlighterIndex.addHighlight(startIndexDot, endIndexDot, highlightPainterPink);

			} catch (BadLocationException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} // try

			// lblAddress.setText(String.format("%08X ", HEUtility.getSourceIndex(startData) + currentLineStart));
			hdAddress.setValueQuiet(HEUtility.getSourceIndex(startData) + currentLineStart);
		}// caretUpdate

		private void clearHighlights(Highlighter highlighter) {
			Highlighter.Highlight[] highlights = highlighter.getHighlights();
			for (Highlighter.Highlight hightlight : highlights) {
				highlighter.removeHighlight(hightlight);
			} // for each highlighted
		}// clearHighlights

		// /* ----------------- HDNumberValueChangeListener ---------------*/

		@Override
		public void valueChanged(HDNumberValueChangeEvent hDNumberValueChangeEvent) {
			if (hDNumberValueChangeEvent.getSource().equals(hdAddress)) {
				convertLocationToCaretPosition(hDNumberValueChangeEvent.getNewValue());
			} // if
		}// valueChanged

	}// class adapterHexEditDisplay

}// class HexEditDisplayPanel
