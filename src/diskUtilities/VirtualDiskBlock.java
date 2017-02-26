package diskUtilities;

/**
 * The Class VirtualDiskBlock is a virtual representation of a physical disk block. 
 * Its composed of a sequence of a fixed number bytes (the size of the block) where data can be recorded. 
 * 
 * 
 * @author Manuel A. Baez Gonzalez
 */
public class VirtualDiskBlock {
	
	/**
	 * The bytes array (block)
	 */
	public byte[] elements;
	
	/**
	 * Creates a block of size equal to 256 bytes.
	 */
	public VirtualDiskBlock(){
		this.elements= new byte[256];
	}
	
	/**
	 * Creates a block of size (number of bytes) equal to blockCapacity.
	 * @param blockCapacity The desired number of bytes for the block.
	 */
	public VirtualDiskBlock(int blockCapacity){
		this.elements= new byte[blockCapacity];
	}
	
	/**
	 * Returns a positive integer value that corresponds to the capacity (number of character spaces or elements) of the current instance of block. 
	 * @return capacity of current block
	 */
	public int getCapacity(){
		return this.elements.length;
	}
	
	/**
	 * Changes the content of element at position index to that of nuevo in the current disk block instance. It is assumed that index is valid for the current disk block instance.
	 * @param index Position in which you want to set the new byte
	 * @param nuevo Byte you wish to set the element to
	 */
	public void setElement(int index, byte nuevo){
		this.elements[index] = nuevo;
	}
	
	/**
	 * Returns a copy of the character at the position index in the current block instance. It is assumed that index is valid for the current disk block instance.
	 * @param index Position in which the element to return is on
	 * @return The character at the position index
	 */
	public byte getElement(int index){
		return this.elements[index];
	}

}
