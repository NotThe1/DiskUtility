package diskUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

import support.AppLogger;
import support.DiskMetrics;
import support.FilePicker;
import support.MemoryLoaderFromFile;

public class UpdateSystemDisk {
	
	static AppLogger log = AppLogger.getInstance();

	public static void updateDisk(File selectedFile) {
		String fileExtension = "F3HD";
		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(fileExtension);
		if (diskMetric == null) {
			System.err.printf("Bad disk type: %s%n", fileExtension);
			return;
		} // if diskMetric

		try (FileChannel fileChannel = new RandomAccessFile(selectedFile, "rw").getChannel();) {
			MappedByteBuffer disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, diskMetric.getTotalBytes());

			/** set up as system disk **/
			Class<DiskUtility> thisClass = DiskUtility.class;
//			String resourcesPathAlt = "/workingOS/";
			String resourcesPath = "/";
//			String resourcesPath = "/Z80Code/";
			String BootSector = resourcesPath + "BootSector.mem";
			String CCP = resourcesPath + "CCP.mem";
			String BDOS = resourcesPath + "BDOS.mem";
			String BIOS = resourcesPath + "BIOS.mem";
			/* Boot Sector */
			// URL rom = thisClass.getResource("/disks/resources/BootSector.mem");
			
			InputStream in = thisClass.getClass().getResourceAsStream(BootSector);
			log.infof("BootSector = %s%n", BootSector);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			byte[] dataBoot = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0200);
			disk.position(0);
			disk.put(dataBoot);

			in = thisClass.getClass().getResourceAsStream(CCP);
			reader = new BufferedReader(new InputStreamReader(in));
			byte[] dataCCP = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0800);
			disk.put(dataCCP);

			in = thisClass.getClass().getResourceAsStream(BDOS);
			reader = new BufferedReader(new InputStreamReader(in));
			byte[] dataBDOS = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0E00);
			disk.put(dataBDOS);

			in = thisClass.getClass().getResourceAsStream(BIOS);
			reader = new BufferedReader(new InputStreamReader(in));
			byte[] dataBIOS = MemoryLoaderFromFile.loadMemoryImage(reader, 0x0A00);
			disk.put(dataBIOS);

			fileChannel.force(true);
			fileChannel.close();
			disk = null;
		} catch (IOException e) {
			e.printStackTrace();
		} // try
	}

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
		}//for 

	}// updateDisks

}// class UpdateSystemDisk
