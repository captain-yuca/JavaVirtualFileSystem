package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.NonExistingDiskException;

public class FileSystemManager {
	//TODO: Documentation
	final static int INODESIZE = 9;

	
	//TODO: Documentation
	public static void createFormattedDiskForUse(String name, int capacity, int blockSize){
		//verify how much space is needed to reserve
		double spaceAvailable = Math.floor(blockSize * capacity*0.01);
		
		double firstFreeBlock =1 + Math.ceil((spaceAvailable)/blockSize) +1;

		
		double totalINodes= spaceAvailable/INODESIZE;
		
		
		System.out.println(totalINodes);
		double iNodesPerBlock =  Math.ceil((double)(blockSize/INODESIZE));
		
		System.out.println(firstFreeBlock);
		int nextFreeBlockIndex = (int)firstFreeBlock+1;
		DiskUnit.createDiskUnit(name, capacity, blockSize, (int)firstFreeBlock, (int)nextFreeBlockIndex, 2, (int)totalINodes);
		DiskUnit du=null;
		try {
			du = DiskUnit.mount(name);
		} catch (NonExistingDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(du.getFirstBlockIndex()==0)
			//TODO: Throw NotValidDiskException
		formatFreeINodes(du, (int)spaceAvailable);
		formatFreeBlocks(du);
		

		

		
		
		
		
	}
	
	//TODO: Documentation
	private static void formatFreeBlocks(DiskUnit du){
		//References of disks you can write per diskblock
		int referencesPerBlock = du.getBlockSize()/4;
		
		//Counter to mark on which diskblock you are referencing
		int freeBlockCounter=du.getFirstBlockIndex();
		
		int currentVDB=0;
		
		//While the counter doesn't exceed the index of the blocks
		//(So it doesn't reference a non-existing free block), keep writing references
		while(freeBlockCounter<du.getCapacity()){
			
			VirtualDiskBlock vdbToAdd = new VirtualDiskBlock();
			
			//Writes the reference to the previous block that has references
			Utils.copyIntToBlock(vdbToAdd, 0 , currentVDB);
			
			//Sets the current virtualDiskBlock to the one that is going to have
			//the new references
			currentVDB=freeBlockCounter;
			
			//Start the counter to the next available free block instead of the one 
			//you will write on.
			freeBlockCounter++;
			
			//Add references for Free DiskBlocks to vdbToAdd
			for(int i = 1; i<referencesPerBlock; i++){
				Utils.copyIntToBlock(vdbToAdd, i*4 , freeBlockCounter);
				freeBlockCounter++;
				if(freeBlockCounter>=du.getCapacity())
					break;
			}			
			//Writes the block with references to the disk unit
			du.write(currentVDB, vdbToAdd);	
		}
		//Register the last reference block
		
		du.setFirstBlockIndex(currentVDB);
		du.setFreeBlockIndex(freeBlockCounter-1);
		System.out.println(du.getFreeBlockIndex());
	}
	
	private static void formatFreeINodes(DiskUnit du, int iNodesPerBlock){
		//Total INodes that will be placed
		int totalINodes= du.getTotalINodes();
		int blockSize = du.getBlockSize();
		//Number of Blocks reserved for INodes
		int totalBlocksForINodes = totalINodes/iNodesPerBlock;
		//Counter to track which INode to set as the next free Inode
		int iNodeCounter=2;
		VirtualDiskBlock vdbToAdd;
		
		//TODO: Find better algorithm
		for(int i=0; i<totalBlocksForINodes; i++){
			vdbToAdd = new VirtualDiskBlock();
			for(int j=0; j<blockSize; j+=9){
				Utils.copyIntToBlock(vdbToAdd, j, iNodeCounter);
				Utils.copyIntToBlock(vdbToAdd, j+4, 0);
				vdbToAdd.setElement(j+8, (byte) 0);
				iNodeCounter++;
				if(iNodeCounter>=totalINodes){
					Utils.copyIntToBlock(vdbToAdd, j, iNodeCounter);
					break;
				}
					
			}
			du.write(i, vdbToAdd);
			
		}
		du.setFirstFreeINodeIndex(2);
		
		

		
		
	}
	
	//TODO: Documentation
	public static void writeFile(String fileToRead, String fileToCreate, DiskUnit du){
		
		
		
		File fileToCopy = new File(fileToRead);
		// This will reference one line at a time
		String line = null;
		
		//If the SystemFile exists, start reading the names
		if (fileToCopy.exists()){
			
			try {
				FileReader fileReader = new FileReader(fileToRead);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}				
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException ex) {
				System.out.println(
						"Error reading file '" 
								+ fileToRead + "'");                  
			}
		}
	}
	
	//TODO:Documentation
	private static int getFreeSpaceBlock(DiskUnit du){
		int freeBlockIndex;
		int fbi = du.getFreeBlockIndex();
		if(du.getFirstBlockIndex()==0);
			//TODO: FullDiskException
		
		//If the disk has space
		if(fbi!=0){
			freeBlockIndex=fbi;
			du.setFreeBlockIndex(fbi-1);
		}
		
		//The root of the reference block (which is the previous reference block)
		//will be returned
		else{
			freeBlockIndex= du.getFirstBlockIndex();
			du.setFirstBlockIndex(fbi);
			du.setFreeBlockIndex(du.getBlockSize()/4 -1);
		}
		return freeBlockIndex;
	}
	
	//TODO:Documentation
	private static int getFreeINode(DiskUnit du){
		int freeINodeIndex=-1;
		if(du.getFirstFreeINodeIndex()==0)
			//TODO: Throw NoFreeINodesException
		
		//TODO:Modify and Document names
		freeINodeIndex=du.getFirstFreeINodeIndex();
		//Calculate where is next INode
		double INodesPerBlock = Math.ceil((double)(du.getBlockSize()/INODESIZE));
		double nextBlock= Math.floor(freeINodeIndex/INodesPerBlock);
		double nextFreeINodeIndex= nextBlock*du.getBlockSize()-freeINodeIndex;
		VirtualDiskBlock blockToRead=null;
		du.read((int)nextBlock, blockToRead);
		du.setFirstFreeINodeIndex(Utils.getIntFromBlock(blockToRead, (int)nextFreeINodeIndex));
		return freeINodeIndex;
	}
}
