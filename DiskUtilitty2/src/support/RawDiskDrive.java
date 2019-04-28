package support;

import java.nio.file.Path;

public class RawDiskDrive extends DiskDrive {

	public RawDiskDrive(Path path) {
		super(path);
	}//Constructor

	public RawDiskDrive(String strPathName) {
		super(strPathName);
	}//Constructor
	
	public RawDiskDrive(String strPathName, String sourceDiskPathName) {
		super(strPathName,sourceDiskPathName);
	}//Constructor  
	
	public int getHeads() {
		return heads;
	}//getHeads
	
	public int getTracksPerHead(){
		return tracksPerHead;
	}//getTracksPerHead
	
	public int getSectorsPerTrack(){
		return sectorsPerTrack;
	}//getSectorsPerTrack
	
	public int getBytesPerSector(){
		return bytesPerSector;
	}//getBytesPerSector
	
	public int getSectorsPerHead(){
		return sectorsPerHead;
	}//getSectorsPerHead
	
	public int getTotalSectorsOnDisk(){
		return totalSectorsOnDisk;
	}//getTotalSectorsOnDisk
	
	public int getTotalTracks(){
		return heads * tracksPerHead;
	}//getTotalTracks
	
	public long getTotalBytesOnDisk(){
		return totalBytesOnDisk;
	}//getTotalBytesOnDisk
	

}//class RawDiskDrive
