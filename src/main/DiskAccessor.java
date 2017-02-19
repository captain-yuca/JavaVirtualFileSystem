package main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import exceptions.*;


public class DiskAccessor {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RandomAccessFile test = new RandomAccessFile("TestFile", "rws");;
		System.out.println(test.length());

//		test.write(13);
//		test.write(13);
//		test.write(13);
//		test.write(13);
//		test.write(13);
//		test.write(13);
		System.out.println(test.length() + ",    " + test.read());
	}
	
	


}
