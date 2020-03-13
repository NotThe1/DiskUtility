package support;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import appLogger.AppLogger;


public class DiskDrive {

	private String diskType;
	protected int heads;
	private int currentHead;
	protected int tracksPerHead;
	private int currentTrack;
	protected int sectorsPerTrack;
	private int currentSector;
	private int currentAbsoluteSector;
	protected int bytesPerSector;
	protected int sectorsPerHead;
	protected int totalSectorsOnDisk;
	protected long totalBytesOnDisk;
	private String fileAbsoluteName;
	private String fileLocalName;

	private FileChannel fileChannel;
	private MappedByteBuffer disk;
	private byte[] readSector;
	private ByteBuffer writeSector;

	private RandomAccessFile raf;

	private AppLogger log = AppLogger.getInstance();

	public DiskDrive(String strPathName, String sourceDiskPathName) {
		resolveDiskType(sourceDiskPathName);
		setupDisk(strPathName);
	}// Constructor

	public DiskDrive(String strPathName) {
		resolveDiskType(strPathName);
		setupDisk(strPathName);
	}// Constructor
	
	public DiskDrive(String strPathName,boolean readOnly) {
		resolveDiskType(strPathName);
		setupDisk(strPathName);
	}// Constructor

	private void setupDisk(String strPathName) {
		try {
			raf = new RandomAccessFile(strPathName, "rw");
			fileChannel = raf.getChannel();
			disk = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());// total Bytes on disk
			fileAbsoluteName = strPathName;

			fileLocalName = strPathName.substring(strPathName.lastIndexOf(File.separator)+1);

		} catch (IOException ioException) {
			log.error("[DiskDrive]: " + ERR_IO + ioException.getMessage());
			fireVDiskError(1l, ERR_IO + ioException.getMessage());
		} // try
		readSector = new byte[bytesPerSector];
		writeSector = ByteBuffer.allocate(bytesPerSector);

	}// setupDisk

	private void resolveDiskType(String strPathName) {
		Pattern patternFileType = Pattern.compile("\\.([^.]+$)");
		Matcher matcher = patternFileType.matcher(strPathName);
		String diskType = matcher.find() ? matcher.group(1).toLowerCase() : NONE;

		DiskMetrics diskMetric = DiskMetrics.getDiskMetric(diskType);
		if (diskMetric == null) {
			fireVDiskError((long) 2, "Not a Valid disk type " + diskType);
		} // if

		this.diskType = diskType;
		this.heads = diskMetric.heads;
		this.tracksPerHead = diskMetric.tracksPerHead;
		this.sectorsPerTrack = diskMetric.sectorsPerTrack;
		this.bytesPerSector = diskMetric.bytesPerSector;
		this.sectorsPerHead = diskMetric.getTotalSectorsPerHead();
		this.totalSectorsOnDisk = diskMetric.getTotalSectorsOnDisk();
		this.totalBytesOnDisk = diskMetric.getTotalBytes();
	}// resolveDiskType

	// ---------------------------------------

	public void dismount() {
		if (disk != null) {
			disk.force(); // flush					
			disk = null;
		} // if - MappedByteBuffer

		if (fileChannel != null) {
			try {
				fileChannel.close();
			} catch (IOException ioe) {
				log.warn("Failed Attempt to close fileChannel - " + ioe.getMessage());
			} // try - close
			fileChannel = null;
		} // if - FileChannel
		
		try {
			raf.close();
			raf = null;
		} catch (IOException ioe) {
			log.warn("Failed Attempt to dismount diskDrive - " + ioe.getMessage());
		} // try - RandomAccessFile
	
	}// dismount

	public byte[] read() {
		setSectorPosition();
		disk.get(readSector);
		return readSector.clone();
	}// read
	
	public byte[] readNext() {
		setCurrentAbsoluteSector(currentAbsoluteSector + 1);
		return read();
	}// readNext

	private void setSectorPosition() {
		int offset = currentAbsoluteSector * bytesPerSector;
		disk.position(offset);
	}// setSectorPosition

	public String getDiskType() {
		return this.diskType;
	}// getDiskType

	public int getCurrentAbsoluteSector() {
		return currentAbsoluteSector;
	}// getCurrentAbsoluteSector

	public int getCurrentHead() {
		return currentHead;
	}// getCurrentHead

	public int getCurrentTrack() {
		return currentTrack;
	}// getCurrentTrack

	public int getCurrentSector() {
		return currentSector;
	}// getCurrentSector

	public String getFileAbsoluteName() {
		return this.fileAbsoluteName;
	}// getFileAbsoluteName
	
	public String getFilePath() {
		return this.fileAbsoluteName;
	}// getFilePath

	public int getBytesPerSector() {
		return this.bytesPerSector;
	}// getBytesPerSector

	public String getFileLocalName() {
		return this.fileLocalName;
	}// getFileLocalName

	public String getFileName() {
		return this.fileLocalName;
	}// getFileName

	private void homeHeads() {
		this.currentHead = 0;
		this.currentTrack = 0;
		this.currentSector = 0;
		this.currentAbsoluteSector = 0;
	}// homeHeads

	public boolean setCurrentAbsoluteSector(int currentAbsoluteSector) {
		boolean result = false;
		if (validateAbsoluteSector(currentAbsoluteSector)) {
			this.currentAbsoluteSector = currentAbsoluteSector;
			int sectorsPerTrackHead = this.sectorsPerTrack * this.heads;
			int headSectors = currentAbsoluteSector % sectorsPerTrackHead;
			setCurrentHead(headSectors / sectorsPerTrack); // /sectorsPerTrackHead
			setCurrentTrack(currentAbsoluteSector / sectorsPerTrackHead);
			setCurrentSector((currentAbsoluteSector % sectorsPerTrack) + 1); // sectorsPerTrackHead
			result = true;
		} // if valid sector
		return result;
	}// setCurretAbsoluteSector

	public boolean setCurrentAbsoluteSector(int head, int track, int sector) {
		boolean result = false;
		if (validateHead(head) && validateSector(sector) && validateTrack(track)) {
			int absoluteSector = (sector - 1) + (head * this.sectorsPerTrack)
					+ (track * this.sectorsPerTrack * this.heads);
			if (validateAbsoluteSector(absoluteSector)) {
				this.currentAbsoluteSector = absoluteSector;
				this.currentHead = head;
				this.currentTrack = track;
				this.currentSector = sector;
				result = true;
			} // inner if
		} // if validate
		return result;
	}// setCurrentAbsoluteSector

	public void setCurrentHead(int currentHead) {
		if (validateHead(currentHead)) {
			this.currentHead = currentHead;
		} // if
	}// setCurrentHead

	public void setCurrentTrack(int currentTrack) {
		if (validateTrack(currentTrack)) {
			this.currentTrack = currentTrack;
		} // if
	}// setCurrentTrack

	public void setCurrentSector(int currentSector) {
		if (validateSector(currentSector)) {
			this.currentSector = currentSector;
		} // if
	}// setCurrentSector

	private boolean validateAbsoluteSector(int absoluteSector) {
		// between 0 and totalSectorsOnDisk - 1
		boolean result = true;
		if (!((absoluteSector >= 0) && (absoluteSector < totalSectorsOnDisk))) {
			homeHeads();
			fireVDiskError((long) absoluteSector, ERR_ABSOLUTE_SECTOR);
			result = false;
		} // if
		return result;
	}// validateAbsoluteSector

	private boolean validateHead(int head) {
		boolean result = true;
		// between 0 and heads-1
		if (!((head >= 0) & (head < heads))) {
			homeHeads();
			fireVDiskError((long) head, ERR_HEAD);
			result = false;
		} // if
		return result;
	}// validateHead

	private boolean validateTrack(int track) {
		boolean result = true;
		// between 0 and tracksPerHead-1
		if (!((track >= 0) & (track < tracksPerHead))) {
			homeHeads();
			fireVDiskError((long) track, ERR_TRACK);
			result = false;
		} // if
		return result;
	}// validateTrack

	private boolean validateSector(int sector) {
		// between 1 andsectorsPerTrack
		boolean result = true;
		if (!((sector > 0) & (sector <= sectorsPerTrack))) {
			homeHeads();
			log.error("Not valid sector - " + sector);
			fireVDiskError((long) sector, ERR_SECTOR);
			result = false;
		} // if
		return result;
	}// validateSector

	public void write(byte[] sector) {
		writeSector.clear();
		if (sector.length != bytesPerSector) {
			log.error("Not valid sector size - " + sector.length);
			fireVDiskError((long) sector.length, ERR_SECTOR);
		} else {
			setSectorPosition();
			disk.put(sector);
		} // if - sector not correct size
	}// write

	public void writeNext(byte[] sector) {
		setCurrentAbsoluteSector(currentAbsoluteSector + 1);
		write(sector);
	}// writeNext

	// ---------------------------------------
	private Vector<VDiskErrorListener> vdiskErrorListeners = new Vector<VDiskErrorListener>();

	public synchronized void addVDiskErrorListener(VDiskErrorListener vdel) {
		if (vdiskErrorListeners.contains(vdel)) {
			return; // Already have it
		} // if
		vdiskErrorListeners.addElement(vdel);
	}// addVDiskErrorListener

	public synchronized void removeVDiskErrorListener(VDiskErrorListener vdel) {
		vdiskErrorListeners.remove(vdel);
	}// removeVDiskErrorListener

	@SuppressWarnings("unchecked")
	private void fireVDiskError(long value, String errorMessage) {

		int size = vdiskErrorListeners.size();
		if (size == 0) {
			return; // No Listeners
		} // if

		VDiskErrorEvent vdee = new VDiskErrorEvent(this, value, errorMessage);

		Vector<VDiskErrorListener> vdels;
		synchronized (this) {
			vdels = (Vector<VDiskErrorListener>) vdiskErrorListeners.clone();
		} // sync

		for (VDiskErrorListener listener : vdels) {
			listener.vdiskError(vdee);
		} // for
	}// fireVDiskError

	// private static final String DISK_TYPES = "(?i)"
	private static final String NONE = "<none>";

	private static final String ERR_TRACK = "Invalid Track";
	private static final String ERR_HEAD = "Invalid Head";
	private static final String ERR_SECTOR = "Invalid Sector";
	private static final String ERR_ABSOLUTE_SECTOR = "Invalid Absolute Sector";
	private static final String ERR_IO = "Physical I/O Error - ";

}// class DiskDrive
