package hexEditPanel;

/*    Memento    */
public class EditAtom {
	private EditType editType;
	private int location;
	private byte from;
	private byte to;
	private String panelSource;

	public EditAtom(int location, byte from, byte to) {
		this(EditType.REPLACE, location, from, to,SOURCE_DATA);
	}// Constructor

	public EditAtom(int location, byte from, byte to,String panelSource) {
		this(EditType.REPLACE, location, from, to,panelSource);
	}// Constructor

	public EditAtom(EditType editType, int location, byte from, byte to,String panelSource) {
		this.editType = editType;
		this.location = location;
		this.from = from;
		this.to = to;
		this.panelSource= panelSource;
	}// Constructor
	/*--*/

	public boolean combineEdits(EditAtom priorEdit) {
		boolean combine = (this.getLocation() == priorEdit.getLocation())
				&& (this.getEditType().equals(priorEdit.getEditType()));
		if (combine) {
			this.from = priorEdit.from;
		} // if - combine this with last edit
		return combine;
	}// combineEdits

	public static EditAtom invalid() {
		return new EditAtom(EditType.INVALID, -1, (byte) 0x00, (byte) 0X00,SOURCE_UNKNOWN);
	}// invalid

	public EditType getEditType() {
		return editType;
	}// getEditType

	public int getLocation() {
		return location;
	}// getLocation

	public byte getFrom() {
		return from;
	}// getFrom

	public byte getTo() {
		return to;
	}// getTo
	
	public String getSource() {
		return panelSource;
	}// getSource

	public String toString() {
		return String.format("Type: %s, Location: %04X, From %02X, to %02X.", editType, location, from, to);
	}// toString

	public boolean canRedo() {
		boolean ans;
		switch (editType) {
		case REPLACE:
			ans = true;
			break;
		case ADD:
		case REMOVE:
		case INVALID:
		case UNDO_REDO:
		default:
			ans = false;
		}// switch
		return ans;
	}//canRedo

	public boolean canUndo() {
		boolean ans;
		switch (editType) {
		case REPLACE:
			ans = true;
			break;
		case ADD:
		case REMOVE:
		case INVALID:
		case UNDO_REDO:
		default:
			ans = false;
		}// switch
		return ans;
	}//canUndo
	
	public static final String SOURCE_DATA = "data";
	public static final String SOURCE_ASCII = "ascii";
	public static final String SOURCE_UNKNOWN = "unknown";
	

}// class EditAtom
