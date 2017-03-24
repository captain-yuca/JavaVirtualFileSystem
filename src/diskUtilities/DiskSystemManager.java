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
	 * Names of
	 */
	private ArrayList<String> diskUnitNames;
	private static final String SYSTEMFILENAME = "diskIdentifiers.cfo";

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
		DiskUnit.createDiskUnit(name, capacity, blockSize);
		//Adds the name for the created RAF to the arrayList of diskUnitNames
		this.diskUnitNames.add(name);
		writeNameToSystemFile(name);



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
			FileWriter fileWriter =new FileWriter(SYSTEMFILENAME);

			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter =new BufferedWriter(fileWriter);

			// Note that write() does not automatically
			// append a newline character.
			bufferedWriter.write(name);
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
}
