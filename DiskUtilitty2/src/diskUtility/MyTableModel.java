package diskUtility;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author Frank Martyn
 * 
 * When extending this class, Implement the "getColumnClass" method. Example included below
 *
 */
public class MyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private Map<Point, Object> data = new HashMap<Point, Object>();
	private int rows = 0;

	public MyTableModel(String[] headers) {
		this.headers = headers.clone();
		this.columnCount = this.headers.length;
	}//Constructor
	
	@Override
	public int getColumnCount() {
		return columnCount;
	}// getColumnCount

	public String getColumnName(int column) {
		return column < columnCount ? headers[column] : EMPTY_STRING;
	}// getColumnName

	@Override
	public int getRowCount() {
		return rows;
	}// getRowCount

	public void clear() {
		data.clear();
		rows = 0;
	}// clear

	@Override
	public Object getValueAt(int row, int column) {
		if ((row < 0) || (column < 0) || (row > rows) || (column > columnCount - 1)) {
			String msg = String.format("Invalid row/column setting. row = %d, Column = %d ", row, column);
			throw new IllegalArgumentException(msg);
		} // if - out of bounds
		return data.get(new Point(row, column));
	}// getValueAt

	public void setValueAt(Object value, int row, int column) {
		if ((row < 0) || (column < 0) || (row > rows) || (column > columnCount - 1)) {
			String msg = String.format("Invalid row/column setting. row = %d, Column = %d ", row, column);
			throw new IllegalArgumentException(msg);
		} // if - out of bounds
		data.put(new Point(row, column), value);
	}// setValueAt
	
	public void addRow(Object[] values) {
		rows++;
		for (int i = 0; i < values.length; i++) {
			data.put(new Point(rows - 1, i), values[i]);
		} // for
	}//addRow

	public Object[] getRow(int rowNumber) {
		if ((rowNumber < 0) || (rowNumber > rows)) {
			String msg = String.format("[getRow] Invalid row. row = %d, max row = %d", rowNumber,rows);
			throw new IllegalArgumentException(msg);
		} // if - out of bounds

		Object[] row = new Object[this.getColumnCount()];
		for (int i = 0; i < columnCount; i++) {
			row[i] = data.get(new Point(rowNumber, i));
		} // for
		return row;
	}// getRow
	
	public synchronized void removeRow(int row) {
		if ( row > rows|| row < 0) {
			return;
		}// if row we have
		
		if (row ==rows-1) {
			rows--;
			return;
		}// if last row
		
		/* move data down a row */
		for (int r = row ;r < rows-1; r++) {
			for (int i = 0; i < columnCount ; i++) {
				data.put(new Point(r, i), data.get(new Point(r+1, i)));
			} // for
		}// for r
		
		/* remove the last row */
		for (int i = 0; i < columnCount ; i++) {
			 data.remove(rows,columnCount);
		} // for		
		rows--;
		
	}//removeRow

//	@Override
//	public Class<?> getColumnClass(int columnIndex) {
//		if (columnIndex > this.getColumnCount() - 1) {
//			String msg = String.format("[getColumnClass] Invalid column. columnIndex = %d, max Column = %d",
//					columnIndex, this.getColumnCount());
//			throw new IllegalArgumentException(msg);
//		} // if
//		Class<?> ans = String.class;
//		switch (columnIndex) {
//		case 0:
//			ans = super.getColumnClass(columnIndex);
//			break;
//		case 1:
//			ans = Boolean.class;
//			break;
//		case 2:
//			ans = Byte.class;
//			break;
//		case 3:
//			ans = Integer.class;
//			break;
//		case 4:
//			ans = Number.class;
//			break;
//		case 5:
//			ans = String.class;
//			break;
//		}// switch
//		return ans;
//	}// getColumnClass

//	public static final String COLUMN_0 = "Column_0";
//	public static final String COLUMN_1 = "Column_1";
//	public static final String COLUMN_2 = "Column_2";
//	public static final String COLUMN_3 = "Column_3";
//	public static final String COLUMN_4 = "Column_4";
//	public static final String COLUMN_5 = "Column_5";
//
//	private static final String[] headers = new String[] { COLUMN_0, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5 };
	private   String[] headers;// = new String[0];
	private  int columnCount;// = headers.length;

	public static final String EMPTY_STRING = "";
	

}//class MyTableModel
