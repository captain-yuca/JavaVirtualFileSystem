package diskUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.NonExistingDiskException;

public class DiskSystemManager {
	/**
	 * Names of the DiskUnits available
	 */
	private ArrayList<String> diskUnitNames;
	
	/**
	 * The name of the file containing the disk identifiers
	 */
	private static final String SYSTEMFILENAME = "diskIdentifiers.cfo";
	
	/**
	 * Current mounted disk
	 */
	private DiskUnit mountedDiskUnit = null;
	public String mountedDiskName=null;

	public DiskSystemManager(){
		this.diskUnitNames=new ArrayList<String>();
		this.readNamesFromSystemFile();
	}
	
	/**
	 * Creates a new disk unit with the given name. The disk is formatted
	 * as with the specified capacity (number of blocks), each of specified
	 * size (number of bytes).  The created disk is left as in off mode.
	 * Then, adds the name of said DiskUnit to the System File
	 * @param name Name of the disk you want to create
	 * @param capacity Capacity of the blocks you wish to have
	 * @param blockSize Size of each VirtualDiskBlock
	 */
	public void createDisk(String name, int capacity, int blockSize){
		FileSystemManager.createFormattedDiskForUse(name, capacity, blockSize);
		//Adds the name for the created RAF to the arrayList of diskUnitNames
		this.diskUnitNames.add(name);
		writeNameToSystemFile(name);

	}
	
	/**
	 * Loads a file onto the mounted DiskUnit
	 * @param fileToLoad name of the file to load
	 * @param newFile The name of the new file to be created
	 */
	public void loadFile(String fileToLoad, String newFile){
		try {
			FileSystemManager.writeFile(fileToLoad, newFile, this.mountedDiskUnit);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Verifies if the diskName exists in the System File
	 * @param diskName The name of the RandomAccessFile you which to verify if it exists
	 * @return true if the diskName exists in the System File
	 */
	public boolean diskExists(String diskName){
		return diskUnitNames.contains(diskName);
	}

	/**
	 * Reads the names written on the System File that states the names of the current DiskUnits available to mount 
	 */
	private void readNamesFromSystemFile(){
		File systemFile = new File(SYSTEMFILENAME);
		// This will reference one line at a time
		String line = null;
		
		//If the SystemFile exists, start reading the names
		if (systemFile.exists()){
			try {
				FileReader fileReader = new FileReader(SYSTEMFILENAME);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
					this.diskUnitNames.add(line);
				}				
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException ex) {
				System.out.println(
						"Error reading file '" 
								+ SYSTEMFILENAME + "'");                  
			}
		}



	}

	/**
	 * Writes the name of the corresponding RandomAccessFile of the DiskUnit to the System File denoted in the constant SYSTEMFILENAME
	 * @param name name of the corresponding RandomAccessFile of the DiskUnit
	 */
	private void writeNameToSystemFile(String name){
		try {
			// Assume default encoding.
			FileWriter fileWriter =new FileWriter(SYSTEMFILENAME, true);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			bufferedWriter.append(name);
			bufferedWriter.newLine();
			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"
							+ SYSTEMFILENAME + "'");

		}
	}
	
	/**
	 * Deletes said Disk from the system
	 * @param diskName Name of the RAF to be deleted
	 */
	public void deleteDisk(String diskName){
		//This method assumes that the disk to delete does exist
		File fileToDelete = new File(diskName);
		fileToDelete.delete();
		System.out.println(this.diskUnitNames.remove(diskName));
		try {
			// Assume default encoding.
			FileWriter fileWriter =new FileWriter(SYSTEMFILENAME);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			for(String name : this.diskUnitNames){
				bufferedWriter.append(name);
				bufferedWriter.newLine();
			}
			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println(
					"Error writing to file '"
							+ SYSTEMFILENAME + "'");

		}
		
		
	}
	
	/**
	 * Mount the DiskUnit of given name
	 * @param name Name of disk
	 */
	public void mountDisk(String name){
		try {
			this.mountedDiskUnit=DiskUnit.mount(name);
			this.mountedDiskName=name;
		} catch (NonExistingDiskException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Unmounts the currently mounted disk
	 */
	public void unmountDisk(){
		this.mountedDiskUnit.shutdown();
		this.mountedDiskUnit=null;
	}
	
	/**
	 * Returns the available DiskUnits in the system
	 * @return how many disks are there in the system
	 */
	public int getNumberOfDisks(){
		return this.diskUnitNames.size();
	}
	
	/**
	 * Given an index, fetches the name of the position given
	 * @param index index in the list
	 * @return the name corresponding to the list
	 */
	public String getName(int index){
		return this.diskUnitNames.get(index);
	}
	
	/**
	 * Returns if there is a disk mounted in the system
	 * @return true if there is a disk mounted
	 */
	public boolean diskIsMounted(){
		return (!(this.mountedDiskUnit==null));
	}
	
	/**
	 * Displays the internal file given by the name fileToDisplay in the mountedDisk
	 * @param fileToDisplay name of the internal file to dispkay
	 */
	public void displayFile(String fileToDisplay) {
		try {
			FileSystemManager.displayFile(fileToDisplay, this.mountedDiskUnit);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows the whole root of the file system
	 */
	public void showDir(){
		FileSystemManager.showDir(this.mountedDiskUnit);
	}
	
	/**
	 * Copies an internal file to another internal file. If the second internal file does
	 * not exist, it creates a new one
	 * @param file1 file to copy
	 * @param file2 file that will be overwritten
	 */
	public void copyFile(String file1, String file2){
		try {
			FileSystemManager.copyFile(file1, file2, this.mountedDiskUnit);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
