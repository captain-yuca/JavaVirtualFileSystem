package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import exceptions.FullDiskUnitException;
import exceptions.InvalidValueException;
import exceptions.NonExistingDiskException;
import testers.TesterUtils;

//TODO: Clean Up Code
/**
 * @author Manuel A. Baez Gonzalez
 *
 */
public class FileSystemManager {
	/**
	 * The size of an INode
	 */
	final static int INODESIZE = 9;
	// String is the filename. Integer is the corresponding INode

	
	/**
	 * Generates a DiskUnit with the necessary format to utilize with the
	 * FileSystemManager
	 * @param name name of the RAF
	 * @param capacity number of blocks the disk will have
	 * @param blockSize size of the Virtual Disk Blocks
	 */
	public static void createFormattedDiskForUse(String name, int capacity, int blockSize) {
		// verify how much space is needed to reserve
		double spaceAvailable = Math.floor(blockSize * capacity * 0.01);

		double firstFreeBlock = 1 + Math.ceil((spaceAvailable) / blockSize) + 1;

		double totalINodes = spaceAvailable / INODESIZE;

		double iNodesPerBlock = Math.ceil((double) (blockSize / INODESIZE));

		int nextFreeBlockIndex = (int) firstFreeBlock + 1;
		DiskUnit.createDiskUnit(name, capacity, blockSize, (int) firstFreeBlock, (int) nextFreeBlockIndex, 1,
				(int) totalINodes);
		DiskUnit du = null;
		try {
			du = DiskUnit.mount(name);
		} catch (NonExistingDiskException e) {
			e.printStackTrace();
		}

		 if(du.getFirstBlockIndex()==0)
		 	throw new InvalidValueException("The First Block Index is the root");
		 
		//Format the inodes, freeblocks and configure the root inode
		formatFreeINodes(du, (int) iNodesPerBlock);
		formatFreeBlocks(du);
		configureRootINode(du);
	}

	/**
	 * Makes the Free Blocks available for the FileSystemManager
	 * @param du The DiskUnit you will format
	 */
	private static void formatFreeBlocks(DiskUnit du) {
		// References of disks you can write per diskblock
		int referencesPerBlock = du.getBlockSize() / 4;

		// Counter to mark on which diskblock you are referencing
		int freeBlockCounter = du.getFirstBlockIndex();

		int currentVDB = 0;

		// While the counter doesn't exceed the index of the blocks
		// (So it doesn't reference a non-existing free block), keep writing
		// references
		while (freeBlockCounter < du.getCapacity()) {

			VirtualDiskBlock vdbToAdd = new VirtualDiskBlock();

			// Writes the reference to the previous block that has references
			Utils.copyIntToBlock(vdbToAdd, 0, currentVDB);

			// Sets the current virtualDiskBlock to the one that is going to
			// have
			// the new references
			currentVDB = freeBlockCounter;

			// Start the counter to the next available free block instead of the
			// one
			// you will write on.
			freeBlockCounter++;

			// Add references for Free DiskBlocks to vdbToAdd
			for (int i = 1; i < referencesPerBlock; i++) {
				Utils.copyIntToBlock(vdbToAdd, i * 4, freeBlockCounter);
				freeBlockCounter++;
				if (freeBlockCounter >= du.getCapacity())
					break;
			}
			// Writes the block with references to the disk unit
			du.write(currentVDB, vdbToAdd);
		}
		// Register the last reference block

		du.setFirstBlockIndex(currentVDB);
		du.setFreeBlockIndex(freeBlockCounter - 1);
	}

	/**
	 * Makes the INodes available for use with the FileSystemManager
	 * @param du The DiskUnit you will format
	 * @param iNodesPerBlock How many blocks will be used for the INodes
	 */
	private static void formatFreeINodes(DiskUnit du, int iNodesPerBlock) {
		// Total INodes that will be placed
		int totalINodes = du.getTotalINodes();
		int blockSize = du.getBlockSize();

		// Number of Blocks reserved for INodes
		int totalBlocksForINodes = totalINodes / iNodesPerBlock;
		// Counter to track which INode to set as the next free Inode
		int iNodeCounter = 1;
		VirtualDiskBlock vdbToAdd;

		// TODO: Find better algorithm
		for (int i = 1; i < totalBlocksForINodes + 1; i++) {
			vdbToAdd = new VirtualDiskBlock(blockSize);
			for (int j = 0; j < blockSize - 9; j = j + 9) {
				Utils.copyIntToBlock(vdbToAdd, j, iNodeCounter);
				Utils.copyIntToBlock(vdbToAdd, j + 4, 0);
				vdbToAdd.setElement(j + 8, (byte) 0);
				iNodeCounter++;
				if (iNodeCounter >= totalINodes) {
					Utils.copyIntToBlock(vdbToAdd, j, iNodeCounter);
					break;
				}

			}
			du.write(i, vdbToAdd);

		}
		du.setFirstFreeINodeIndex(1);

	}

	/**
	 * Writes a file into the DiskUnit with the contents of an external file
	 * @param fileToRead The name of the external file you wish to copy
	 * @param fileToCreate The name of the file you will create in the Disk Unit
	 * @param du The DiskUnit you will write on
	 * @throws FileNotFoundException when the fileToRead does not match with an external file
	 */
	public static void writeFile(String fileToRead, String fileToCreate, DiskUnit du) throws FileNotFoundException {
		File fileToCopy = new File(fileToRead);
		if (!fileToCopy.exists())
			throw new FileNotFoundException("The file :" + fileToRead + " does not exist");
			
			String stringToCopy = getStringFromExternalFile(fileToRead);
			writeStringToDisk(du, stringToCopy, fileToCreate);
		
	}
	
	//TODO: Documentation
	private static String getStringFromExternalFile(String fileToRead){
		// This will reference one line at a time
		String line = null;
		String stringToCopy="";
		try {
			//bufferedReader to read each line and append it to stringToCopy
			FileReader fileReader = new FileReader(fileToRead);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				stringToCopy = stringToCopy + line + "\n";
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileToRead + "'");
		}
		return stringToCopy;
	}
	
	//TODO: Documentation
	private static String getStringFromInternalFile(DiskUnit du, String fileName) throws FileNotFoundException{
		
		// Add spaces to complete to length 20
		int lengthDifference = 20 - fileName.length();
		for (int i = 0; i < lengthDifference; i++) {
			fileName = fileName + " ";
		}

		int fileINodeIndex = getFileINode(du, fileName);
		if (fileINodeIndex == -1) {
			throw new FileNotFoundException("File: " + fileName + " was not found in the Disk Unit");

		}
		INode fileINode = obtainINodeFromIndex(fileINodeIndex, du);
		
		String stringToReturn="";
		int eof = fileINode.getFileSize();
		int currentBlock = fileINode.getFirstBlockIndex();
		int currentBlockIndex = 0;
		int blockSize = du.getBlockSize();

		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(currentBlock, vdb);

		for (int i = 0; i < eof; i++) {
			if (currentBlockIndex >= blockSize - 5) {
				currentBlock = Utils.getIntFromBlock(vdb, currentBlockIndex);
				du.read(currentBlock, vdb);
				currentBlockIndex = 0;
			}
			stringToReturn = stringToReturn + Utils.getCharFromBlock(vdb, currentBlockIndex);
			currentBlockIndex++;
		}
		
		return stringToReturn;
	}
	
	//TODO: Documentation
	private static void writeStringToDisk(DiskUnit du, String stringToCopy, String fileToCreate){
		
		// Obtain free INode
		int freeINode = getFreeINode(du);
		int freeSpaceBlock = getFreeSpaceBlock(du);
		
		/*
		 * 
		 * Register INode
		 * 
		 */
		
		
		VirtualDiskBlock inodeVDB = new VirtualDiskBlock(du.getBlockSize());
		INode iNodeToAdd = new INode((byte) 1, stringToCopy.length(), freeSpaceBlock);
		writeINodeToDisk(freeINode, du, iNodeToAdd);
		// Register it in the root
		registerFileInDiskUnitRoot(du, fileToCreate, freeINode);

		/*
		 * 
		 * Copy the string to the file
		 * 
		 */
		
		
		//Counter to determine if it's going to exceed into another FreeBlock
		int currentBlockSizeCounter = 0;
		int blockSize = du.getBlockSize();
		VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);
		
		//Copying the string...
		for (int i = 0; i < stringToCopy.length(); i++) {
			if (currentBlockSizeCounter >= blockSize - 5) {

				// Store reference to the current vdb its working on and
				// obtain the next free space block
				int currentTemp = freeSpaceBlock;
				freeSpaceBlock = getFreeSpaceBlock(du);

				// Adds reference to next disk block that contains file
				Utils.copyIntToBlock(vdb, blockSize - 5, freeSpaceBlock);
				// Adds the block to the currentTemp
				du.write(currentTemp, vdb);

				// Reset vdb and currentBlockSizeCounter
				vdb = new VirtualDiskBlock(blockSize);
				currentBlockSizeCounter = 0;

			}
			Utils.copyCharToBlock(vdb, currentBlockSizeCounter, stringToCopy.charAt(i));
			currentBlockSizeCounter++;
		}
		// Write the reference 
		Utils.copyIntToBlock(vdb, blockSize - 5, 0);
		du.write(freeSpaceBlock, vdb);
	}
	

	/**
	 * Returns the index of a block that has free space
	 * @param du The DiskUnit that will get the freeSpaceBlock
	 * @return the index corresponding to the freeSpaceBlock
	 */
	private static int getFreeSpaceBlock(DiskUnit du) {
		int freeBlockIndex;
		int fbi = du.getFreeBlockIndex();
		if (du.getFirstBlockIndex() == 0)
			throw new FullDiskUnitException("Disk Unit does not have free blocks available.");
		

		// If the disk has space
		if (fbi != 0) {
			freeBlockIndex = fbi;
			du.setFreeBlockIndex(fbi - 1);
		}

		// The root of the reference block (which is the previous reference
		// block)
		// will be returned
		else {
			freeBlockIndex = du.getFirstBlockIndex();
			du.setFirstBlockIndex(fbi);
			du.setFreeBlockIndex(du.getBlockSize() / 4 - 1);
		}
		return freeBlockIndex;
	}

	/**
	 *  Returns an array with the block in which the INode resides ([0]), the index where the
	 *  INode Starts([1]) and the index that corresponds to this INode([2])
	 * @param du The DiskUnit to get the free INode
	 * @return an array of ints that contains the values specified in the method
	 */
	private static int getFreeINode(DiskUnit du) {
		int freeINodeIndex = du.getFirstFreeINodeIndex();
		if (du.getFirstFreeINodeIndex() == 0)
			throw new FullDiskUnitException("Disk Unit does not have available INodes");

		// Calculate the block in which it resides
		double INodesPerBlock = Math.floor((double) (du.getBlockSize() / INODESIZE));
		double freeINodeBlock = Math.max(1, Math.ceil(freeINodeIndex / INodesPerBlock));
		double internalIndex = (freeINodeIndex - (INodesPerBlock * freeINodeBlock - INodesPerBlock)) * INODESIZE;

		// TODO:Modify names

		/*
		 * Set next INode
		 */

		// Access current free INode
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read((int) freeINodeBlock, vdb);

		// Read the next free INode index from current one
		// and set it as the next free INode

		du.setFirstFreeINodeIndex(Utils.getIntFromBlock(vdb, (int) internalIndex));

		
		return freeINodeIndex;
	}

	/**
	 * Configures the initial INode in the DiskUnit to function as the root directory in the FileSystemManager
	 * @param du The DiskUnit that will have the root INode
	 */
	private static void configureRootINode(DiskUnit du) {
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(1, vdb);
		Utils.copyIntToBlock(vdb, 0, 0);
		getFreeSpaceBlock(du);
		du.write(1, vdb);

	}

	/**
	 * Registers a file in the according directory INode
	 * @param du The DiskUnit that will be utilized
	 * @param fileName the name of said file to be registered
	 * @param iNodeReference reference to the root INode
	 */
	private static void registerFileInDiskUnitRoot(DiskUnit du, String fileName, int iNodeReference) {
		INode fileINode = obtainINodeFromIndex(iNodeReference, du);
		int freeBlock = -1;
		int currentBlockSizeCounter = 0;
		
		//Since there is only one directory (root), ignore
		//iNodeReference param
		INode rootINode = obtainINodeFromIndex(0, du);

		int blockSize = du.getBlockSize();
		VirtualDiskBlock vdb = new VirtualDiskBlock(blockSize);

		// We need to append the new name to the file
		if (!isDirectoryEmpty(0, du)) {

			/*
			 * 
			 *  Append
			 *
			 */

			// Navigate to File

			int blocksToTraverse = rootINode.getFileSize() / du.getBlockSize();

			int eofIndexPosition = rootINode.getFileSize() - blocksToTraverse * du.getBlockSize() - 1;
			int eofBlock = rootINode.getFirstBlockIndex();
			VirtualDiskBlock vdbToRead = new VirtualDiskBlock(du.getBlockSize());

			// Find last part of the file
			for (int i = 0; i < blocksToTraverse; i++) {
				du.read(eofBlock, vdbToRead);
				eofBlock = Utils.getIntFromBlock(vdbToRead, du.getBlockSize() - 4);
			}
			rootINode.setFileSize(rootINode.getFileSize() + 24);

			freeBlock = eofBlock;
			currentBlockSizeCounter = eofIndexPosition + 1;
			du.read(eofBlock, vdb);

		}
		// There is nothing on the directory file
		else {
			freeBlock = getFreeSpaceBlock(du);
			currentBlockSizeCounter = 0;
			rootINode.setFileSize(24);
			rootINode.setFirstBlockIndex(freeBlock);

		}
		// This methods assumes that the String given is 20 or less characters
		// To maintain uniformity, we will add spaces to the file where the
		// names are held
		int remainingLength = 20 - fileName.length();
		/*
		 * Add the size of the directory file
		 */

		/*
		 * Writes the name
		 */
		for (int i = 0; i < fileName.length(); i++) {
			if (currentBlockSizeCounter >= blockSize - 4) {

				// Store reference to the current vdb its working on and
				// obtain the next free space block
				int currentTemp = freeBlock;
				freeBlock = getFreeSpaceBlock(du);

				// Adds reference to next disk block that contains file
				Utils.copyIntToBlock(vdb, blockSize - 4, freeBlock);
				// Adds the block to the currentTemp
				du.write(currentTemp, vdb);

				// Reset vdb and currentBlockSizeCounter
				vdb = new VirtualDiskBlock(blockSize);
				currentBlockSizeCounter = 0;

			}
			Utils.copyCharToBlock(vdb, currentBlockSizeCounter, fileName.charAt(i));
			currentBlockSizeCounter++;
		}

		/*
		 * Writes the additional spaces
		 */
		for (int i = 0; i < remainingLength; i++) {
			if (currentBlockSizeCounter >= blockSize - 4) {

				// Store reference to the current vdb its working on and
				// obtain the next free space block
				int currentTemp = freeBlock;
				freeBlock = getFreeSpaceBlock(du);

				// Adds reference to next disk block that contains file
				Utils.copyIntToBlock(vdb, blockSize - 5, freeBlock);
				// Adds the block to the currentTemp
				du.write(currentTemp, vdb);

				// Reset vdb and currentBlockSizeCounter
				vdb = new VirtualDiskBlock(blockSize);
				currentBlockSizeCounter = 0;

			}
			Utils.copyCharToBlock(vdb, currentBlockSizeCounter, ' ');
			currentBlockSizeCounter++;
		}

		if (currentBlockSizeCounter >= blockSize - 4) {
			// Store reference to the current vdb its working on and
			// obtain the next free space block
			int currentTemp = freeBlock;
			freeBlock = getFreeSpaceBlock(du);

			// Adds reference to next disk block that contains file
			Utils.copyIntToBlock(vdb, blockSize - 4, freeBlock);
			// Adds the block to the currentTemp
			du.write(currentTemp, vdb);

			// Reset vdb and currentBlockSizeCounter
			vdb = new VirtualDiskBlock(blockSize);
			currentBlockSizeCounter = 0;
		}
		Utils.copyIntToBlock(vdb, currentBlockSizeCounter, iNodeReference);

		/*
		 * Writes the index corresponding to the iNode
		 */
		du.write(freeBlock, vdb);
		writeINodeToDisk(0, du, rootINode);

	}

	/**
	 * Remove File method(NOT IMPLEMENTED)
	 * @param du
	 * @param iNodeReference
	 */
	private static void removeFile(DiskUnit du, int iNodeReference) {
		// TODO: Find file in iNode
		INode fileINode = obtainINodeFromIndex(iNodeReference, du);

		/*
		 * Make blocks available
		 */

		// traverse blocks
		recursiveRemoval(fileINode.getFirstBlockIndex(), du);

		/*
		 * Make INode available
		 */

		// Set the reference of INode to the free INode
		fileINode.setFirstBlockIndex(du.getFirstFreeINodeIndex());
		fileINode.setFileSize(0);
		fileINode.setFileType((byte) 0);

		du.setFirstFreeINodeIndex(iNodeReference);

		writeINodeToDisk(iNodeReference, du, fileINode);
	}

	/**
	 * Recursive Removal method(NOT IMPLEMENTED)
	 * @param blockReference
	 * @param du
	 */
	private static void recursiveRemoval(int blockReference, DiskUnit du) {
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(blockReference, vdb);
		if (Utils.getIntFromBlock(vdb, du.getBlockSize() - 5) == 0) {
			registerFreeBlocks(du, blockReference);
			return;
		} else {
			recursiveRemoval(Utils.getIntFromBlock(vdb, du.getBlockSize() - 5), du);
			registerFreeBlocks(du, blockReference);
		}
	}

	/**
	 * Register Free Blocks method(NOT IMPLEMENTED)
	 * @param du
	 * @param block
	 */
	private static void registerFreeBlocks(DiskUnit du, int block) {
		int referencesPerBlock = du.getBlockSize() / 4;
		int fbi = du.getFreeBlockIndex();
		VirtualDiskBlock vdb;

		// The whole diskUnit is full so we set the firstBlockIndex to the block
		// parameter
		if (du.getFirstBlockIndex() == 0)
			du.setFirstBlockIndex(block);
		du.setFreeBlockIndex(0);

		// Set the current block as a reference
		vdb = new VirtualDiskBlock(du.getBlockSize());
		Utils.copyIntToBlock(vdb, 0, 0);
		du.write(block, vdb);

		// The reference block is full, so we set the firstBlockIndex to the
		// block parameter
		if (fbi == referencesPerBlock - 1) {

			// Set the current block as a reference
			vdb = new VirtualDiskBlock(du.getBlockSize());
			Utils.copyIntToBlock(vdb, 0, du.getFirstBlockIndex());
			du.write(block, vdb);

			// Set the index of the references to 0
			du.setFreeBlockIndex(0);

			// The block is now the firstBlock
			du.setFirstBlockIndex(block);

		}

		// The root of the reference block (which is the previous reference
		// block)
		// will be returned
		else {
			du.setFreeBlockIndex(du.getFreeBlockIndex() + 1);
			// We are going to modify the reference block to add the new free
			// block
			vdb = new VirtualDiskBlock(du.getBlockSize());
			du.read(du.getFirstBlockIndex(), vdb);

			// Multiplied by 4 since it's the size of int
			Utils.copyIntToBlock(vdb, du.getFreeBlockIndex() * 4, block);

		}
	}

	/**
	 * Obtains the INode object according to the index given relative to the INodes.
	 * The index of INodes go from 0 to totalINodes-1
	 * @param index index that belongs to the INodes
	 * @param du DiskUnit from which to obtain the INode
	 * @return the corresponding INode object
	 */
	private static INode obtainINodeFromIndex(int index, DiskUnit du) {

		double INodesPerBlock = Math.floor((double) (du.getBlockSize() / 9));
		double iNodeBlock = Math.max(1, Math.ceil(index / INodesPerBlock));
		double internalIndex = (index - (INodesPerBlock * iNodeBlock - INodesPerBlock)) * 9;

		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getCapacity());
		du.read((int) iNodeBlock, vdb);
		// First Data Block In File
		int firstBlockIndex = Utils.getIntFromBlock(vdb, (int) internalIndex);
		// File Size
		int fileSize = Utils.getIntFromBlock(vdb, (int) internalIndex + 4);
		// File Type
		byte fileType = vdb.getElement((int) internalIndex + 8);

		return new INode(fileType, fileSize, firstBlockIndex);

	}

	/**
	 * Writes an INode to the disk given the index of the INode and the object
	 * The index of INodes go from 0 to totalINodes-1
	 * @param index Index of the INode in the collection of INodes
	 * @param du DiskUnit to write the INode
	 * @param iNode The INode object to write onto the disk
	 */
	private static void writeINodeToDisk(int index, DiskUnit du, INode iNode) {
		double INodesPerBlock = Math.floor((double) (du.getBlockSize() / 9));
		double iNodeBlock = Math.max(1, Math.ceil(index / INodesPerBlock));
		double internalIndex = (index - (INodesPerBlock * iNodeBlock - INodesPerBlock)) * 9;
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getCapacity());
		du.read((int) iNodeBlock, vdb);

		Utils.copyIntToBlock(vdb, (int) internalIndex, iNode.getFirstBlockIndex());
		// File Size
		int fileSize = Utils.getIntFromBlock(vdb, (int) internalIndex + 4);
		Utils.copyIntToBlock(vdb, (int) internalIndex + 4, iNode.getFileSize());

		// File Type
		byte fileType = vdb.getElement((int) internalIndex + 8);
		vdb.setElement((int) internalIndex + 8, iNode.getFileType());

		du.write((int) iNodeBlock, vdb);
	}

	/**
	 * Verifies if the directory INode is empty
	 * The index of INodes go from 0 to totalINodes-1
	 * @param iNodeReference The index relative to all the INodes
	 * @param du The DiskUnit the INode belongs to
	 * @return
	 */
	private static boolean isDirectoryEmpty(int iNodeReference, DiskUnit du) {
		INode directoryINode = obtainINodeFromIndex(iNodeReference, du);
		return directoryINode.getFileSize() == 0;
	}

	
	/**
	 * Displays the file of name fileName in the System.out stream
	 * @param fileName The name of the desired file
	 * @param du The DiskUnit where the file is housed
	 * @throws FileNotFoundException 
	 */
	public static void displayFile(String fileName, DiskUnit du) throws FileNotFoundException {	
		
		//Initialize stringToDisplay
		String stringToDisplay = getStringFromInternalFile(du, fileName);

		System.out.print(stringToDisplay + "\n\n");

	}

	/**
	 * Gets the appropriate INode for the given fileName
	 * @param du DiskUnit to find the INode
	 * @param fileName Name of said file
	 * @return the index of the FileINode
	 */
	private static int getFileINode(DiskUnit du, String fileName) {
		int currentBlockSizeCounter = 0;
		INode rootINode = obtainINodeFromIndex(0, du);
		int sizeOfDirectoryFile = rootINode.getFileSize();
		int fileBlock = rootINode.getFirstBlockIndex();
		// FileName variable
		String currentFileName = "";

		// Access the file block
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(fileBlock, vdb);

		// TODO: Case for more that the alloted 1 block

		int fileINodeIndex = -1;

		// Start Reading
		
		for (int i = 0; i < sizeOfDirectoryFile; i++) {
			// If it finished the string, add the number
			if (currentBlockSizeCounter == 20) {
				if (currentFileName.equals(fileName)) {
					fileINodeIndex = Utils.getIntFromBlock(vdb, i);
				}
				currentFileName = "";
				currentBlockSizeCounter = 0;
				i = i + 3;
				continue;
			}
			currentFileName = currentFileName + Utils.getCharFromBlock(vdb, i);
			currentBlockSizeCounter++;

		}

		return fileINodeIndex;
	}

	/**
	 * Shows the root directory of the current DiskUnit through the System.out stream
	 * @param du DiskUnit which to show the root directory
	 */
	public static void showDir(DiskUnit du) {
		//Initialize String to be outputted
		String str = "";
		int currentBlockSizeCounter = 0;
		INode rootINode = obtainINodeFromIndex(0, du);
		int sizeOfDirectoryFile = rootINode.getFileSize();
		int fileBlock = rootINode.getFirstBlockIndex();
		
		
		str="--------------------\t--------\n"+ 
			"|    File Name     |\t| Size |\n"+
			"--------------------\t--------\n";
		
		
		//Read the VirtualDiskBlock which contains the references
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getBlockSize());
		du.read(fileBlock, vdb);

		for (int i = 0; i < sizeOfDirectoryFile; i++) {
			// If it finished the string, add the number
			if (currentBlockSizeCounter == 20) {
				str = str + "\t  ";
				str = str + obtainINodeFromIndex(Utils.getIntFromBlock(vdb, i), du).getFileSize() + "\n";
				i = i + 3;
				currentBlockSizeCounter = 0;
				continue;
			}
			str = str + Utils.getCharFromBlock(vdb, i);
			currentBlockSizeCounter++;

		}
		str=str+"\n";
		System.out.print(str);
		
	}
	
	//TODO:Finish implementation
	/**
	 * Copies file1 to file2. If file2 exists, it gets overwritten
	 * @param file1 File to copy
	 * @param file2 Name of the new file the DiskUnit will have
	 * @param du DiskUnit to copy the file to
	 * @throws FileNotFoundException 
	 */
	public static void copyFile(String file1, String file2, DiskUnit du) throws FileNotFoundException {
		int lengthDifference = 20 - file1.length();
		for (int i = 0; i < lengthDifference; i++) {
			file1 = file1 + " ";
		}
		lengthDifference = 20 - file2.length();
		for (int i = 0; i < lengthDifference; i++) {
			file2 = file2 + " ";
		}
		int fini1 = getFileINode(du, file1);
		if (fini1 == -1)
			return;
		int fini2 = getFileINode(du, file2);
		if (fini2 == -1) {

			String stringToDisplay = getStringFromInternalFile(du, file1);
			writeStringToDisk(du,stringToDisplay, file2);

		} else {

		}
	}

}
