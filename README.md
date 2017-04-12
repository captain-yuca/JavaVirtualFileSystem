# Java Virtual Disk

NOTE: This is a Markdown file. This is a regular readme with Markdown syntax. Anything between this > `` `` is code.


This is a Virtual Disk program for Java. This project implements a virtual disk system. You can create, read and write the Disk utilizing disk units which are composed of bytes. This is implemented utilizing a UNIX style command prompt.

You can utilize this program by executing the theSystem.MySystem class which runs the system for use.

By typing help, you can see all the commands available for the program.


Compared to other outputs, the output of DiskUnitTester1 might differ from system to system. This is due to encoding of characters. To ensure its compability is assured, you must set any text encoding to UTF-8.

## How to run the program

### Eclipse

To run in Eclipse, do the following:
1. Open Eclipse and go to File > Import
2. Here, you will click General > Projects from Folder or Archive
3. Afterwards, select the Archive option. This will prompt for the archive. Find the zip folder of this project that you obtained. Only select the project folder from the two expanded ones. Click finish.
4. Make sure that the encoding set in Eclipse is UTF-8. Go to Window > Preferences, then click General > Workspace. The text encoding should be set to UTF-8. If not, select other: UTF-8.
5. Once you have the project open in your Package Explorer, navigate to P1_802140616  > src > theSystem. Right click MySystem and click Run As > Java Application. This will start the command prompt.

### Console
To run using a console instance, do the following:
1. On your console, navigate to the projects root directory.
2. Verify if the bin folder is in the project directory. You can do this by using ``ls`` or ``dir`` on Windows. If not, execute ``mkdir bin``.
3. Before running the testers, we have to compile the java files first. To compile all the java files to class files, use the following:

  ``javac -d bin -sourcepath src src\theSystem\*.java``

  ``javac -d bin -sourcepath src src\exceptions\*.java``

  ``javac -d bin -sourcepath src src\lists\*.java``

  ``javac -d bin -sourcepath src src\listsManagementClasses\*.java``

  ``javac -d bin -sourcepath src src\diskUtilities\*.java``

  ``javac -d bin -sourcepath src src\testers\*.java``

  ``javac -d bin -sourcepath src src\stack\*.java``

  ``javac -d bin -sourcepath src src\operandHandlers\*.java``

  ``javac -d bin -sourcepath src src\systemGeneralClasses\*.java``

  Note: Depending on what you are using (bash or command prompt), the \ might change to /.


4. After compiling, you can use the program. This is done by using:

  ``java -classpath bin theSystem.MySystem``
