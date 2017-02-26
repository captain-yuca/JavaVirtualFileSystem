package testers;
import diskUtilities.*;
import exceptions.NonExistingDiskException;
public class DiskUnitTester1 {

	/**
	 * Main method for tester
	 * @param args
	 * @throws NonExistingDiskException 
	 */
	public static void main(String[] args) throws NonExistingDiskException {
		DiskUnit d = DiskUnit.mount("disk1"); // edit the name of the disk to mount
		
	    showDiskContent(d); 
		
		showFileInDiskContent(d);   
		d.shutdown(); 
	}

	/**
	 * Shows all the content beginning from block 1
	 * @param d The Disk you want to show the content from
	 */
	private static void showFileInDiskContent(DiskUnit d) { 
		VirtualDiskBlock vdb = new VirtualDiskBlock(d.getBlockSize()); 
		
		System.out.println("\nContent of the file begining at block 1"); 
		int bn = 1; 
		while (bn != 0) { 
			d.read(bn, vdb); 
			showVirtualDiskBlock(bn, vdb);
			bn = getNextBNFromBlock(vdb);			
		}
		
	}

	/**
	 * Shows all the DiskUnits content
	 * @param d The DiskUnit desired to show it's content
	 */
	private static void showDiskContent(DiskUnit d) { 
		
		System.out.println("Capacity of disk is: " + d.getCapacity()); 
		System.out.println("Size of blocks in the disk is: " + d.getBlockSize()); 
		
		VirtualDiskBlock block = new VirtualDiskBlock(d.getBlockSize()); 
		for (int b = 0; b < d.getCapacity(); b++) { 
			d.read(b, block); 
			showVirtualDiskBlock(b, block); 
		}
		
	}
	
	/**
	 * Shows the desired block's content
	 * @param b The position of the corresponding block according to the DiskUnit
	 * @param block The VirtualDiskBlock you wish to read from
	 */
	private static void showVirtualDiskBlock(int b, VirtualDiskBlock block) {
	    System.out.print(" Block "+ b + "\t"); 
	    for (int i=0; i<block.getCapacity(); i++) {
	    	char c = (char) block.getElement(i); 
	    	if (Character.isLetterOrDigit(c))
	    		System.out.print(c); 
	    	else
	    		System.out.print('-'); 
	    }
	    System.out.println(); 
	}
	
	
	/**
	 * Internal method to make sure string can be written completely on Disk
	 * @param vdb The Virtual Disk Block you are working with
	 * @param value The desired value to copy next
	 */
	public static void copyNextBNToBlock(VirtualDiskBlock vdb, int value) { 
		int lastPos = vdb.getCapacity()-1;

		for (int index = 0; index < 4; index++) { 
			vdb.setElement(lastPos - index, (byte) (value & 0x000000ff)); 	
			value = value >> 8; 
		}

	}
	/**
	 * Gets next block number
	 * @param vdb The Virtual Disk Block you are working with
	 */
	private static int getNextBNFromBlock(VirtualDiskBlock vdb) { 
		int bsize = vdb.getCapacity(); 
		int value = 0; 
		int lSB; 
		for (int index = 3; index >= 0; index--) { 
			value = value << 8; 
			lSB = 0x000000ff & vdb.getElement(bsize-1-index);
			value = value | lSB; 
		}
		return value; 

	}

}



