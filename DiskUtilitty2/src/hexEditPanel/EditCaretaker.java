package hexEditPanel;

import java.util.ArrayList;
import java.util.List;

public class EditCaretaker {
	private  int currentIndex = 0;
	private List<EditAtom> edits = new ArrayList<EditAtom>();

	public boolean canUndo() {
		try {
			return edits.get(currentIndex ).canUndo();
		} catch (IndexOutOfBoundsException e) {
			return false;
		} // try
	}//canUndo

	public EditAtom getUndo() {
		EditAtom priorEdit = edits.get(currentIndex--);
		return new EditAtom(EditType.UNDO_REDO, priorEdit.getLocation(), (byte) 0X00, priorEdit.getFrom(),priorEdit.getSource());
	}// getUndo
	
	
	public boolean canRedo() {
		try {
			return edits.get(currentIndex + 1).canUndo();
		} catch (IndexOutOfBoundsException e) {
			return false;
		} // try
	}//canUndo

	public EditAtom getRedo() {
		EditAtom priorEdit = edits.get(++currentIndex);
		return new EditAtom(EditType.UNDO_REDO, priorEdit.getLocation(), (byte) 0X00, priorEdit.getTo(),priorEdit.getSource());
	}// getUndo

	public void addEdit(EditAtom edit) {
		if (edit.combineEdits(getLastEdit())) {
			edits.set(getLastIndex(), edit);
		} else {
			edits.add(edit);
			currentIndex = getLastIndex();
		} // if combine
	}// add

	public void clear() {
		edits.clear();
		currentIndex = -1;
	}// clear

	public int getCurrentIndex() {
		return currentIndex;
	}// getNextIndex

	public EditAtom getCurrentEdit() {
		try {
			return edits.get(currentIndex--);
		} catch (Exception e) {
			return EditAtom.invalid();
		} // try

	}// getPreviousEdit

	public EditAtom getNextEdit() {
		// return getEdit(++currentIndex);
		try {
			return edits.get(++currentIndex);
		} catch (Exception e) {
			return EditAtom.invalid();
		} // try

	}// getNextEdit

	public EditAtom getEdit(int index) {
		try {
			currentIndex = index;
			return edits.get(currentIndex--);
		} catch (Exception e) {
			return EditAtom.invalid();
		} // try
	}// getAtom
	
	public List<EditAtom> getAllEdits() {
		return edits;
	}//getAllEdits

	public int getLastIndex() {
		return edits.size() - 1;
	}// getLastIndex

	public EditAtom getLastEdit() {
		try {
			return edits.get(getLastIndex());
		} catch (Exception e) {
			return EditAtom.invalid();
		} // try

	}// getLastEdit

}// class EditCaretaker
