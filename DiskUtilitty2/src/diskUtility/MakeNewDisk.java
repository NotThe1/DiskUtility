
package diskUtility;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import support.DiskMetrics;
import support.FilePicker;

public class MakeNewDisk {

	public static File makeNewDisk(Container container) {
		
		String fileExtension = "F3HD";
		JFileChooser fc = FilePicker.getDisk();
		if (fc.showOpenDialog(container) == JFileChooser.CANCEL_OPTION) {
			System.out.println("Bailed out of the open");
			return null;
		} // if

		File pickedFile = fc.getSelectedFile();

		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			return null;
		} // if diskMetric

		String targetRawAbsoluteFileName = pickedFile.getAbsolutePath();
		String[] fileNameComponents = targetRawAbsoluteFileName.split("\\.");
		String targetAbsoluteFileName = fileNameComponents[0] + "." + fileExtension;

		File selectedFile = new File(targetAbsoluteFileName);
		if (selectedFile.exists()) {
			if (JOptionPane.showConfirmDialog(null, "File already exists do you want to overwrite it?",
					"YES - Continue, NO - Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return null;
			} else {
				if(!selectedFile.delete()){
					JOptionPane.showMessageDialog(null, "Unable to delete old file: " + selectedFile.getAbsolutePath(),"Make new disk",JOptionPane.ERROR_MESSAGE);
					return null;
				}// if not deleted
				selectedFile = null;
				selectedFile = new File(targetAbsoluteFileName);
			} // inner if
		} // if - file exists

		try (FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();) {
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());
			ByteBuffer sector = ByteBuffer.allocate(diskMetric.bytesPerSector);
			int sectorCount = 0;
			while (disk.hasRemaining()) {
				sector = setUpBuffer(sector, sectorCount++);
				disk.put(sector);
			} // while

			fileChannel.force(true);
			fileChannel.close();
			disk = null;
			UpdateSystemDisk.updateDisk(selectedFile);
		} catch (IOException e) {
			e.printStackTrace();
		} // try

		return selectedFile;
	}// makeNewDisk

	private static ByteBuffer setUpBuffer(ByteBuffer sector, int value) {
		sector.clear();
		// set value to be put into sector
		Byte byteValue = (byte) 0x00; // default to null
		Byte MTfileValue = (byte) 0xE5; // deleted file value
		Byte workingValue;
		while (sector.hasRemaining()) {
			workingValue = ((sector.position() % 0x20) == 0) ? MTfileValue : byteValue;
			sector.put(workingValue);
		} // while
		sector.flip();
		return sector;
	}// setUpBuffer

}// class MakeNewDisk
