package diskUtility;

import support.CPMDirectoryEntry;

//import disks.CPMDirectoryEntry;

public class DirectoryTableModel extends MyTableModel {
	private static final long serialVersionUID = 1L;

	public DirectoryTableModel() {
		super(new String[] { INDEX, NAME, TYPE, USER, RO, SYS, SEQ, COUNT, BLOCKS });
	}// Constructor
	
	public void addRow(Integer entryIndex,CPMDirectoryEntry entry) {
		/* @formatter:off */ 
		Object[] rowData = new Object[] { 	entryIndex,
											entry.getFileNameTrim(),
											entry.getFileTypeTrim(),
											entry.getUserNumberInt(),
											entry.isReadOnly(),
											entry.isSystemFile(),
											entry.getActualExtentNumber(),
											entry.getRcInt(),
											entry.getBlockCount()};
		/* @formatter:on  */
		addRow(rowData);
	}//addRow

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex > this.getColumnCount() - 1) {
			String msg = String.format("[getColumnClass] Invalid column. columnIndex = %d, max Column = %d",
					columnIndex, this.getColumnCount());
			throw new IllegalArgumentException(msg);
		} // if
		
		Class<?> ans;
		switch (columnIndex) {
		case 0: // Index
			ans = Integer.class;
			break;
		case 1: // Name
			ans = String.class;
			break;
		case 2: // Type
			ans = String.class;
			break;
		case 3: // User
			ans = Integer.class;
			break;
		case 4: // R/O
			ans = Boolean.class;
			break;
		case 5:// Sys
			ans = Boolean.class;
			break;
		case 6:// Seq
			ans = Integer.class;
			break;
		case 7:// Count
			ans = Integer.class;
			break;
		case 8:// Blocks
			ans = Integer.class;
			break;
		default:
			ans = String.class;
			break;
		}// switch
		return ans;
	}// getColumnClass

	public static final String INDEX = "Index";
	public static final String NAME = "Name";
	public static final String TYPE = "Type";
	public static final String USER = "User";
	public static final String RO = "R/O";
	public static final String SYS = "Sys";
	public static final String SEQ = "Seq";
	public static final String COUNT = "Count";
	public static final String BLOCKS = "Blocks";

}// class DirectoryTableModel
