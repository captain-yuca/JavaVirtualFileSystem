package diskUtilities;

public class FileSystemManager {
	final static int INODESIZE = 9;

	public static void reserveSpaceForINodes(DiskUnit du){
		//verify how much space is needed to reserve
		
		int spaceAvailable = (int) (du.getBlockSize() * du.getCapacity()*0.01);
		int totalINodes= spaceAvailable/INODESIZE;
		System.out.println(totalINodes);
		int iNodesPerBlock = du.getBlockSize()/INODESIZE;
		int finalPosition = (9*totalINodes)/du.getBlockSize();
	}
}
