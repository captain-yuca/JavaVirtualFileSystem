package main;
import java.io.RandomAccessFile;

import exceptions.*;


public class DiskAccessor {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
