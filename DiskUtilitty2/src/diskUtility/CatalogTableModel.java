package diskUtility;

public class CatalogTableModel extends MyTableModel {

	private static final long serialVersionUID = 1L;

	public CatalogTableModel() {
		super(new String[] { FILE, DISK, LOCATION });
		// TODO Auto-generated constructor stub
	}// Constructor

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex > this.getColumnCount() - 1) {
			String msg = String.format("[getColumnClass] Invalid column. columnIndex = %d, max Column = %d",
					columnIndex, this.getColumnCount());
			throw new IllegalArgumentException(msg);
		} // if

		Class<?> ans;// = String.class;
		switch (columnIndex) {
		case 0: // cpmFile
			ans = String.class;
			break;
		case 1: // Disk
			ans = String.class;
			break;
		case 2: // Location
			ans = String.class;
			break;
		default:
			ans = String.class;
		}// switch
		return ans;
	}// getColumnClass

	public static final String FILE = "cpmFile";
	public static final String DISK = "disk";
	public static final String LOCATION = "Location";

}// class CatalogTableModel
