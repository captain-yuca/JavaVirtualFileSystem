package theSystem;

import java.util.ArrayList;

import operandHandlers.OperandValidatorUtils;
import lists.DLDHDTList;
import lists.LLIndexList1;
import listsManagementClasses.ListsManager;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;
import systemGeneralClasses.VariableLengthCommand;
import stack.IntStack;
import diskUtilities.*;
import exceptions.NonExistingDiskException;


/**
 * 
 * @author Pedro I. Rivera-Vega
 *
 */
public class SystemCommandsProcessor extends CommandProcessor { 
	
	
	//NOTE: The HelpProcessor is inherited...

	// To initially place all lines for the output produced after a 
	// command is entered. The results depend on the particular command. 
	private ArrayList<String> resultsList; 
	
	SystemCommand attemptedSC; 
	// The system command that looks like the one the user is
	// trying to execute. 

	boolean stopExecution; 
	// This field is false whenever the system is in execution
	// Is set to true when in the "administrator" state the command
	// "shutdown" is given to the system.
	
	////////////////////////////////////////////////////////////////
	// The following are references to objects needed for management 
	// of data as required by the particular octions of the command-set..
	// The following represents the object that will be capable of
	// managing the different lists that are created by the system
	// to be implemented as a lab exercise. 
	private ListsManager listsManager = new ListsManager(); 
	private DiskSystemManager diskManager = new DiskSystemManager();

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		
		// stack of states
		currentState = new IntStack(); 
		
		// The system may need to manage different states. For the moment, we
		// just assume one state: the general state. The top of the stack
		// "currentState" will always be the current state the system is at...
		currentState.push(GENERALSTATE); 

		// Maximum number of states for the moment is assumed to be 1
		// this may change depending on the types of commands the system
		// accepts in other instances...... 
		createCommandList(1);    // only 1 state -- GENERALSTATE

		// commands for the state GENERALSTATE
		
		// the following are just for demonstration...
		add(GENERALSTATE, SystemCommand.getVLSC("testoutput int", 
				new TestOutputProcessor()));        // just for demonstration
//		add(GENERALSTATE, SystemCommand.getVLSC("addnumbers int_list", 
//				new AddNumbersProcessor()));        // just for demonstration
		
		// the following are for the different commands that are accepted by
		// the shell-like system that manage lists of integers
		
		// the command to create a new list is treated here as a command of variable length
		// as in the case of command testoutput, it is done so just to illustrate... And
		// again, all commands can be treated as of variable length command... 
		// One need to make sure that the corresponding CommandActionHandler object
		// is also working (in execute method) accordingly. See the documentation inside
		// the CommandActionHandler class for testoutput command.
//		add(GENERALSTATE, SystemCommand.getVLSC("create name", new CreateProcessor())); 
		
		// the following commands are treated as fixed lentgh commands....
//		add(GENERALSTATE, SystemCommand.getFLSC("add name int int", new AddProcessor())); 		
//		add(GENERALSTATE, SystemCommand.getFLSC("showlists", new ShowListsProcessor())); 		
//		add(GENERALSTATE, SystemCommand.getFLSC("append name int", new AppendProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("showall name", new ShowAllProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("deletedisk name", new DeleteDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("mount name", new MountDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("unmount", new UnmountDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("loadfile name name", new LoadFileProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("cd dir_name", new ShutDownProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("mk dir", new ShutDownProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("rm dir", new ShutDownProcessor()));
//		add(GENERALSTATE, SystemCommand.getFLSC("rm filename", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cp name name", new CopyProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("ls", new ListDirProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cat name", new DisplayFileProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		// need to follow this pattern to add a SystemCommand for each
		// command that has been specified...
		// ...
				
		// set to execute....
		stopExecution = false; 

	}
		
	public ArrayList<String> getResultsList() { 
		return resultsList; 
	}
	
	// INNER CLASSES -- ONE FOR EACH VALID COMMAND --
	/**
	 *  The following are inner classes. Notice that there is one such class
	 *  for each command. The idea is that enclose the implementation of each
	 *  command in a particular unique place. Notice that, for each command, 
	 *  what you need is to implement the internal method "execute(Command c)".
	 *  In each particular case, your implementation assumes that the command
	 *  received as parameter is of the type corresponding to the particular
	 *  inner class. For example, the command received by the "execute(...)" 
	 *  method inside the "LoginProcessor" class must be a "login" command. 
	 *
	 */
	
	private class ShutDownProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			resultsList = new ArrayList<String>(); 
			resultsList.add("SYSTEM IS SHUTTING DOWN!!!!");
			stopExecution = true;
			return resultsList; 
		}
	}

	private class ShowListsProcessor implements CommandActionHandler { 
	   public ArrayList<String> execute(Command c) {  
	
		// command has no operand - nothing is needed from the
		// command. if it comes here, it is the showall command....
		resultsList = new ArrayList<String>(); 
	
		int nLists = listsManager.getNumberOfLists();
		if (nLists == 0)
		    resultsList.add("There are no lists in the system at this moment."); 
		else {
		    resultsList.add("Names of the existing lists are: "); 
		    for (int i=0; i<nLists; i++)
			  resultsList.add("\t"+listsManager.getName(i)); 		
		    }
	      return resultsList; 
	   } 
	}

	// classes added for the lab exercise about this project. 
	private class ShowAllProcessor implements CommandActionHandler { 
	   public ArrayList<String> execute(Command c) {  
				
	     // command has no operand - nothing is needed from the
	     // command. if it comes here, it is the showall command....
	     resultsList = new ArrayList<String>(); 

	     // Show each element in the list in a different line, followin
	     // the specified format: index   --- value
	     // put some heading too....

	     FixedLengthCommand fc = (FixedLengthCommand) c;
			
	     String name = fc.getOperand(1); 
	     int listIndex = listsManager.getListIndex(name); 
	     if (listIndex == -1)
		  resultsList.add("No such list: " + name); 
	     else {
		  int lSize = listsManager.getSize(listIndex);
		  if (lSize == 0)
		      resultsList.add("List is currently empty."); 
		  else {
			resultsList.add("Values in the list are: "); 
			for (int i=0; i<lSize; i++) 
			    resultsList.add("\tlist[" + i + "] --- " +   
	                    listsManager.getElement(listIndex, i)); 		
	        }
	     }
	     return resultsList; 
	   } 
	}


	private class AppendProcessor implements CommandActionHandler { 
	   public ArrayList<String> execute(Command c) {  
	
	      resultsList = new ArrayList<String>(); 
	
	      FixedLengthCommand fc = (FixedLengthCommand) c;
	
	      // the following needs to be adapted to named lists and the 
	      // usage of the ListsManagerObject ......
	
	      String name = fc.getOperand(1); 
	      int listIndex = listsManager.getListIndex(name); 
	      if (listIndex == -1)
	         resultsList.add("No such list: " + name); 
	      else {
		   int value = Integer.parseInt(fc.getOperand(2)); 
	         listsManager.addElement(listIndex, value);
	      }
	      return resultsList; 
	   } 
	}
	private class AddProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  
		
		      resultsList = new ArrayList<String>(); 
		
		      FixedLengthCommand fc = (FixedLengthCommand) c;
		
		      // the following needs to be adapted to named lists and the 
		      // usage of the ListsManagerObject ......
		
		      String name = fc.getOperand(1); 
		      int listIndex = listsManager.getListIndex(name); 
			  int index = Integer.parseInt(fc.getOperand(2));

		      if (listIndex == -1)
		         resultsList.add("No such list: " + name); 
		      else if(index > listsManager.getSize(listIndex))
		    	  resultsList.add("No such index: " + index);
		      else {
			   int value = Integer.parseInt(fc.getOperand(3)); 
		         listsManager.addElement(listIndex, index, value);
		      }
		      return resultsList; 
		   } 
		}



	
	/** this is added just for testing purposes and is not part of 
	 * what has been specified.
	 * @author pirvos
	 *
	 */
	private class TestOutputProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) {  
			
			// Implemented as a Variablle length command, just for testing
			// such feature. For a fixed length alternative, just comment
			// the following two lines, and remove comment markers from
			// the two after. Remember to properly modify the line of
			// code that adds the corresponding system command to 
			// the processor --- so as to get a FLSC...
			
			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String operand = vlc.getItemsForOperand(1).get(0);

			//FixedLengthCommand fc = (FixedLengthCommand) c; 
			//String operand = fc.getOperand(1); 

			resultsList = new ArrayList<String>(); 

			// the first operand is assumed to be an integer...
			int operandInt = Integer.parseInt(operand); 
			if (operandInt < 1) 
				resultsList.add("Incorrect int value"); 
			else 
				for (int index=1; index <= operandInt; index++) 
					resultsList.add("Line number "+index); 

			return resultsList; 
		} 
	}

	// classes added for the lab exercise about this project. 
	
	private class CreateProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String name = vlc.getItemsForOperand(1).get(0);

			//FixedLengthCommand fc = (FixedLengthCommand) c;
			//String name = fc.getOperand(1); 

			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			else if (listsManager.nameExists(name)) 
				resultsList.add("Name give is already in use by another list: " + name); 
			else 
				listsManager.createNewList(name);
			return resultsList; 
		} 
		
	}


	// an additional command... just for demonstration....
	private class AddNumbersProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			
						
			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			ArrayList<String> operandList = vlc.getItemsForOperand(1);

			resultsList = new ArrayList<String>(); 

			// the first operand is assumed to be a list of integers...
			// put all numbers in resultsList while adding them up
			int sum = 0; 
			resultsList.add("Numbers to add are: "); 
			for (String operand : operandList) { 
				resultsList.add(operand); 
				sum += Integer.parseInt(operand); 
			}
			resultsList.add("===================="); 
			resultsList.add("Total = " + sum); 

			return resultsList; 
		} 
	}
	
	
	private class CreateDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			int numberOfBlocks = Integer.parseInt(fc.getOperand(2));
			int size = Integer.parseInt(fc.getOperand(3));


			//String name = fc.getOperand(1); 

			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			else if (diskManager.diskExists(name)) 
				resultsList.add("Name give is already in use by another disk: " + name); 
			else 
				diskManager.createDisk(name, numberOfBlocks, size);
			return resultsList; 
		} 
		
	}
	private class ShowDisksProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  
		
			// command has no operand - nothing is needed from the
			// command. if it comes here, it is the showall command....
			resultsList = new ArrayList<String>(); 
		
			int nLists = diskManager.getNumberOfDisks();
			if (nLists == 0)
			    resultsList.add("There are no disks in the system at this moment."); 
			else {
			    resultsList.add("Names of the existing disks are: "); 
				String str="";

			    for (int i=0; i<nLists; i++){
			    	if(diskManager.getName(i).equals(diskManager.mountedDiskName))
						try {
							str="\t"+diskManager.getName(i) +"\t "+ DiskUnit.mount(diskManager.getName(i)).getBlockSize()+"\t "+ DiskUnit.mount(diskManager.getName(i)).getCapacity()+"\t"+ "mounted";
						} catch (NonExistingDiskException e) {
							e.printStackTrace();
						}
			    	else{
			    		try {
							str="\t"+diskManager.getName(i) +"\t "+ DiskUnit.mount(diskManager.getName(i)).getBlockSize()+"\t "+ DiskUnit.mount(diskManager.getName(i)).getCapacity()+"\t"+ "unmounted";
						} catch (NonExistingDiskException e) {
							e.printStackTrace();
						}
			    	}
			    resultsList.add(str);
			    }
			    	
					
			    }
		      return resultsList; 
		   } 
		}
	private class DeleteDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);



			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			else if (!diskManager.diskExists(name)) 
				resultsList.add("Name given does not exists: " + name); 
			else 
				diskManager.deleteDisk(name);
			return resultsList; 
		} 
		
	}
	private class MountDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);


			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			else if (!diskManager.diskExists(name)) 
				resultsList.add("Disk name given does not exists: " + name); 
			else 
				diskManager.mountDisk(name);
			return resultsList; 
		} 
		
	}
	private class UnmountDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;


			 if (!diskManager.diskIsMounted()) 
				resultsList.add("There is no disk mounted currently"); 
			else 
				diskManager.unmountDisk();
			return resultsList; 
		} 
		
	}
	private class LoadFileProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String fileToRead = fc.getOperand(1);
			String newFile = fc.getOperand(2);


			if (!OperandValidatorUtils.isValidName(fileToRead))
				resultsList.add("Invalid name formation: " + fileToRead);
			else if(!OperandValidatorUtils.isValidName(newFile))
				resultsList.add("Invalid name formation: " + newFile);
			else if (!diskManager.diskIsMounted()) 
				resultsList.add("There is no disk mounted currently"); 
			else 
				diskManager.loadFile(fileToRead, newFile);
			return resultsList; 
		} 
		
	}
	private class DisplayFileProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String fileToDisplay = fc.getOperand(1);


			if (!OperandValidatorUtils.isValidName(fileToDisplay))
				resultsList.add("Invalid name formation: " + fileToDisplay);
			else if (!diskManager.diskIsMounted()) 
				resultsList.add("There is no disk mounted currently"); 
			else 
				diskManager.displayFile(fileToDisplay);
			return resultsList; 
		} 
		
	}
	private class ListDirProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;

			if (!diskManager.diskIsMounted()) 
				resultsList.add("There is no disk mounted currently"); 
			else
				diskManager.showDir();
			
			return resultsList; 
		} 
		
	}
	private class CopyProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String fileToCopy = fc.getOperand(1);
			String fileToCopyOnto = fc.getOperand(2);


			if (!OperandValidatorUtils.isValidName(fileToCopy))
				resultsList.add("Invalid name formation: " + fileToCopy);
			else if(!OperandValidatorUtils.isValidName(fileToCopyOnto))
				resultsList.add("Invalid name formation: " + fileToCopyOnto);
			else if (!diskManager.diskIsMounted()) 
				resultsList.add("There is no disk mounted currently"); 
			else 
				diskManager.copyFile(fileToCopy, fileToCopyOnto);
			return resultsList; 
		} 
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean inShutdownMode() {
		return stopExecution;
	}

}		





