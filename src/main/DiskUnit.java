package main;
import exceptions.*;

public class DiskUnit {
	private int capacity;
	private int blockSize;
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
}
