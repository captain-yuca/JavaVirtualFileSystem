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
		DiskUnit.createDiskUnit(name, capacity, blockSize, (int)firstFreeBlock, (int)nextFreeBlockIndex, 1, (int)totalINodes);
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
		configureRootINode(du);
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
			//Obtain free INode
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
			
			//Register it in the root
			registerFileInDiskUnitRoot(du, fileToCreate, freeINode[2]);

			
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
			//Write 
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
		double freeINodeBlock = Math.max(1,Math.ceil(freeINodeIndex/INodesPerBlock));;
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
		
		int[] arrayToReturn = {(int)freeINodeBlock,(int)internalIndex, freeINodeIndex};
		return arrayToReturn;
	}
	
	//TODO:Documentation
	private static void configureRootINode(DiskUnit du){
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(1, vdb);
		Utils.copyIntToBlock(vdb, 0, 0);
		//TODO:Delete and fix
		getFreeSpaceBlock(du);
		du.write(1, vdb);
		
		
	}
	
	//TODO:Documentation
	private static void registerFileInDiskUnitRoot(DiskUnit du, String fileName, int iNodeReference){
		INode fileINode = obtainINodeFromIndex(iNodeReference,du);
		int freeBlock=-1;
		int currentBlockSizeCounter=0;
		INode rootINode = obtainINodeFromIndex(0,du);
		
		int blockSize = du.getBlockSize();
		VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
		
		//We need to append the new name to the file
		if(!isDirectoryEmpty(0, du)){
			
			//Append
			
			//Navigate to File
			
			int blocksToTraverse = rootINode.getFileSize()/du.getBlockSize();
			
			int eofIndexPosition=rootINode.getFileSize()-blocksToTraverse*du.getBlockSize()-1; 
			int eofBlock = rootINode.getFirstBlockIndex();
			VirtualDiskBlock vdbToRead = new VirtualDiskBlock(du.getBlockSize());
			
			//Find last part of the file
			for(int i=0; i<blocksToTraverse;i++){
				du.read(eofBlock, vdbToRead);
				eofBlock = Utils.getIntFromBlock(vdbToRead, du.getBlockSize()-5);
			}
			rootINode.setFileSize(rootINode.getFileSize()+24);
			
			freeBlock=eofBlock;
			currentBlockSizeCounter=eofIndexPosition;
			du.read(eofBlock, vdb);
			
		}
		//There is nothing on the directory file
		else{
			freeBlock = getFreeSpaceBlock(du);
			currentBlockSizeCounter=0;
			rootINode.setFileSize(24);
			rootINode.setFirstBlockIndex(freeBlock);

		}
		//This methods assumes that the String given is 20 or less characters
		//To maintain uniformity, we will add spaces to the file where the names are held
		int remainingLength = 20-fileName.length();
		
		/*
		 * Add the size of the directory file
		 */
		
		
		
		
		
		/*
		 * Writes the name
		 */
		for(int i=0; i<fileName.length(); i++){
			if(currentBlockSizeCounter>=blockSize-5){
				
				//Store reference to the current vdb its working on and
				//obtain the next free space block
				int currentTemp = freeBlock;
				freeBlock = getFreeSpaceBlock(du);
				
				//Adds reference to next disk block that contains file
				Utils.copyIntToBlock(vdb, blockSize-5, freeBlock);
				//Adds the block to the currentTemp
				du.write(currentTemp, vdb);
				
				//Reset vdb and currentBlockSizeCounter
				vdb = new VirtualDiskBlock(blockSize);
				currentBlockSizeCounter=0;
				
			}
			Utils.copyCharToBlock(vdb, currentBlockSizeCounter, fileName.charAt(i));
			currentBlockSizeCounter++;
		}
		
		/*
		 * Writes the additional spaces
		 */
		for(int i=0; i<remainingLength;i++){
			if(currentBlockSizeCounter>=blockSize-5){
				
				//Store reference to the current vdb its working on and
				//obtain the next free space block
				int currentTemp = freeBlock;
				freeBlock = getFreeSpaceBlock(du);
				
				//Adds reference to next disk block that contains file
				Utils.copyIntToBlock(vdb, blockSize-5, freeBlock);
				//Adds the block to the currentTemp
				du.write(currentTemp, vdb);
				
				//Reset vdb and currentBlockSizeCounter
				vdb = new VirtualDiskBlock(blockSize);
				currentBlockSizeCounter=0;
				
			}
			Utils.copyCharToBlock(vdb, currentBlockSizeCounter, ' ');
			currentBlockSizeCounter++;
		}
		if(currentBlockSizeCounter>=blockSize-5){
			//Store reference to the current vdb its working on and
			//obtain the next free space block
			int currentTemp = freeBlock;
			freeBlock = getFreeSpaceBlock(du);
			
			//Adds reference to next disk block that contains file
			Utils.copyIntToBlock(vdb, blockSize-5, freeBlock);
			//Adds the block to the currentTemp
			du.write(currentTemp, vdb);
			
			//Reset vdb and currentBlockSizeCounter
			vdb = new VirtualDiskBlock(blockSize);
			currentBlockSizeCounter=0;
		}
		Utils.copyIntToBlock(vdb, currentBlockSizeCounter, iNodeReference);

		/*
		 * Writes the index corresponding to the iNode
		 */
		System.out.println("Last block for register was: "+ freeBlock);
		du.write(freeBlock, vdb);
		writeINodeToDisk(0, du, rootINode);
		
		
			
		
		
		
		
		
		
		
		
	}
	private static void removeFile(DiskUnit du, int iNodeReference){
		//TODO: Find file in iNode
		INode fileINode = obtainINodeFromIndex(iNodeReference,du);
		
		/*
		 * Make blocks available
		 */
		
		//traverse blocks
		recursiveRemoval(fileINode.getFirstBlockIndex(), du);
		
		/*
		 * Make INode available
		 */
		
		//Set the reference of INode to the free INode
		fileINode.setFirstBlockIndex(du.getFirstFreeINodeIndex());
		fileINode.setFileSize(0);
		fileINode.setFileType((byte)0);
		
		du.setFirstFreeINodeIndex(iNodeReference);
		
		writeINodeToDisk(iNodeReference, du, fileINode);
	}
	
	private static void recursiveRemoval(int blockReference, DiskUnit du){
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(blockReference, vdb);
		if(Utils.getIntFromBlock(vdb, du.getBlockSize()-5)==0){
			registerFreeBlocks(du, blockReference);
			return;
		}	
		else{
			recursiveRemoval(Utils.getIntFromBlock(vdb, du.getBlockSize()-5), du);
			registerFreeBlocks(du, blockReference);
		}
	}
	
	//TODO:Documentation
	private static void registerFreeBlocks(DiskUnit du, int block){
		int referencesPerBlock = du.getBlockSize()/4;
		int fbi = du.getFreeBlockIndex();
		VirtualDiskBlock vdb;
		
		//The whole diskUnit is full so we set the firstBlockIndex to the block parameter
		if(du.getFirstBlockIndex()==0)
			du.setFirstBlockIndex(block);
			du.setFreeBlockIndex(0);
			
			//Set the current block as a reference 
			vdb = new VirtualDiskBlock(du.getBlockSize());
			Utils.copyIntToBlock(vdb, 0, 0);
			du.write(block, vdb);
			
		//The reference block is full, so we set the firstBlockIndex to the block parameter 
		if(fbi==referencesPerBlock-1){
			
			//Set the current block as a reference
			vdb = new VirtualDiskBlock(du.getBlockSize());
			Utils.copyIntToBlock(vdb, 0, du.getFirstBlockIndex());
			du.write(block, vdb);
			
			//Set the index of the references to 0
			du.setFreeBlockIndex(0);
			
			//The block is now the firstBlock
			du.setFirstBlockIndex(block);

			
		}
		
		//The root of the reference block (which is the previous reference block)
		//will be returned
		else{
			du.setFreeBlockIndex(du.getFreeBlockIndex()+1);
			//We are going to modify the reference block to add the new free block
			vdb = new VirtualDiskBlock(du.getBlockSize());
			du.read(du.getFirstBlockIndex(), vdb);
			
			//Multiplied by 4 since it's the size of int
			Utils.copyIntToBlock(vdb, du.getFreeBlockIndex()*4, block);
			
		}
	}
	private static INode obtainINodeFromIndex(int index, DiskUnit du){
			
			double INodesPerBlock = Math.floor((double)(du.getBlockSize()/9));
			double iNodeBlock = Math.max(1,Math.ceil(index/INodesPerBlock));
			double internalIndex = (index-(INodesPerBlock*iNodeBlock-INodesPerBlock))*9;
			
			String stringToReturn="";
			
			VirtualDiskBlock vdb = new VirtualDiskBlock(du.getCapacity());
			du.read((int)iNodeBlock, vdb);
			//First Data Block In File
			int firstBlockIndex=Utils.getIntFromBlock(vdb, (int)internalIndex);
			//File Size
			int fileSize=Utils.getIntFromBlock(vdb, (int)internalIndex+4);
			//File Type
			byte fileType = vdb.getElement((int)internalIndex+8);
			
			return new INode(fileType,fileSize, firstBlockIndex);
		
	}
	private static void writeINodeToDisk (int index, DiskUnit du, INode iNode){
		double INodesPerBlock = Math.floor((double)(du.getBlockSize()/9));
		double iNodeBlock = Math.max(1,Math.ceil(index/INodesPerBlock));
		double internalIndex = (index-(INodesPerBlock*iNodeBlock-INodesPerBlock))*9;
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getCapacity());
		du.read((int)iNodeBlock, vdb);
		
		
		Utils.copyIntToBlock(vdb, (int)internalIndex, iNode.getFirstBlockIndex());
		//File Size
		int fileSize=Utils.getIntFromBlock(vdb, (int)internalIndex+4);
		Utils.copyIntToBlock(vdb, (int)internalIndex+4, iNode.getFileSize());

		//File Type
		byte fileType = vdb.getElement((int)internalIndex+8);
		vdb.setElement((int)internalIndex+8, iNode.getFileType());
		
		du.write((int)iNodeBlock, vdb);
	}
	private static boolean isDirectoryEmpty(int iNodeReference, DiskUnit du){
		INode directoryINode = obtainINodeFromIndex(iNodeReference, du);
		return directoryINode.getFileSize()==0;
	}
}
