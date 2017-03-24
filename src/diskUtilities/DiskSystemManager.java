package diskUtilities;

import java.util.ArrayList;

import exceptions.NonExistingDiskException;

public class DiskSystemManager {
	private ArrayList<DiskUnit> diskUnits;
	
	public DiskSystemManager(){
		diskUnits=new ArrayList<DiskUnit>();
	}
	
	public void createDisk(String name, int capacity, int blockSize){
		DiskUnit.createDiskUnit(name, capacity, blockSize);
		DiskUnit diskToAddToList=null;
		try {
			diskToAddToList = DiskUnit.mount(name);
			System.out.println(diskToAddToList);
			diskUnits.add(diskToAddToList);
		} catch (NonExistingDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO: Add Disk Name to the txt file and add it to the arrayList.
		
	}
	public boolean diskExists(String name){
		//TODO: Create Method.
		return false;
	}
}
