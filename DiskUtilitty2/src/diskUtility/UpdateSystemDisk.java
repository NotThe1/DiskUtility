package diskUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import javax.swing.JFileChooser;

import appLogger.AppLogger;
import support.DiskMetrics;
import support.FilePicker;

public class UpdateSystemDisk {

	static AppLogger log = AppLogger.getInstance();
	
	public static void updateDisk(String diskPath) {

		File selectedFile = new File(diskPath);
		if (!selectedFile.exists()) {
			System.err.printf("this file does not exist: %s%n", diskPath);
			return;
		} // if

		updateDisk(selectedFile);
		log.info("Updated System on " + selectedFile.toString());
	}// updateDisk

	public static void updateDisks() {
		JFileChooser fc = FilePicker.getDisks();
		if (fc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			return;
		} // if

		File[] files = fc.getSelectedFiles();
		for (File file : files) {
			updateDisk(file);
			log.info("Updated System on " + file.toString());
		} // for
	}// updateDisks


	public static void updateDisk(File selectedFile) {
		String fileExtension = "F3HD";
		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			log.errorf("Bad disk type: %s%n", fileExtension);
			return;
		} // if diskMetric

		String resourcesPath = "/";
		 resourcesPath = "";

			int[] sizes = new int[] { 0x200, 0x0800, 0x0E00, 0x0A00 };
			String[] fileNames = new String[] { resourcesPath + "BootSector.mem", resourcesPath + "CCP.mem",
					resourcesPath + "BDOS.mem", resourcesPath + "BIOS.mem" };
//			Class<UpdateSystemDisk> thisClass = UpdateSystemDisk.class;
			Class<DiskUtility> thisClass = DiskUtility.class;
			int fileIndex = 0;
		try (FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();) {
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());
			/** set up as system disk **/

			InputStream in;
			disk.position(0);
			int byteIndex;
			String strAddress;
			Scanner scanner;
			for (fileIndex = 0; fileIndex < fileNames.length; fileIndex++) {
//				 in = this.getClass().getResourceAsStream(fileNames[fileIndex]);
//*				in = thisClass.getClass().getResourceAsStream(fileNames[fileIndex]);
				in = thisClass.getClassLoader().getResourceAsStream(fileNames[fileIndex]);
				scanner = new Scanner(in);
				byte[] dataRead = new byte[sizes[fileIndex]];
				byteIndex = 0;
				
				while(scanner.hasNextLine()) {
					strAddress = scanner.next();
					strAddress = strAddress.replace(":", "");
					
					for ( int lineIndex = 0; lineIndex <SIXTEEN;lineIndex++) {
						dataRead[byteIndex++] = (byte)((int) Integer.valueOf(scanner.next(),16));
					}//for
					
					scanner.nextLine();							
				}//while
				
				scanner.close();				
				disk.put(dataRead);
			} // for
			fileChannel.force(true);
			fileChannel.close();
			disk = null;
		} catch (  NullPointerException | IOException npe) {//IOException
			log.errorf("[UpdateSystemDisk.updateDisk]  Failed to load file: %s%n", fileNames[fileIndex]);
			npe.printStackTrace();
		} // try
	}//updateDisk
	

	private final static int SIXTEEN = 16;

}// class UpdateSystemDisk
