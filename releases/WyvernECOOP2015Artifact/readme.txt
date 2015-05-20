In the current folder:

leoadmins-MacBook-Air:WyvernECOOP2015Artifact alex$ ls
CopperCompiler.jar	hamcrest-core-1.3.jar	junit-4.11.jar		tests
asm-debug-all-5.0.1.jar	javatuples-1.2.jar	readme.txt		wyvern.jar
borderedwindow.wyv	json.wyv		testmethods.wyv

To run use Java 8 or higher:

java -jar wyvern.jar borderedwindow.wyv

or

java -jar wyvern.jar borderedwindow2.wyv

or

java -jar wyvern.jar json.wyv

For your reference we provided the tests folder which is a copy of our
JUnit tests folder with tag related code from GitHub, however some
tests are supposed to make Wyvern fail to compile them and some expect
it to succeed. Its best to run them with JUnit by checking out the
full source code from GitHub.
