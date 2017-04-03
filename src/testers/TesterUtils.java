package testers;

import diskUtilities.DiskUnit;
import diskUtilities.Utils;
import diskUtilities.VirtualDiskBlock;
import exceptions.NonExistingDiskException;

public class TesterUtils {
	
	//TODO:Documentation
	public static void showInts(DiskUnit d){
		for(int i=0; i<d.getCapacity();i++){
			System.out.print("Block " + i + "\t");
			VirtualDiskBlock vdb = new VirtualDiskBlock(d.getCapacity());
			d.read(i, vdb);
			for(int j=0; j<d.getBlockSize(); j+=4){
				System.out.print(Utils.getIntFromBlock(vdb, j));
				System.out.print(" - ");

			}
			System.out.println("");
		}
		
	}
	//TODO:Documentation
	public static String buildStringWithIntsForGivenBlock(DiskUnit d, int blockIndex){
			String stringToReturn="";
			VirtualDiskBlock vdb = new VirtualDiskBlock(d.getCapacity());
			d.read(blockIndex, vdb);
			for(int j=0; j<d.getBlockSize(); j+=4){
				stringToReturn = stringToReturn + Utils.getIntFromBlock(vdb, j);
				stringToReturn = stringToReturn + " - ";
			}
			return stringToReturn;
		}
		
}
