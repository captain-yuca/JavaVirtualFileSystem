# Java Virtual Disk

NOTE: This is a Markdown file. This is a regular readme with Markdown syntax. Anything between this > `` `` is code.


This is a Virtual Disk program for Java. This project implements a virtual disk system. You can create, read and write the Disk utilizing disk units which are composed of bytes.

This project contains two test files: DiskUnitTester0.java and DiskUnitTester1.java .

DiskUnitTester0 is in charge of creating the 5 DiskUnit files to test.

DiskUnitTester1 is in charge of reading the created Disks and outputting their contents.

By default the DiskUnitTester1 will test disk1. If you want to test the other disks, you need to change the file in src/testers/DiskUnitTester0.java to the appropriate disk name in line 11. Once that change is made, you have to compile the java file as shown in the next section.

Compared to other outputs, the output of DiskUnitTester1 might differ from system to system. This is due to encoding of characters. To ensure its compability is assured, you must set any text encoding to UTF-8.

## How to run the program

### Eclipse

To run in Eclipse, do the following:
1. Open Eclipse and go to File > Import
2. Here, you will click General > Projects from Folder or Archive
3. Afterwards, select the Archive option. This will prompt for the archive. Find the zip folder of this project that you obtained. Only select the project folder from the two expanded ones. Click finish.
4. Make sure that the encoding set in Eclipse is UTF-8. Go to Window > Preferences, then click General > Workspace. The text encoding should be set to UTF-8. If not, select other: UTF-8.
5. Once you have the project open in your Package Explorer, navigate to P1_802140616 > src > testers > DiskUnitTester0. Right click DiskUnitTester0 and click Run As > Java Application. It might start indexing at this point but just wait. This will not present an output but it will create the 5 disk files.
6. Navigate to P1_802140616 > src > testers > DiskUnitTester1. Right click DiskUnitTester1 and click Run As > Java Application. This will output the test.

### Console
To run using a console instance, do the following:
1. On your console, navigate to the projects root directory.
2. Verify if the bin folder is in the project directory. You can do this by using ``ls`` or ``dir`` on Windows. If not, execute ``mkdir bin``.
3. Before running the testers, we have to compile the java files first. To compile all the java files to class files, use the following:

  ``javac -d bin -sourcepath src src\testers\*.java``

  ``javac -d bin -sourcepath src src\exceptions\*.java``

  ``javac -d bin -sourcepath src src\diskUtilities\*.java``

  Note: Depending on what you are using (bash or command prompt), the \ might change to /.


4. After compiling, you need to create the disks for testing. This is done by using:

  ``java -classpath bin testers.DiskUnitTester0``

5. If you verify the files inside the directory, you should have 5 disk files named disk1, disk2, disk3, disk4 and disk5. These are the disks we will use to test.  To run, do the following:

  ``java -classpath bin testers.DiskUnitTester1``
