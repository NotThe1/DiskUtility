package support;

// Contains all the constants used by the classes in the package - disks
public class Disk {

	public Disk() {
		// TODO Auto-generated constructor stub
	}//Constructor
	
	public static byte[] getEmptyDirectory() {
		return new byte[] { Disk.EMPTY_ENTRY, Disk.NULL_BYTE, Disk.NULL_BYTE,
				Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
				Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
				Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
				Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
				Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE };
	}

	// Constants
	// for actual number of drives

	public static final int NUMBER_OF_DISKS = 4;

	// for disk Metrics
	public static final String TYPE_3DD = "F3DD";
	public static final String TYPE_3HD = "F3HD";
	public static final String TYPE_3ED = "F3ED";
	public static final String TYPE_5DD = "F5DD";
	public static final String TYPE_5HD = "F5HD";
	public static final String TYPE_8SS = "F8SS";
	public static final String TYPE_8DS = "F8DS";

	public static final int LOGICAL_SECTOR_SIZE = 128;
	public static final int RECORD_SIZE = 128;
	public static final int SYSTEM_SIZE = 0X2000;
	public static final int SYSTEM_LOGICAL_BLOCKS = SYSTEM_SIZE / LOGICAL_SECTOR_SIZE;
	public static final int SYSTEM_RECORDS = SYSTEM_LOGICAL_BLOCKS;
	public static final int DIRECTORY_ENTRY_SIZE = 32;
	public static final int DIRECTORY_ENTRYS_PER_LOGICAL_SECTOR = LOGICAL_SECTOR_SIZE / DIRECTORY_ENTRY_SIZE;
	public static final int DIRECTORY_ENTRYS_PER_RECORD = LOGICAL_SECTOR_SIZE / DIRECTORY_ENTRY_SIZE;
	public static final int DIRECTORY_ALLOC_SIZE_SMALL = 16;
	public static final int DIRECTORY_ALLOC_SIZE_BIG = DIRECTORY_ALLOC_SIZE_SMALL / 2;
	public static final int DIRECTORY_ENTRY_RECORD_LIMIT = 0X80; // 128

	public static final byte NULL_BYTE = (byte) 0x00;
	public static final byte EMPTY_ENTRY = (byte) 0xE5;
	public static final String EMPTY_NAME = "        ";
	public static final String EMPTY_TYPE = "   ";
	public static final String PERIOD = ".";

	public static final int NAME_MAX = 8;
	public static final int TYPE_MAX = 3;

	public final static int DIR_USER = 0;
	public final static int DIR_NAME = 1;
	public final static int DIR_NAME_SIZE = 8;
	public final static int DIR_NAME_END = DIR_NAME + DIR_NAME_SIZE;
	public final static int DIR_TYPE = 9;
	public final static int DIR_TYPE_SIZE = 3;
	public final static int DIR_TYPE_END = DIR_TYPE + DIR_TYPE_SIZE;
	public final static int DIR_T1 = 9;
	public final static int DIR_T2 = 10;
	public final static int DIR_EX = 12; // LOW BYTE
	public final static int DIR_S2 = 14;
	public final static int DIR_S1 = 13;
	public final static int DIR_RC = 15;
	public final static int DIR_BLOCKS = 16;
	public final static int DIR_BLOCKS_SIZE = 16;
	public final static int DIR_BLOCKS_END = DIR_BLOCKS + DIR_BLOCKS_SIZE;
	// private final static int DIR_SMALL_BLOCKS_COUNT = 16;
	// private final static int DIR_BIG_BLOCKS_COUNT = 8;

//	protected final static byte[] EMPTY_DIRECTORY_ENTRY = new byte[] { Disk.EMPTY_ENTRY, Disk.NULL_BYTE, Disk.NULL_BYTE,
//			Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
//			Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
//			Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
//			Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE,
//			Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE, Disk.NULL_BYTE };

}//class Disk
