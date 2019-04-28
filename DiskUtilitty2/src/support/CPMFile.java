package support;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;

public class CPMFile extends CPMFileHeader {
	private DiskDrive diskDrive;
	private CPMDirectory directory;
	private String fileName;

	private CPMFile(DiskDrive diskDrive, CPMDirectory directory, String fileName) {
		super(directory, fileName);
		this.directory= directory;
		this.diskDrive = diskDrive;
		this.fileName= fileName;
	}// Constructor
	
	public static CPMFile getCPMFile(DiskDrive diskDrive, CPMDirectory directory, String fileName){
		return new CPMFile( diskDrive,  directory,  fileName);
	}//getCPMFile
	
	public static CPMFile createCPMFile(DiskDrive diskDrive, CPMDirectory directory, String fileName){
		return new CPMFile( diskDrive,  directory,  fileName);
	}//getCPMFile
	
	public String getFileName() {
		return this.fileName;
	}//getFileName
	

	public byte[] readRaw() {
		return read(actualByteCount);
	}// read

	public byte[] readNet() {
		return read(recordCount * Disk.LOGICAL_SECTOR_SIZE);
	}// readNet

	private byte[] read(int byteCount) {
		byte[] ans = new byte[byteCount];
		if (recordCount == 0) {
			return ans;
		} // if
		ByteBuffer readData = ByteBuffer.allocate(numberOfSectors * diskDrive.getBytesPerSector());
		for (int i = 0; i < numberOfSectors; i++) {
			diskDrive.setCurrentAbsoluteSector(sectors.get(i));
			readData.put(diskDrive.read());
		} // - for- i : each sector
		readData.rewind();
		readData.get(ans);
		return ans;
	}// read

	public void write(byte[] writeData) {
		ByteBuffer writeBuffer = ByteBuffer.allocate(numberOfSectors * diskDrive.getBytesPerSector());
		writeBuffer.put(writeData);
		writeBuffer.rewind();
		byte[] dataToWrite = new byte[bytesPerSector];
		for (int i = 0; i < numberOfSectors; i++) {
			diskDrive.setCurrentAbsoluteSector(sectors.get(i));
			writeBuffer.get(dataToWrite);
			diskDrive.write(dataToWrite);
		} // for
	}// write
/*
 * Writes a new file  returns true if successful
 */
	public boolean writeNewFile(byte[] writeData){
		if (recordCount!=0){
			return false;
		}//if new file
		
		ByteBuffer writeBuffer = ByteBuffer.wrap(writeData);
		byte[] sectorData = new byte[bytesPerSector];
		
		int directoryIndex = directory.updateEntry(fileName);
		Queue<Integer> sectorsToUse = directory.storageFromBlock(directory.getMoreStorage(directoryIndex));
		
		int logicalRecordCount = bytesPerSector/Disk.LOGICAL_SECTOR_SIZE;
		int writeSector = -1;
		
		while(writeBuffer.hasRemaining()){
			
			//get a sector's worth of data
			try {
				writeBuffer.get(sectorData);
			} catch (BufferUnderflowException bufferUnderflowException) {
				Arrays.fill(sectorData, (byte)0X1A);
				writeBuffer.get(sectorData, 0, writeBuffer.remaining());
			}//try
			
			//get the physical sector to write to
			try {
				writeSector = sectorsToUse.remove();
			} catch (NoSuchElementException noSuchElementException) {
				if (directory.isEntryFull(directoryIndex)){
					directoryIndex = directory.getNextDirectoryExtent(directoryIndex);
				}//if need a new directory entry
				sectorsToUse = directory.storageFromBlock(directory.getMoreStorage(directoryIndex));
				writeSector = sectorsToUse.remove();
			}//try - get writeSector
			
			diskDrive.setCurrentAbsoluteSector(writeSector);
			diskDrive.write(sectorData);
			directory.incrementRc(directoryIndex, logicalRecordCount);
			
		//	System.out.printf("[] xxx %s%n", new String(sectorData));
		}//while
		
		overwriteDirectory();
		
		return true;
		
	}//writeNewFile
	
	private void overwriteDirectory() {
		if (directory == null) {
			return;
		}// if
		int firstDirectorySector = directory.getDirectoryStartSector();
		int lastDirectorySector = directory.getDirectoryLastSector();
		int entriesPerSector = bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;
		int directoryIndex = 0;
		for (int s = firstDirectorySector; s < lastDirectorySector + 1; s++) {
			byte[] sector = {};
			for (int i = 0; i < entriesPerSector; i++) {
				byte[] anEntry = directory.getRawDirectoryEntry(directoryIndex++);
				sector = concat(sector, anEntry);
			}// for
			diskDrive.setCurrentAbsoluteSector(s);
			diskDrive.write(sector);
		}// for -s
	}//	overwriteDirectory
	
	private byte[] concat(byte[] a, byte b[]) {
		int aLen = a.length;
		int bLen = b.length;
		byte[] ans = new byte[aLen + bLen];
		System.arraycopy(a, 0, ans, 0, aLen);
		System.arraycopy(b, 0, ans, aLen, bLen);
		return ans;
	}//concat

}// class CPMFile
