package diskUtilities;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import exceptions.*;

/**
 * The Class DiskUnit simulates a Virtual Disk Unit. This class allows an instance to read, write, create and mount the units.
 * These units utilize the VirtualDiskBlock class as its blocks. The blocks are written into
 * a RandomAccessFile (RAF). Each RAF that is created via the create method corresponds to a Disk Unit 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class DiskUnit {
	private int capacity;
	private int blockSize;
	public RandomAccessFile disk;
	private final static int DEFAULT_CAPACITY = 1024;  // default number of blocks 	
	private final static int DEFAULT_BLOCK_SIZE = 256; // default number of bytes per block
	private final static int RESERVED_SPACE = 8; //reserved space for capacity and block size fields. 8 due to 4(int) + 4(int) 

	
	private DiskUnit(String name) {
		try {
			disk = new RandomAccessFile(name, "rw");
		}
		catch (IOException e) {
			System.err.println ("Unable to start the disk");
			System.exit(1);
		}
	}

	/**
	 * Writes the content of block b into the disk block corresponding to blockNum; i.e., 
	 * whatever is the actual content of the disk block corresponding to the specified block 
	 * number (blockNum) is changed to (or overwritten by) that of b in the current disk instance.
	 * @param blockNum The index of the block you want to write on.
	 * @param b The VirtualDiskBlock you want to write on the selected blockNumber.
	 * @throws InvalidBlockNumberException whenever the block number received is not valid for the 
	 * current disk instance.
	 * @throws InvalidBlockException whenever b does not represent a valid 
	 * disk block for the current disk instance (for example, if b is null, or if that block 
	 * instance does not match the block size of the current disk instance)
	 */
	public void write(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException{
		int offset = blockNum*this.getBlockSize();
		try {
			this.disk.seek(offset);
		} catch (IOException e1) {
			throw new InvalidBlockNumberException();
		}
		try {
			this.disk.write(b.elements);
		} catch (IOException e) {
			throw new InvalidBlockException();
		}

	}

	/**
	 * Reads a given block from the disk. The content of the specified 
	 * disk block (identified by its number � blockNum) is copied as the 
	 * new content of the current instance of block being referenced by parameter b. 
	 * Notice that b must reference an existing instance of VirtualDiskBlock, 
	 * and that the current content of that instance shall be overwritten by the 
	 * content of the disk block to be read. The announced exceptions are thrown 
	 * as described for the write operation. 
	 * @param blockNum The index of the block you want to read.
	 * @param b The VirtualDiskBlock you want the contents of the chosen block to be copied onto.
	 * @throws InvalidBlockNumberException whenever the block number received is not valid for the 
	 * current disk instance.
	 * @throws InvalidBlockException whenever b does not represent a valid 
	 * disk block for the current disk instance (for example, if b is null, or if that block 
	 * instance does not match the block size of the current disk instance)
	 */
	public void read(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException{
		int offset = blockNum*this.getBlockSize();

		try {
			for(int i=0; i<this.getBlockSize(); i++){
				this.disk.seek(offset+i);
				b.setElement(i, this.disk.readByte());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Returns the capacity of the DiskUnit instance.
	 * @return The capacity of the DiskUnit instance.
	 **/
	public int getCapacity(){
		int currentCapacity = 0;
		try {
			this.disk.seek(0);

			currentCapacity=this.disk.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return currentCapacity;
	}
	
	/** 
	 * Returns the blockSize of the VirtualDiskBlock instances utilized on this DiskUnit
	 * @return The blockSize of the VirtualDiskBlock instances utilized on this DiskUnit
	 **/
	public int getBlockSize(){
		int currentBlockSize = 0;
		try {
			this.disk.seek(RESERVED_SPACE/2);
			currentBlockSize = this.disk.readInt();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentBlockSize;
	}

	/** 
	 * Formats the disk. This operation visits every �physical block� 
	 * in the disk and fills with zeroes all those that are valid
	 **/
	public void lowLevelFormat(){

		
		//Only creating VirtualDiskBlock without any initialization since default value for the elements are 0
		VirtualDiskBlock formatDiskBlock = new VirtualDiskBlock(this.blockSize);
		for(int i=1; i<this.capacity; i++){
			this.write(i, formatDiskBlock);
		}
	}

	/** 
	 * Simulates shutting-off the disk. Just closes the corresponding RAF.
	 **/
	public void shutdown() {
		try {
			disk.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Turns on an existing disk unit whose name is given. If successful, it makes
	 * the particular disk unit available for operations suitable for a disk unit.
	 * @param name the name of the disk unit to activate
	 * @return the corresponding DiskUnit object
	 * @throws NonExistingDiskException whenever no
	 *    �disk� with the specified name is found.
	 */
	public static DiskUnit mount(String name)
			throws NonExistingDiskException
	{
		File file=new File(name);
		if (!file.exists())
			throw new NonExistingDiskException("No disk has name : " + name);

		DiskUnit dUnit = new DiskUnit(name);

		// get the capacity and the block size of the disk from the file
		// representing the disk
		try {
			dUnit.disk.seek(0);
			dUnit.capacity = dUnit.disk.readInt();
			dUnit.blockSize = dUnit.disk.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dUnit;     	
	}


	/***
	 * Creates a new disk unit with the given name. The disk is formatted
	 * as having default capacity (number of blocks), each of default
	 * size (number of bytes). Those values are: DEFAULT_CAPACITY and
	 * DEFAULT_BLOCK_SIZE. The created disk is left as in off mode.
	 * @param name the name of the file that is to represent the disk.
	 * @throws ExistingDiskException whenever the name attempted is
	 * already in use.
	 */
	public static void createDiskUnit(String name)
			throws ExistingDiskException
	{
		createDiskUnit(name, DEFAULT_CAPACITY, DEFAULT_BLOCK_SIZE);
	}


	/**
	 * Creates a new disk unit with the given name. The disk is formatted
	 * as with the specified capacity (number of blocks), each of specified
	 * size (number of bytes).  The created disk is left as in off mode.
	 * @param name the name of the file that is to represent the disk.
	 * @param capacity number of blocks in the new disk
	 * @param blockSize size per block in the new disk
	 * @throws ExistingDiskException whenever the name attempted is
	 * already in use.
	 * @throws InvalidParameterException whenever the values for capacity
	 *  or blockSize are not valid according to the specifications
	 */
	public static void createDiskUnit(String name, int capacity, int blockSize)
			throws ExistingDiskException, InvalidParameterException
	{
		File file=new File(name);
		if (file.exists())
			throw new ExistingDiskException("Disk name is already used: " + name);

		RandomAccessFile disk = null;
		if (capacity < 0 || blockSize < 0 ||
				!Utils.powerOf2(capacity) || !Utils.powerOf2(blockSize))
			throw new InvalidParameterException("Invalid values: " +
					" capacity = " + capacity + " block size = " +
					blockSize);
		// disk parameters are valid... hence create the file to represent the
		// disk unit.
		try {
			disk = new RandomAccessFile(name, "rw");
		}
		catch (IOException e) {
			System.err.println ("Unable to start the disk");
			System.exit(1);
		}

		reserveDiskSpace(disk, capacity, blockSize);

		// after creation, just leave it in shutdown mode - just
		// close the corresponding file
		try {
			disk.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Reserves the specified capacity of blocks with blockSize of the specified parameter.
	 * @param disk The disk you wish to reserve the space on.
	 * @param capacity Number of blocks you want to reserve.
	 */
	private static void reserveDiskSpace(RandomAccessFile disk, int capacity,
			int blockSize)
	{
		try {
			disk.setLength(blockSize * capacity);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// write disk parameters (number of blocks, bytes per block) in
		// block 0 of disk space
		try {
			disk.seek(0);
			disk.writeInt(capacity);  
			disk.writeInt(blockSize);
		} catch (IOException e) {
			e.printStackTrace();
		} 	
	}

}