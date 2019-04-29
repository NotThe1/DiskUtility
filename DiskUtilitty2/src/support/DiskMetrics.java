package support;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class DiskMetrics {

	public int heads;
	public int tracksPerHead;
	public int sectorsPerTrack;
	public int bytesPerSector;
//	public String fileExtension;
//	public String descriptor;
	public int sectorsPerBlock;
	public int directoryBlockCount;
//	public int directoryStartSector;

	// Block size
	private int blockSizeInBytes;
	private boolean bootDisk = true;

	private DiskMetrics() {
	}// Constructor

	private DiskMetrics(int heads, int tracksPerHead, int sectorsPerTrack, int bytesPerSector, int sectorsPerBlock,
			int directoryBlockCount, boolean bootDisk, String fileExtension, String descriptor) {
		this.heads = heads;
		this.tracksPerHead = tracksPerHead;
		this.sectorsPerTrack = sectorsPerTrack;
		this.bytesPerSector = bytesPerSector;
//		this.fileExtension = fileExtension;
//		this.descriptor = descriptor;
		setDirectoryBlockCount(directoryBlockCount);
		setSectorsPerBlock(sectorsPerBlock);
		setBootDisk(bootDisk);
	}// Constructor
	
	public static DiskMetrics getDiskMetric(String diskType) {
		Object[] setupValues;
		switch (diskType.trim().toUpperCase()) {
		case Disk.TYPE_3DD:
			setupValues = f3DD;
			break;
		case Disk.TYPE_3ED:
			setupValues = f3ED;
			break;
		case Disk.TYPE_3HD:
			setupValues = f3HD;
			break;
		case Disk.TYPE_5DD:
			setupValues = f5DD;
			break;
		case Disk.TYPE_5HD:
			setupValues = f5HD;
			break;
		case Disk.TYPE_8DS:
			setupValues = f8DS;
			break;
		case Disk.TYPE_8SS:
			setupValues = f8SS;
			break;
		default:
			return null;
		}// switch
		return new DiskMetrics((int) setupValues[0], (int) setupValues[1], (int) setupValues[2], (int) setupValues[3],
				(int) setupValues[4], (int) setupValues[5], (boolean) setupValues[6],
				(String) setupValues[7], (String) setupValues[8]);
	}//getDiskMetric	
	

	private void setDirectoryBlockCount(int directoryBlockCount) {
		this.directoryBlockCount = directoryBlockCount <= 0 ? 1 : directoryBlockCount;
	}// setDirectoryBlockCount

	private void setSectorsPerBlock(int sectorsPerBlock) {
		this.sectorsPerBlock = sectorsPerBlock <= 0 ? 1 : sectorsPerBlock;
		this.blockSizeInBytes = this.sectorsPerBlock * this.bytesPerSector;
	}// setSectorsPerBlock

	public
	void setBootDisk(boolean state) {
		this.bootDisk = state;
	}// setBootDisk
	// ==============================================================================================================

	// AL0 & AL1 -(AL01) Directory Allocation bits
	public int getAL01() {
		int al = 0xFF0000 >> this.getDirectoryBlockCount();
		return al & 0xFFFF;
	}//getAL01
	
	// BLM - Block Mask - Block size = 128 * (BLM-1)
	public int getBLM() {
		return (blockSizeInBytes / Disk.LOGICAL_SECTOR_SIZE) - 1;
	}//getBLM

	// BSH - Block Shift - block size is given by disk.LOGICAL_SECTOR_SIZE * (2**BSH)
	public int getBSH() {
		int targetValue = this.blockSizeInBytes / Disk.LOGICAL_SECTOR_SIZE;
		int value = 1;
		int bsh = 0;
		for (int i = 0; value <= targetValue; i++) {
			value *= 2;
			bsh = i;
		}
		return bsh;
	}//getBSH
	
	public int getBytesPerBlock(){
		return this.blockSizeInBytes;
	}//getBytesPerBlock
	
	// CKS - Check Area Size
	public int getCKS() {
		return (getDRM() + 1) / Disk.DIRECTORY_ENTRYS_PER_LOGICAL_SECTOR;
	}//getCKS

	public int getDirectoryBlockCount() {
		return this.directoryBlockCount;
	}//getDirectoryBlockCount

	public int getDirectoryLastSector() {
		return (getDirectoryStartSector() + (directoryBlockCount * this.sectorsPerBlock) - 1);
	}//getDirectorysLastSector

	public int getDirectoryStartSector() {
		int ans = 0;
		if (bootDisk) {
			ans = (this.sectorsPerTrack * this.heads);
		}//if
		return ans;
	}//getDirectoryStartSector
	
	public int getDirectoryEntriesPerSector() {
		return this.bytesPerSector / Disk.DIRECTORY_ENTRY_SIZE;
	}//getDirectoryEntriesPerSector

	// DRM - Highest Disk Entry Position
	public int getDRM() {
		int drm = (this.blockSizeInBytes * this.getDirectoryBlockCount()) / Disk.DIRECTORY_ENTRY_SIZE;
		return drm - 1;
	}//getDRM

	// DSM - Highest Block Number
	public int getDSM() {
		int totalPhysicalSectorsOnDisk = this.getTotalSectorsOnDisk();
		int totalPhysicalSectorsOnOFS = getOFS() * this.heads * this.sectorsPerTrack;
		return ((totalPhysicalSectorsOnDisk - totalPhysicalSectorsOnOFS) / sectorsPerBlock) - 1;
	}//getDSM
	
	// EXM - Extent Mask
	public int getEXM() {
		int sizefactor = (getDSM() < 256) ? 1024 : 2048;
		return (blockSizeInBytes / sizefactor) - 1;
	}//getEXM

	// Number of Logical(128-byte) Sectors per Physical Sectors
	public int getLSperPS() {
		return this.bytesPerSector / Disk.LOGICAL_SECTOR_SIZE;
	}//getLSperPS

	public int getMaxDirectoryEntries() {
		return (directoryBlockCount * blockSizeInBytes) / Disk.DIRECTORY_ENTRY_SIZE;
	}//getMaxDirectoryEntries
	
	// OFS - Cylinder offset
	public int getOFS() {
		int ans = 0;
		if (bootDisk) {
			float floatSize = (Disk.SYSTEM_LOGICAL_BLOCKS + 1) / (float) getSPT();
			ans = (int) Math.ceil(floatSize);
		}//if
		return ans;
	}//getOFS

	public int getSectorsPerBlock(){
		return this.sectorsPerBlock;
	}//getSectorsPerBlock


	// SPT- Number of Logical(128-byte) Sectors per Logical Track
	public int getSPT() {
		return this.sectorsPerTrack  * getLSperPS();
	}//getSPT

	public long getTotalBytes() {
		return getTotalSectorsOnDisk() * bytesPerSector;
	}//getTotalBytes


	public int getTotalSectorsOnDisk() {
		return heads * tracksPerHead * sectorsPerTrack;
	}//getTotalSectorsOnDisk

	public int getTotalSectorsPerHead() {
		return tracksPerHead * sectorsPerTrack;
	}//getTotalSectorsPerHead

	public boolean isBigDisk() {
		return this.getDSM() > 255 ? true : false;
	}//isBigDisk

	public boolean isBootDisk() {
		return this.bootDisk;
	}//isBootDisk


	public Queue<Integer> storageFromBlock(int blockNumber){
		Queue<Integer> ans =  new LinkedList<Integer>();
		int firstDirectorySector = this.getDirectoryStartSector();
		int sector = firstDirectorySector + ( blockNumber * this.sectorsPerBlock);
		for ( int i = 0; i< this.sectorsPerBlock; i++){
			ans.offer(sector++);
		}//for		
		return ans;
	}//storageFromBlock
	
	public ArrayList<Integer> sectorsFromBlock(int blockNumber){
		ArrayList<Integer> ans  = new ArrayList<Integer>();
		int firstDirectorySector = this.getDirectoryStartSector();
		int sector = firstDirectorySector + ( blockNumber * this.sectorsPerBlock);
		for ( int i = 0; i< this.sectorsPerBlock; i++){
			ans.add(sector++);
		}//for		
		return ans;
	}//sectorsFromBlock

	
	// ==============================================================================================================

	private static final Object[] f3DD = new Object[] { 2, 80, 9, 512, 4, 2, true, "F3DD", "3.5\"  DD   720 KB" };
	private static final Object[] f3HD = new Object[] { 2, 80, 18, 512, 4, 2, true, "F3HD", "3.5\"  HD   1.44 MB" };
	private static final Object[] f3ED = new Object[] { 2, 80, 36, 512, 4, 2, true, "F3ED", "3.5\"  ED   2.88 MB" };

	private static final Object[] f5DD = new Object[] { 2, 40, 9, 512, 4, 2, true, "F5DD", "5.25\" DD   360 KB" };
	private static final Object[] f5HD = new Object[] { 2, 80, 15, 512, 4, 2, true, "F5HD", "5.25\" HD   1.2 MB" };
	private static final Object[] f8SS = new Object[] { 1, 77, 26, 128, 8, 2, true, "F8SS", "8\"    SS   256 KB" };
	private static final Object[] f8DS = new Object[] { 2, 77, 26, 128, 8, 2, true, "F8DS", "8\"    DS   512 KB" };

//	private static final Object[] allFileTypes = new Object[] { f5DD, f5HD, f8SS, f8DS, f3DD, f3HD, f3ED };

}// class DiskMetrics
