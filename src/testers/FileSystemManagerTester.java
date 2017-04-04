package testers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import diskUtilities.DiskUnit;
import diskUtilities.FileSystemManager;
import exceptions.NonExistingDiskException;

public class FileSystemManagerTester {
	private static final String DISKNAMEPREFIX="test-disk"; 

	@Test
	public void createFormattedDiskForUseTest() {
		
		FileSystemManager.createFormattedDiskForUse(DISKNAMEPREFIX, 1024, 256);
		DiskUnit testDisk=null;

		try {
			testDisk = DiskUnit.mount(DISKNAMEPREFIX);
		} catch (NonExistingDiskException e) {
			fail("Did not create the RAF file or did not use the appropiate name.");
		}

		//Assert that all of the private variables are used
		assertTrue("Capacity is not 1024: " + testDisk.getCapacity(),testDisk.getCapacity()==1024);
		assertTrue("Block Size is not 256: " + testDisk.getBlockSize(),testDisk.getBlockSize()==256);
		assertTrue("First Free Block Index: " + testDisk.getFirstBlockIndex(), testDisk.getFirstBlockIndex()==973);
		assertTrue("Next Free Block Index: "+ testDisk.getFreeBlockIndex(), testDisk.getFreeBlockIndex()==1022);
		assertTrue("First Free INode Index: " + testDisk.getFirstFreeINodeIndex(), testDisk.getFirstFreeINodeIndex()==1);
		assertTrue("Total INodes is not 291: " + testDisk.getTotalINodes(), testDisk.getTotalINodes()==(int)((1024*256*.01)/9));
		
		
	}
	@Test
	public void createFormattedDiskContentTester() {
		FileSystemManager.createFormattedDiskForUse(DISKNAMEPREFIX+1, 1024, 256);
		DiskUnit testDisk=null;
		try {
			testDisk = DiskUnit.mount(DISKNAMEPREFIX+1);
		} catch (NonExistingDiskException e) {
			fail("Did not create the RAF file or did not use the appropiate name.");
		}
		assertTrue("Did not match the free block Integers: " + 
		TesterUtils.buildStringWithIntsForGivenBlock(testDisk, 909), 
		TesterUtils.buildStringWithIntsForGivenBlock(testDisk, 909).
		equals("845 - 910 - 911 - 912 - 913 - 914 - 915 - "
				+ "916 - 917 - 918 - 919 - 920 - 921 - 922 - "
				+ "923 - 924 - 925 - 926 - 927 - 928 - 929 - "
				+ "930 - 931 - 932 - 933 - 934 - 935 - 936 - "
				+ "937 - 938 - 939 - 940 - 941 - 942 - 943 - "
				+ "944 - 945 - 946 - 947 - 948 - 949 - 950 - "
				+ "951 - 952 - 953 - 954 - 955 - 956 - 957 - "
				+ "958 - 959 - 960 - 961 - 962 - 963 - 964 - "
				+ "965 - 966 - 967 - 968 - 969 - 970 - 971 - "
				+ "972 - "));
	}
	
	@Test
	public void writeFileTester(){
		FileSystemManager.createFormattedDiskForUse(DISKNAMEPREFIX+2, 1024, 256);
		DiskUnit testDisk=null;
		try {
			testDisk = DiskUnit.mount(DISKNAMEPREFIX+2);
		} catch (NonExistingDiskException e) {
}

		FileSystemManager.writeFile("fileToAdd.txt", "fileToTest", testDisk);
//		System.out.println(TesterUtils.buildStringWithINodeParams(0, testDisk));
//		TesterUtils.showChars(testDisk);

		assertTrue("Did not match the String:\n " + 
			TesterUtils.buildStringWithCharForGivenBlock(testDisk, 1019) + "\n\n" + " i-node corresponding to the particular \nfile or subdirectory in the disk. For this, we have the following rules. A file name \nis a string of no more than 20 characters (one byte each - ASCII code) long and the \nnumber identifying an i-node is a 4-byte1018", 
			TesterUtils.buildStringWithCharForGivenBlock(testDisk, 1019).equals
			("i-node corresponding to the particular \nfile or subdirectory in the disk. For this, we have the following rules. A file name \nis a string of no more than 20 characters (one byte each - ASCII code) long and the \nnumber identifying an i-node is a 4-byte1018"));
		
		assertTrue("Did not match the iNode params: " + 
		TesterUtils.buildStringWithINodeParams(1, testDisk), 
		TesterUtils.buildStringWithINodeParams(1, testDisk).equals("1022-1581-1"));
//		System.out.println(TesterUtils.buildStringWithINodeParams(0, testDisk));

		
		FileSystemManager.writeFile("fileToAdd2.txt", "fileToTest2", testDisk);
		
		assertTrue("Did not match the iNode params: " + 
				TesterUtils.buildStringWithINodeParams(2, testDisk), 
				TesterUtils.buildStringWithINodeParams(2, testDisk).equals("1014-32-1"));
		System.out.println(TesterUtils.buildStringWithDirectoryINodeParams(0, testDisk));
//		System.out.println(TesterUtils.buildStringWithINodeParams(0, testDisk));
//		TesterUtils.showChars(testDisk);
	}
}


