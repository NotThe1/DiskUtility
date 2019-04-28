package support;

import java.io.File;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FilePicker {
	/**
	 * 
	 * @author Frank Martyn
	 *
	 *         This Class follows the design pattern for a Factory class. It generates a specialized JFileChooser, that
	 *         handles the identification of various files used by the VM application. All the file are found in the
	 *         user's directory, or path passed on the constructor, in a directory called VMdata. Virtual disks are in a
	 *         sub directory called "Disks". Each type of file is identified by its suffix.
	 * 
	 *         examples: JFileChooser fc = FilePicker.getDataPicker("Memory Image Files", "mem", "hex");
	 *
	 *         2019-04-26 - Fixed GetDisks did not select any disks
	 *         2018-09-12 - major cleanup.
	 */

	/* Listing files */
	public static JFileChooser getListing(Path newPath) {
		pathCode = newPath.toString();
		return getListing();
	}// getListings

	public static JFileChooser getListing() {
		return getChooser(pathCode, FilterFactory.getListingFiles(), false);
	}// getListings

	public static JFileChooser getListings(Path newPath) {
		pathCode = newPath.toString();
		return getListings();
	}// getListings

	public static JFileChooser getListings() {
		return getChooser(pathCode, FilterFactory.getListingFiles(), true);
	}// getListings

	/* Disks */
	public static JFileChooser getDisk(Path newPath) {
		pathDisk = newPath.toString();
		return getDisk();
	}// getDisk

	public static JFileChooser getDisk() {
		return getChooser(pathDisk, FilterFactory.getDisk(), false);
	}// getDisk

	public static JFileChooser getDisks(Path newPath) {
		pathDisk = newPath.toString();
		return getDisks();
	}// getDisks

	public static JFileChooser getDisks() {
		return getChooser(pathDisk, FilterFactory.getDisk(), true);
	}// getDisks

	/* Memory */
	public static JFileChooser getMemory(Path newPath) {
		pathMemory = newPath.toString();
		return getMemory();
	}// getMemory

	public static JFileChooser getMemory() {
		return getChooser(pathMemory, FilterFactory.getMemory(), false);
	}// getMemory

	public static JFileChooser getMemories() {
		return getChooser(pathMemory, FilterFactory.getMemory(), true);
	}// getMemories

	/* Collections  Listings*/
	public static JFileChooser getListingCollection(Path newPath) {
		collectionsPath = newPath.toString();
		return getListingCollection();
	}// getMemory

	public static JFileChooser getListingCollection() {
		return getChooser(collectionsPath, FilterFactory.getListingCollection(), false);
	}// getMemory


	/* Collections Memory*/
	public static JFileChooser getMemoryCollection(Path newPath) {
		collectionsPath = newPath.toString();
		return getMemoryCollection();
	}// getMemory

	public static JFileChooser getMemoryCollection() {
		return getChooser(collectionsPath, FilterFactory.getMemoryCollection(), false);
	}// getMemory


	/* Printer Output*/
	public static JFileChooser getPrinterOutput(Path newPath) {
		printerOutputPath = newPath.toString();
		return getPrinterOutput();
	}// getPrinterOutput

	public static JFileChooser getPrinterOutput() {
		return getChooser(printerOutputPath, FilterFactory.getPrinterOutput(), false);
	}// getPrinterOutput


	////////////////////////////////////////////////////////////////////////////////
	public FilePicker() {
	}// Constructor

//	private static JFileChooser getChooser(String target, String filterDescription, String... filterExtensions) {
//		FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDescription, filterExtensions);
//		return getChooser(target, filter, false);
//	}// customiseChooser
//
//	private static JFileChooser getChoosers(String target, String filterDescription, String... filterExtensions) {
//		FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDescription, filterExtensions);
//		return getChooser(target, filter, true);
//	}// customiseChooser

	private static JFileChooser getChooser(String target, FileNameExtensionFilter filter, boolean multiSelect) {
		File targetDirectory = new File(target);
		if(!targetDirectory.exists()) {
			if(!targetDirectory.mkdirs()) {
				JOptionPane.showMessageDialog(null, "unable to make folder: " + target,
						"File Piicker",JOptionPane.ERROR_MESSAGE);
			}//if bad make dir
		}//if dir not there
		
//		if (!new File(target).exists()) {
//			new File(target).mkdirs();
//		} // make sure the target directory exists
		
		JFileChooser fileChooser = new JFileChooser(target);
		fileChooser.setMultiSelectionEnabled(multiSelect);
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}// customiseChooser

	private static final String DIR_PARENT = "Z80Work";
	private static final String DIR_DISK = "Disks";
	private static final String DIR_MEMORY = "Memory";
	private static final String DIR_CODE = "Code";
	private static final String DIR_COLLECTIONS = "Collections";
	private static final String DIR_PRINT_OUTPUT = "PrintOutput";

	public static final String COLLECTIONS_LISTING = "colListing";
	public static final String COLLECTIONS_MEMORY = "colMemory";
	//
	private static String userDirectory = System.getProperty("user.home", ".");
	private static String fileSeparator = System.getProperty("file.separator", "\\");
	private static String parentDirectory = userDirectory + fileSeparator + DIR_PARENT + fileSeparator;
	//
	private static String pathDisk = parentDirectory + DIR_DISK;
	private static String pathMemory = parentDirectory + DIR_MEMORY;
	private static String pathCode = parentDirectory + DIR_CODE;
	private static String collectionsPath = parentDirectory + DIR_COLLECTIONS;
	private static String printerOutputPath = parentDirectory + DIR_PRINT_OUTPUT;

}// class FilePicker1
