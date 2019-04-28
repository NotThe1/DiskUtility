package support;

import javax.swing.filechooser.FileNameExtensionFilter;

public class FilterFactory {

	private FilterFactory() {
	}// Constructor

	public static FileNameExtensionFilter getListingFiles() {
		return new FileNameExtensionFilter("Listing Files", "list", "lst");
	}// getListingFiles

	public static FileNameExtensionFilter getDisk() {
		return new FileNameExtensionFilter("Disketts", "F3HD");
	}// getDisk

	public static FileNameExtensionFilter getAnyDisk() {
		return new FileNameExtensionFilter("Disketts", "F3DD", "F3HD", "F3ED", "F5DD", "F5HD", "F8SS", "F8DS");
	}// getDisk
	
	public static FileNameExtensionFilter getMemory() {
		return new FileNameExtensionFilter("Memory Image", "HEX","MEM");
	}// getDisk getListingCollection

	public static FileNameExtensionFilter getListingCollection() {
		return new FileNameExtensionFilter("Listing Collection", FilePicker.COLLECTIONS_LISTING);
	}// getDisk getListingCollection

	public static FileNameExtensionFilter getMemoryCollection() {
		return new FileNameExtensionFilter("Memory Collection", FilePicker.COLLECTIONS_MEMORY);
	}// getDisk getListingCollection

	public static FileNameExtensionFilter getAllCollections() {
		return new FileNameExtensionFilter("All Collections", FilePicker.COLLECTIONS_MEMORY,FilePicker.COLLECTIONS_LISTING);
	}// getDisk getListingCollection

	public static FileNameExtensionFilter getPrinterOutput() {
		return new FileNameExtensionFilter("Z80 Printer output", "txt","log","print");
	}// getDisk getListingCollection



}// class FilterFactory
