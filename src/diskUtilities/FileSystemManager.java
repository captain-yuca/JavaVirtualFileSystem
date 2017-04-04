package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.NonExistingDiskException;


//TODO: Clean Up Code
public class FileSystemManager {
	//TODO: Documentation
	final static int INODESIZE = 9;

	
	//TODO: Documentation
	public static void createFormattedDiskForUse(String name, int capacity, int blockSize){
		//verify how much space is needed to reserve
		double spaceAvailable = Math.floor(blockSize * capacity*0.01);
		
		double firstFreeBlock =1 + Math.ceil((spaceAvailable)/blockSize) +1;

		
		double totalINodes= spaceAvailable/INODESIZE;
		
		
		double iNodesPerBlock =  Math.ceil((double)(blockSize/INODESIZE));
		
		int nextFreeBlockIndex = (int)firstFreeBlock+1;
		DiskUnit.createDiskUnit(name, capacity, blockSize, (int)firstFreeBlock, (int)nextFreeBlockIndex, 2, (int)totalINodes);
		DiskUnit du=null;
		try {
			du = DiskUnit.mount(name);
		} catch (NonExistingDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
//		if(du.getFirstBlockIndex()==0);
			//TODO: Throw NotValidDiskException
		formatFreeINodes(du, (int)iNodesPerBlock);
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
	}
	
	//TODO:Documentation
	private static void formatFreeINodes(DiskUnit du, int iNodesPerBlock){
		//Total INodes that will be placed
		int totalINodes= du.getTotalINodes();
		int blockSize = du.getBlockSize();
		
		

		//Number of Blocks reserved for INodes
		int totalBlocksForINodes = totalINodes/iNodesPerBlock;
		//Counter to track which INode to set as the next free Inode
		int iNodeCounter=1;
		VirtualDiskBlock vdbToAdd;
		
		//TODO: Find better algorithm
		for(int i=1; i<totalBlocksForINodes+1; i++){
			vdbToAdd = new VirtualDiskBlock(blockSize);
			for(int j=0; j<blockSize-9; j=j+9){
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
		du.setFirstFreeINodeIndex(1);
		
		

		
		
	}
	
	//TODO: Documentation
	public static void writeFile(String fileToRead, String fileToCreate, DiskUnit du){
		
		File fileToCopy = new File(fileToRead);
		// This will reference one line at a time
		String line = null;
		
		//If the SystemFile exists, start reading the names
		if (fileToCopy.exists()){
			//TODO: Verify if there is a Free INode
			//TODO: Verify if there is a Free Space
			int[] freeINode=getFreeINode(du);
			int freeSpaceBlock = getFreeSpaceBlock(du);
			
			
			/*
			 * Obtain the String to write on the Disk
			 */
			String stringToCopy="";
			try {
				FileReader fileReader = new FileReader(fileToRead);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while((line = bufferedReader.readLine()) != null) {
					stringToCopy=stringToCopy+line + "\n";
				}	
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException ex) {
				System.out.println(
						"Error reading file '" 
								+ fileToRead + "'");                  
			}
			
			/*
			 * Register INode
			 */
			VirtualDiskBlock inodeVDB = new VirtualDiskBlock(du.getBlockSize());
			INode iNodeToAdd = new INode((byte)1, stringToCopy.length(), freeSpaceBlock);
			du.read(freeINode[0], inodeVDB);
			//First Data Block In File
			Utils.copyIntToBlock(inodeVDB, freeINode[1], iNodeToAdd.getFirstBlockIndex());
			//File Size
			Utils.copyIntToBlock(inodeVDB, freeINode[1]+4, iNodeToAdd.getFileSize());
			//Set type to file
			inodeVDB.setElement(freeINode[1]+8, (byte)1);
			
			//Place it back with the new information
			du.write(freeINode[0], inodeVDB);

			
			/*
			 * Copy the string to the file
			 */
			int currentBlockSizeCounter=0;
			int blockSize = du.getBlockSize();
			VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
			for(int i=0; i<stringToCopy.length(); i++){
				if(currentBlockSizeCounter>=blockSize-5){
					
					//Store reference to the current vdb its working on and
					//obtain the next free space block
					int currentTemp = freeSpaceBlock;
					freeSpaceBlock = getFreeSpaceBlock(du);
					
					//Adds reference to next disk block that contains file
					Utils.copyIntToBlock(vdb, blockSize-5, freeSpaceBlock);
					//Adds the block to the currentTemp
					du.write(currentTemp, vdb);
					
					//Reset vdb and currentBlockSizeCounter
					vdb = new VirtualDiskBlock(blockSize);
					currentBlockSizeCounter=0;
					
				}
				Utils.copyCharToBlock(vdb, currentBlockSizeCounter, stringToCopy.charAt(i));
				currentBlockSizeCounter++;
			}
			Utils.copyIntToBlock(vdb, blockSize-5, 0);
			du.write(freeSpaceBlock, vdb);

			
		}
	}
	
	//TODO:Documentation
	private static int getFreeSpaceBlock(DiskUnit du){
		int freeBlockIndex;
		int fbi = du.getFreeBlockIndex();
		if(du.getFirstBlockIndex()==0)
			return -1;
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
	//Returns the block in which the INode resides and the index where the INode Starts
	private static int[]getFreeINode(DiskUnit du){
		int freeINodeIndex=du.getFirstFreeINodeIndex();
		if(du.getFirstFreeINodeIndex()==0);
		//TODO: Throw NoFreeINodesException

		//Calculate the block in which it resides
		double INodesPerBlock = Math.floor((double)(du.getBlockSize()/INODESIZE));
		double freeINodeBlock = Math.ceil(freeINodeIndex/INodesPerBlock);
		double internalIndex = (freeINodeIndex-(INodesPerBlock*freeINodeBlock-INodesPerBlock))*INODESIZE;
		
		
		
		//TODO:Modify and Document names
		
		

		/*
		 * Set next INode
		 */
		
		//Access current free INode
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read((int)freeINodeBlock, vdb);
		
		//Read the next free INode index from current one
		//and set it as the next free INode
		
		du.setFirstFreeINodeIndex(Utils.getIntFromBlock(vdb, (int)internalIndex));
		
		int[] arrayToReturn = {(int)freeINodeBlock,(int)internalIndex};
		return arrayToReturn;
	}
	private static void configureRootINode(DiskUnit du){
		
	}
}
