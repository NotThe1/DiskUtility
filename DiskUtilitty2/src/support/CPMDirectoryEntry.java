package support;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CPMDirectoryEntry {

	private byte[] rawDirectory;

	private boolean readOnly;
	private boolean systemFile;
	//String strEntry;
	private ArrayList<Integer> allocatedBlocks;

	private boolean bigDisk;

	public static CPMDirectoryEntry emptyDirectoryEntry(boolean bigDisk) {
		return new CPMDirectoryEntry(Disk.getEmptyDirectory(),bigDisk);
//		return new CPMDirectoryEntry(Disk.EMPTY_DIRECTORY_ENTRY,bigDisk);
	}//emptyDirectoryEntry(boolean bigDisk)
	
	public static CPMDirectoryEntry emptyDirectoryEntry() {
		return new CPMDirectoryEntry(Disk.getEmptyDirectory());
//		return new CPMDirectoryEntry(Disk.EMPTY_DIRECTORY_ENTRY);
	}//emptyDirectoryEntry()
	

	public void markAsDeleted() {
//		rawDirectory = Disk.EMPTY_DIRECTORY_ENTRY.clone();		
		rawDirectory = Disk.getEmptyDirectory();		
	}//markAsDeleted

	public CPMDirectoryEntry() {
		rawDirectory = new byte[Disk.DIRECTORY_ENTRY_SIZE];
		this.setUserNumber(Disk.EMPTY_ENTRY);
		allocatedBlocks = new ArrayList<Integer>();
		this.bigDisk = false;
	}// Constructor

	public CPMDirectoryEntry(byte[] rawEntry) {
		this(rawEntry, false);
	}// Constructor , bigDisk = false

	public CPMDirectoryEntry(byte[] rawEntry, boolean bigDisk) {
		this();
		rawDirectory = rawEntry.clone();
		this.bigDisk = bigDisk;
		
		if (rawDirectory[0]==Disk.EMPTY_ENTRY){
			return;
		}// if
		
		String strEntry =EMPTY_STRING;
		try {
			strEntry = new String(rawDirectory, "US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}//try

		this.setUserNumber(rawDirectory[0]);
		this.setFileName(strEntry.substring(Disk.DIR_NAME, Disk.DIR_NAME_END));
		this.setFileType(strEntry.substring(Disk.DIR_TYPE, Disk.DIR_TYPE_END));

		readOnly = ((rawDirectory[Disk.DIR_T1] & 0x80) == 0x80);
		systemFile = ((rawDirectory[Disk.DIR_T2] & 0x80) == 0x80);
		this.setEx(rawDirectory[Disk.DIR_EX]);
		this.setS2(rawDirectory[Disk.DIR_S2]);
		this.setRc(rawDirectory[Disk.DIR_RC]);
		setAllocationTable();
	}// Constructor - rawDirectory bigDisk
	
	public byte[] getRawDirectory(){
		return rawDirectory.clone();
	}//getRawDirectory

	private void setAllocationTable() {
		int value = 0;
		for (int i = Disk.DIR_BLOCKS; i < Disk.DIR_BLOCKS_END; i++) {
			value = bigDisk ? (int) ((rawDirectory[i] & 0xff) * 256) + (rawDirectory[++i] & 0xff)
					: rawDirectory[i] & 0xff;
			if (value != 0) {
				allocatedBlocks.add(value);
			}// inner if - value
		}// for
	}//setAllocationTable

	public String toString() {
		return getNameAndTypePeriod();
	}//toString

	// user number
	public int getUserNumberInt() {
		return (int) rawDirectory[Disk.DIR_USER] & 0xFF;
	}//getUserNumberInt

	public byte getUserNumber() {
		return rawDirectory[Disk.DIR_USER];
	}//getUserNumber

	public void setUserNumber(byte userNumber) {
		rawDirectory[Disk.DIR_USER] = userNumber;
	}//setUserNumber

	// File Name & Type
	public String getFileName() {
		byte[] name = new byte[Disk.NAME_MAX];
		for ( int i = 0; i < Disk.NAME_MAX; i++){
			name[i]= rawDirectory[i+1];
		}// for
		return new String(name);
	}//getFileName

	public String getFileNameTrim() {
		return getFileName().trim();
	}//getFileNameTrim
	
	public void setFilenameAndType(String nameAndType){
		String[] nat = nameAndType.split("\\.");
		String name = nat[0];
		String type = nat.length>1?nat[1]:EMPTY_STRING;
		
		this.setFileName(name);
		this.setFileType(type);
	}//setFilenameAndType

	public void setFileName(String fileName) {
		String namePad =padEntryField(fileName, Disk.NAME_MAX);
		byte[] nameRaw;
		try {
			nameRaw = namePad.getBytes("US-ASCII");
			for (int i = 0; i < Disk.NAME_MAX; i++) {
				rawDirectory[Disk.DIR_NAME + i] = nameRaw[i];
			}//for in try
		} catch (Exception e) {
			e.printStackTrace();
		}//try
	}//setFileName

	public String getFileType() {
		byte[] type = new byte[Disk.TYPE_MAX];
		for ( int i = 0; i < Disk.TYPE_MAX; i++){
			type[i]= rawDirectory[i+9];
		}//
		return new String(type);
	}//getFileType

	public String getFileTypeTrim() {
		return getFileType().trim();
	}//getFileTypeTrim

	public void setFileType(String fileType) {
		boolean wasReadOnly = this.readOnly;
		boolean wasSystemFile = this.systemFile;

		String typePad = padEntryField(fileType, Disk.TYPE_MAX);
		byte[] type;
		try {
			type = typePad.getBytes("US-ASCII");
			for (int i = 0; i < 0 + Disk.TYPE_MAX; i++) {
				rawDirectory[Disk.DIR_TYPE + i] = type[i];
			}//for
		} catch (UnsupportedEncodingException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}//try
		if (wasReadOnly) {
			rawDirectory[Disk.DIR_T1] = (byte) (rawDirectory[Disk.DIR_T1] | 0x80);
		}//if
		if (wasSystemFile) {
			rawDirectory[Disk.DIR_T2] = (byte) (rawDirectory[Disk.DIR_T2] | 0x80);
		}//if

		readOnly = ((rawDirectory[Disk.DIR_T1] & 0x80) == 0x80);
		systemFile = ((rawDirectory[Disk.DIR_T2] & 0x80) == 0x80);

	}//setFileType

	public String getNameAndType11() {
		String fileNamePadded = padEntryField(getFileName(), Disk.NAME_MAX);
		String fileTypePadded = padEntryField(getFileType(), Disk.TYPE_MAX);
		return fileNamePadded + fileTypePadded;
	}///getNameAndType11

	public String getNameTypeAndExt() {
		return getNameAndType11() + getActualExtNumString();
	}//getNameTypeAndExt

	public String getNameAndTypePeriod() {
		String result = getFileName().trim();
		if (getFileType().trim().length() != 0) {
			result += Disk.PERIOD + getFileType().trim();
		}
		return result;
	}//getNameAndTypePeriod

	private String padEntryField(String field, int fieldLength) {
		String result = field.trim().toUpperCase();
		result = result.length() > fieldLength ? result.substring(0, fieldLength) : result;
		result = String.format("%-" + fieldLength + "s", result);
		return result;
	}//padEntryField

	// Exstents values Ex & s2
	public byte getEx() {
		return rawDirectory[Disk.DIR_EX];
	}//getEx

	public void setEx(byte ex) {
		rawDirectory[Disk.DIR_EX] = (byte) (ex & 0x1F);
	}//setEx
	public int getExInt(){
		return rawDirectory[Disk.DIR_EX] & 0X1F;
	}//getExInt
	public void incEx(){
		setEx ((byte) (getExInt() +1)) ;
	}//incEx

	public byte getS1() {
		return rawDirectory[Disk.DIR_S1];   //Disk.NULL_BYTE;
	}//getS1

	public byte getS2() {
		return rawDirectory[Disk.DIR_S2];
	}//getS2

	public void setS2(byte s2) {
		rawDirectory[Disk.DIR_S2] = (byte) (s2 & 0x3F);
	}//setS2

	public int getActualExtentNumber() {
		return (getS2() * 32) + getEx(); // ( s2 * 32) + ex should max at 0x832
	}//getActualExtentNumber
	
	public void setActualExtentNumber(int actualExtentNumber){
		byte s = (byte) (actualExtentNumber / 32) ;
		byte e = (byte) (actualExtentNumber % 32);
		setS2(s);
		setEx(e);
	}//setActualExtentNumber

	private String getActualExtNumString() {
		return String.format("%03X", getActualExtentNumber());
	}//getActualExtNumString

	// 128-byte record count
	public int getRcInt() {
		return (int) rawDirectory[Disk.DIR_RC] & 0xFF;
	}//getRcInt

	public byte getRc() {
		return rawDirectory[Disk.DIR_RC];
	}//getRc

	public void setRc(int rc){
		setRc((byte)rc);
	}
	public void setRc(byte rc) {
		int rcInt = rc & 0xFF;
		rawDirectory[Disk.DIR_RC] = (byte) (rcInt > 0x80 ? 0x80 : rc); // 0x80 = this extent is full
	}//setRc
	
	public void incrementRc(int amount){
		this.setRc((byte) (this.getRc() + amount));
	}//incrementRc

	// Allocation table
	public void addBlock(int blockNumber) {
		allocatedBlocks.add(blockNumber);

		if (bigDisk) {
			for (int i = Disk.DIR_BLOCKS; i < Disk.DIR_BLOCKS_END; i += 2) {
				if ((rawDirectory[i] + rawDirectory[i + 1]) == Disk.NULL_BYTE) {
					rawDirectory[i] = (byte) (blockNumber / 256);
					rawDirectory[i + 1] = (byte) (blockNumber % 256);
					break; // we are done
				}// if
			}// for

		} else {
			for (int i = Disk.DIR_BLOCKS; i < Disk.DIR_BLOCKS_END; i++) {
				if (rawDirectory[i] == Disk.NULL_BYTE) {
					rawDirectory[i] = (byte) blockNumber;
					break; // we are done
				}// inner if
			}// for
		}// if
		return;
	}//addBlock

	public int getBlockCount() {
		return allocatedBlocks.size();
	}//getBlockCount

	public ArrayList<Integer> getAllocatedBlocks() {
		return allocatedBlocks;
	}//getAllocatedBlocks
	
	public void resetEntry(){
		this.setFileName(EMPTY_STRING);
		this.setFileType(EMPTY_STRING);
		this.setActualExtentNumber(0);
		this.setRc((byte) 0);
		allocatedBlocks.clear();
		for (int i = Disk.DIR_BLOCKS; i < Disk.DIR_BLOCKS_END; i++) {
			rawDirectory[i] = Disk.NULL_BYTE;
		}// for

	}//resetEntry

	public boolean isReadOnly() {
		return this.readOnly;
	}//isReadOnly

	public boolean isSystemFile() {
		return this.systemFile;
	}//isSystemFile

	public void setReadOnly(boolean state) {
		if (state) {
			rawDirectory[Disk.DIR_T1] = (byte) (rawDirectory[Disk.DIR_T1] | 0x80);
		} else {
			rawDirectory[Disk.DIR_T1] = (byte) (rawDirectory[Disk.DIR_T1] & 0x7F);
		}//if
	}//setReadOnly

	public void setSystemFile(boolean state) {
		if (state) {
			rawDirectory[Disk.DIR_T2] = (byte) (rawDirectory[Disk.DIR_T2] | 0x80);
		} else {
			rawDirectory[Disk.DIR_T2] = (byte) (rawDirectory[Disk.DIR_T2] & 0x7F);
		}//if
	}//setSystemFile
	
//	public boolean isEntryFull(){
//		int limit = isBigDisk()?Disk.DIRECTORY_ALLOC_SIZE_BIG:Disk.DIRECTORY_ALLOC_SIZE_SMALL;
//		return allocatedBlocks.size()>= limit;
//	}//isEntryFull
	public boolean isEntryFull() {
//		if (this.bigDisk) {
//			return this.getRcInt() >= Disk.DIRECTORY_ENTRY_RECORD_LIMIT;
//		} // done if big disk
//
//		if (this.getRcInt() >= Disk.DIRECTORY_ENTRY_RECORD_LIMIT) {
//			
//			if ((getEx() % 2) == 0) {
//				setEx((byte) (getEx() + 1)); // update the extent
//				setRc((byte) 0X00); // restart the record count
//				return false;
//			}else{
//				return true;
//			}// ex odd
//			
//		} else {
//			return false;
//		}// outter if		
		
		return this.getRcInt() >= Disk.DIRECTORY_ENTRY_RECORD_LIMIT;
	}//isEntryFull

	public boolean isEmpty() {
		return this.getUserNumber() == Disk.EMPTY_ENTRY;
	}//isEmpty

	// Supporting attributes
	public boolean isBigDisk() {
		return bigDisk;
	}//isBigDisk

	public void setBigDisk(boolean bigDisk) {
		this.bigDisk = bigDisk;
	}//setBigDisk
	
	private static final String EMPTY_STRING = "";

}//class CPMDirectoryEntry
