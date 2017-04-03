package diskUtilities;

import exceptions.NonExistingDiskException;

public class FileSystemManager {
	final static int INODESIZE = 9;


	public static void createFormattedDiskForUse(String name, int capacity, int blockSize){
		//verify how much space is needed to reserve
		int spaceAvailable = (int) (blockSize * capacity*0.01);
		int totalINodes= spaceAvailable/INODESIZE;
		System.out.println(totalINodes);
		int iNodesPerBlock = blockSize/INODESIZE;
		
		int firstFreeBlock = (INODESIZE*totalINodes)/blockSize;
		int nextFreeBlockIndex = firstFreeBlock+1;
		DiskUnit.createDiskUnit(name, capacity, blockSize, firstFreeBlock, nextFreeBlockIndex, 2, totalINodes);
		DiskUnit du=null;
		try {
			du = DiskUnit.mount(name);
		} catch (NonExistingDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		//TODO: Create root directory INode
		INode rootINode = new INode((byte) 0, 0, 0);
		VirtualDiskBlock rootVDB = new VirtualDiskBlock();
		Utils.copyIntToBlock(rootVDB, 0, rootINode.getFirstBlockIndex());
		Utils.copyIntToBlock(rootVDB, 4, rootINode.getFileSize());
		rootVDB.setElement(8, rootINode.getFileType());
		du.write(1, rootVDB);

		
		
		
		//TODO: Create remaining INodes
		
	}
	public static void writeFile(){
		
	}
}
