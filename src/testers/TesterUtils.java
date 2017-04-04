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
	public static void showChars(DiskUnit d){
		for(int i=0; i<d.getCapacity();i++){
			System.out.print("Block " + i + "\t");
			VirtualDiskBlock vdb = new VirtualDiskBlock(d.getCapacity());
			d.read(i, vdb);
			for(int j=0; j<d.getBlockSize(); j++){
				if(j>= d.getBlockSize()-5){
					System.out.print(Utils.getIntFromBlock(vdb, d.getBlockSize()-5));
					
					break;
				}
				System.out.print(Utils.getCharFromBlock(vdb, j));
				System.out.print(" - ");

			}
			System.out.println("");
		}
		
	}
	
	//TODO:Documentation
		public static void showCharsWithoutDash(DiskUnit d){
			for(int i=0; i<d.getCapacity();i++){
				System.out.print("Block " + i + "\t");
				VirtualDiskBlock vdb = new VirtualDiskBlock(d.getCapacity());
				d.read(i, vdb);
				for(int j=0; j<d.getBlockSize(); j++){
					if(j>= d.getBlockSize()-5){
						System.out.print(Utils.getIntFromBlock(vdb, d.getBlockSize()-5));
						
						break;
					}
					System.out.print(Utils.getCharFromBlock(vdb, j));

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
	
	//TODO: Documentation
	public static String buildStringWithCharForGivenBlock(DiskUnit d, int blockIndex){
		String stringToReturn="";
		VirtualDiskBlock vdb = new VirtualDiskBlock(d.getCapacity());
		d.read(blockIndex, vdb);
		for(int j=0; j<d.getBlockSize(); j++){
			if(j>= d.getBlockSize()-5){
				stringToReturn = stringToReturn + Utils.getIntFromBlock(vdb, d.getBlockSize()-5);
				break;
			}
			stringToReturn = stringToReturn + Utils.getCharFromBlock(vdb, j);
		}
		return stringToReturn;
	}
	//Assumes that INode size is 9
	public static String buildStringWithINodeParams(int iNodeIndex, DiskUnit du){
		
		double INodesPerBlock = Math.floor((double)(du.getBlockSize()/9));
		double iNodeBlock = Math.ceil(iNodeIndex/INodesPerBlock);
		double internalIndex = (iNodeIndex-(INodesPerBlock*iNodeBlock-INodesPerBlock))*9;
		
		String stringToReturn="";
		
		VirtualDiskBlock vdb = new VirtualDiskBlock(du.getCapacity());
		du.read((int)iNodeBlock, vdb);
		//First Data Block In File
		stringToReturn+=Utils.getIntFromBlock(vdb, (int)internalIndex) + "-";
		//File Size
		stringToReturn+=Utils.getIntFromBlock(vdb, (int)internalIndex+4) + "-";
		//File Type
		stringToReturn+=vdb.getElement((int)internalIndex+8);
		
		return stringToReturn;
	}
		
}
