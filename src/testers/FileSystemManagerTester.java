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

	@Test
	public void createFormattedDiskForUseTest() {
		
		FileSystemManager.createFormattedDiskForUse("test-disk", 1024, 256);
		DiskUnit testDisk=null;

		try {
			testDisk = DiskUnit.mount("test-disk");
		} catch (NonExistingDiskException e) {
			fail("Did not create the RAF file or did not use the appropiate name.");
		}

		//Assert that all of the private variables are used
		assertTrue("Capacity is not 1024: " + testDisk.getCapacity(),testDisk.getCapacity()==1024);
		assertTrue("Block Size is not 256: " + testDisk.getBlockSize(),testDisk.getBlockSize()==256);
		assertTrue("First Free Block Index: " + testDisk.getFirstBlockIndex(), testDisk.getFirstBlockIndex()==973);
		assertTrue("Next Free Block Index: "+ testDisk.getFreeBlockIndex(), testDisk.getFreeBlockIndex()==1023);
		assertTrue("First Free INode Index: " + testDisk.getFirstFreeINodeIndex(), testDisk.getFirstFreeINodeIndex()==2);
		assertTrue("Total INodes is not 291: " + testDisk.getTotalINodes(), testDisk.getTotalINodes()==(int)((1024*256*.01)/9));
		
		
	}
	@Test
	public void createFormattedDiskContentTester() {
		FileSystemManager.createFormattedDiskForUse("test-disk1", 1024, 256);
		DiskUnit testDisk=null;
		try {
			testDisk = DiskUnit.mount("test-disk1");
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
		TesterUtils.showInts(testDisk);
	}
}


