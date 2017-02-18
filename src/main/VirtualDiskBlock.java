package main;

public class VirtualDiskBlock {
	/*
	 * creates a block of size equal to 256 bytes.
	 */
	public VirtualDiskBlock(){
		
	}
	
	/*
	 * creates a block of size (number of bytes) equal to blockCapacity.
	 */
	public VirtualDiskBlock(int blockCapacity){
		
	}
	
	/*
	 * returns a positive integer value that corresponds to the capacity (number of character spaces or elements) of the current instance of block. 
	 */
	public int getCapaticy(){
		
		return 1;
	}
	
	/*
	 * changes the content of element at position index to that of nuevo in the current disk block instance. It is assumed that index is valid for the current disk block instance.
	 */
	public void setElement(int index, byte nuevo){
		
	}
	
	/*
	 * returns a copy of the character at the position index in the current block instance. It is assumed that index is valid for the current disk block instance.
	 */
	public byte getElement(int index){
		
		return 1;
	}

}
