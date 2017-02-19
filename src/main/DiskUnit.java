package main;
import java.io.RandomAccessFile;

import exceptions.*;

public class DiskUnit {
	private int capacity;
	private int blockSize;
	private RandomAccessFile disk;
	private final int DEFAULT_CAPACITY = 1024;  // default number of blocks 	
	private final int DEFAULT_BLOCK_SIZE = 256; // default number of bytes per block

	/* writes the content of block b into the disk block corresponding to blockNum; i.e., 
	 * whatever is the actual content of the disk block corresponding to the specified block 
	 * number (blockNum) is changed to (or overwritten by) that of b in the current disk instance. 
	 * The first exception is thrown whenever the block number received is not valid for the 
	 * current disk instance. The second exception is thrown whenever b does not represent a valid 
	 * disk block for the current disk instance (for example, if b is null, or if that block 
	 * instance does not match the block size of the current disk instance)
	 */
	private DiskUnit(){
//		this.disk = new RandomAccessFile(null, null);
	}
	public void write(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException{
		
		return;
	}
	
	/*: reads a given block from the disk. The content of the specified 
	 * disk block (identified by its number – blockNum) is copied as the 
	 * new content of the current instance of block being referenced by parameter b. 
	 * Notice that b must reference an existing instance of VirtualDiskBlock, 
	 * and that the current content of that instance shall be overwritten by the 
	 * content of the disk block to be read. The announced exceptions are thrown 
	 * as described for the write operation. 
	 */
	public void read(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException{
		return;
	}
	
	/*Formats the disk. This operation visits every “physical block” 
	 * in the disk and fills with zeroes all those that are valid
	 */
	public void lowLevelFormat(){
		
	}
	
	/*turns off the current disk unit. It saves all data needed in 
	 * order for the same content of the disk to be available whenever 
	 * the disk is activated (or  mounted) in the future.
	 */
	public void shutdown(){
		
	}
	
	/**
	 * Turns on an existing disk unit whose name is given. If successful, it makes
	 * the particular disk unit available for operations suitable for a disk unit.
	 * @param name the name of the disk unit to activate
	 * @return the corresponding DiskUnit object
	 * @throws NonExistingDiskException whenever no
	 *    ¨disk¨ with the specified name is found.
	*/
	public static DiskUnit mount(String name) throws NonExistingDiskException{  
		DiskUnit du1 = new DiskUnit();
		return du1;
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
	public static void createDiskUnit(String name) throws ExistingDiskException{
		
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
	public static void createDiskUnit(String name, int capacity, int blockSize) throws ExistingDiskException, 
	InvalidParameterException{
	}
	   

	private static void reserveDiskSpace(RandomAccessFile disk, int capacity, int blockSize){
		
	}
}
