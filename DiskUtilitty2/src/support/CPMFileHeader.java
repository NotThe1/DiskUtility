package support;

import java.util.ArrayList;

public class CPMFileHeader {

	protected int bytesPerSector;
	protected int recordCount;
	protected int actualByteCount;
	protected int numberOfSectors;
	private int byteCountSectors;
	private boolean readOnly;
	private boolean systemFile;
	protected ArrayList<Integer> sectors;
	

//	public String fileName;

	public CPMFileHeader(CPMDirectory directory, String fileName) {
		this.recordCount = directory.getTotalRecordCount(fileName);
		this.bytesPerSector = directory.getBytesPerSector();
		this.numberOfSectors = ((recordCount - 1) / directory.getLogicalRecordsPerSector()) + 1;
		this.readOnly = directory.isReadOnly(fileName);
		this.systemFile = directory.isSystemFile(fileName);
		this.byteCountSectors = this.numberOfSectors * directory.getBytesPerSector();
		this.actualByteCount = this.recordCount * Disk.LOGICAL_SECTOR_SIZE;

		this.sectors = new ArrayList<Integer>();
		ArrayList<Integer> blocks = directory.getAllAllocatedBlocks(fileName);
		int blockSectorStart = 0;
		int sectorsPerBlock = directory.getSectorsPerBlock();
		int block0StartSector = directory.getDirectoryStartSector();
		for (int i = 0; i < blocks.size(); i++) {
			blockSectorStart = block0StartSector + (blocks.get(i) * sectorsPerBlock);
			for (int j = 0; j < sectorsPerBlock; sectors.add(blockSectorStart + j++))
				;
		} // for i
	}// Constructor

	public int getBytesPerSector() {
		return this.bytesPerSector;
	}// getBytesPerSector

	public int getRecordCount() {
		return this.recordCount;
	}// getRecordCount

	public int getActualByteCount() {
		return this.actualByteCount;
	}// getActualByteCount

	public int getNumberOfSectors() {
		return this.numberOfSectors;
	}// getNumberOfSectors

	public int getByteCountSectors() {
		return this.byteCountSectors;
	}// getByteCountSectors

	public boolean isReadOnly() {
		return this.readOnly;
	}// isRedOnly

	public boolean isSystemFile() {
		return this.systemFile;
	}// isSystemFile

	public ArrayList<Integer> getSectors() {
		return this.sectors;
	}// getSectors

}// class CPMFileHeader
