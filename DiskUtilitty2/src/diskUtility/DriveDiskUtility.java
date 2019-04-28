package diskUtility;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import support.AppLogger;

public class DriveDiskUtility {

	public DriveDiskUtility() {
		// TODO Auto-generated constructor stub
	}//

	public static void main(String[] args) {
		StyledDocument doc = new DefaultStyledDocument();
		AppLogger log = AppLogger.getInstance();
		log.setDoc(doc);
		DiskUtility du = new DiskUtility();
		du.setVisible(true);

	}// main

}// class DriveDiskUtility
