# Java Virtual Disk

This is a Virtual Disk program for Java. This project implements a virtual disk system. You can create, read and write the Disk utilizing disk units which are composed of bytes.

This project contains two test files: DiskUnitTester0.java and DiskUnitTester1.java .

DiskUnitTester0 is in charge of creating the 5 DiskUnit files to test.

DiskUnitTester1 is in charge of reading the created Disks and outputting their contents.

By default the DiskUnitTester1 will test disk1. If you want to test the other disks, you need to change the file in src/testers/DiskUnitTester0.java to the appropriate disk name in line 11. Once that change is made you have to compile the java file as shown in the next section.

## How to run the program

### Eclipse

To run in Eclipse, do the following:
1. Open Eclipse and go to File > Import
2. Here, you will click General > Projects from Folder or Archive
3.

### Console
To run using a console instance, do the following:
1. On your console, navigate to the projects root directory.
2. Verify if the bin folder is in the project directory. You can do this by using ``ls`` or ``dir`` on Windows. If not, execute ``mkdir bin``.
3. Before running the testers, we have to compile the java files first. To compile all the java files to class files, use the following:

  ``javac -d bin -sourcepath src src\testers\*.java``

  ``javac -d bin -sourcepath src src\exceptions\*.java``

  ``javac -d bin -sourcepath src src\diskUtilities\*.java``

4. After compiling, you need to create the disks for testing. This is done by using:

  ``java -classpath bin testers.DiskUnitTester0``

5. If you verify the files inside the directory, you should have 5 disk files named disk1, disk2, disk3, disk4 and disk5. These are the disks we will use to test.  To run, do the following:

  ``java -classpath bin testers.DiskUnitTester1``
