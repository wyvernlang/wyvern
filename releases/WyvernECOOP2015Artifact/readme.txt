To run:

java -jar wyvern.jar borderedwindow.wyv

or

java -jar wyvern.jar json.wyv

For your reference we provided the tests folder which is a copy of our JUnit tests folder with tag related code from GitHub, however some tests are supposed to make Wyvern fail to compile them and some expect it to succeed. Its best to run them with JUnit by checking out the full source code from GitHub.


FROM KICK THE TIRES:


===========================================================================
                     ECOOP 2015 Artifacts Review #11A
                     Updated 25 Mar 2015 8:50:35am EDT
---------------------------------------------------------------------------
                   Paper #11: A Theory of Tagged Objects
---------------------------------------------------------------------------


                   ===== Kicking-the-tires outcome =====

From readme.txt, I tried

java -jar wyvern.jar json.wyv

I got the following exception

Exception in thread "main" java.lang.UnsupportedClassVersionError: wyvern/tools/Interpreter : Unsupported major.minor version 52.0
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:800)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:449)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:71)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:361)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:355)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.net.URLClassLoader.findClass(URLClassLoader.java:354)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:425)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:358)
	at java.lang.Class.forName0(Native Method)
	at java.lang.Class.forName(Class.java:274)
	at org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader.main(JarRsrcLoader.java:56)

     ===== Computing platform(s) used for assessing the artifact =====

I am using Ubuntu 14.04. I have java version 1.7.0_075.

===========================================================================
                     ECOOP 2015 Artifacts Review #11B
                     Updated 26 Mar 2015 1:09:39pm EDT
---------------------------------------------------------------------------
                   Paper #11: A Theory of Tagged Objects
---------------------------------------------------------------------------


                   ===== Kicking-the-tires outcome =====

There are only few instructions and it is not clear what to expect from the artifact. The attached artifact contains an executable jar and a set of over 100 Wyvern test programs. The executable jar is able to execute the test programs I tried but there is no provided way to execute all the test programs, and we are asked to clone the github repository for this purpose. Was there a particular issue with providing a checkout of the github repository as the artifact? The github repository can be updated at any time (including altering history).

I cloned and the github repository and tried to import it in both IntelliJ and Eclipse. It failed to import cleanly in both. With Eclipse it did import (via the provided .project file) but it recognized /tools instead of tools/src as a source directory, and the dependent libraries were not linked. Nothing improved after running ant. After manually fixing the project, the Java classes do compile. Still, it is not clear what is expected from the artifact. 

The only evaluable output seems to be that from running ant on build.xml. It seems to be a report of parsing and compilation. Still, there is no clear link between the output and individual test programs. Could the authors provide a short guide on reading this output? Is there anything else we should look at?

     ===== Computing platform(s) used for assessing the artifact =====

OS X 10.10, Java 8

===========================================================================
                     ECOOP 2015 Artifacts Review #11C
                    Updated 26 Mar 2015 11:50:18pm EDT
---------------------------------------------------------------------------
                   Paper #11: A Theory of Tagged Objects
---------------------------------------------------------------------------


                   ===== Kicking-the-tires outcome =====

I could make it work out of the box with the installation of Java 8.
However, the instructions explaining the usages of the tool is missing. I have to search the detail in the paper

     ===== Computing platform(s) used for assessing the artifact =====

Hardware: Intel Core i5-3230M; 8GB RAM
OS: Windows 64 bit.

===========================================================================
           Response by Alex Potanin <alex.potanin@ecs.vuw.ac.nz>
---------------------------------------------------------------------------
Reviewer A: We require Java 8 and forgot to include this in the readme.txt as we assumed the latest stable versions of the tools will be used by reviewers. Here is a more detailed description of the submission.

What is included in a Wyvern compiler. It takes a program written in Wyvern Programming Language (e.g. borderedwindow.wyv) and then compiles and executes (interprets) the result printing the final value of the evaluation to the standard output.

We included 3 sample programs written in Wyvern with the implementation inside wyvern.jar. Here is an example of running it on Linux:

cuba: [WyvernECOOP2015Artifact] % java -version
java version "1.8.0_25"
Java(TM) SE Runtime Environment (build 1.8.0_25-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02, mixed mode)
cuba: [WyvernECOOP2015Artifact] % java -jar wyvern.jar borderedwindow.wyv
"big"
cuba: [WyvernECOOP2015Artifact] % java -jar wyvern.jar json.wyv
15
cuba: [WyvernECOOP2015Artifact] % java -jar wyvern.jar testmethods.wyv
64

The reviewers can try to modify the programs or write their own following either the description in the paper or using the samples included in the "tests" folder or by looking at the Wyvern language web site with some specifications and papers or GitHub repository.

The main example in the paper is indeed borderedwindow.wyv class that shows the core of the paper's contribution (dynamic tags) working - compiling and executing.

Reviewer B: We did not initially expect that reviewers would need to re-build the compiler in Eclipse.  However, if you wish to do so, here is what you need to do.

There are two parts to the compiler implementation: (1) is the parser generator Copper that converts the grammar in Wyvern.x into Wyvern.java that is then used inside the Wyvern implementation (2).

The ant script in the tools folder is to run Copper to generate the Wyvern.java from Wyvern.x:

cuba: [wyvern] % l
total 40K
drwxr-xr-x 8 alex ecs  512 Mar 13 14:28 ./
drwx------ 6 alex ecs  512 Mar 18 14:05 ../
drwxr-xr-x 8 alex ecs  512 Mar 26 11:08 .git/
-rw-r--r-- 1 alex ecs  586 Mar 12 09:34 .gitignore
-rw-r--r-- 1 alex ecs  18K Mar  7 17:21 COPYING.TXT
drwxr-xr-x 2 alex ecs  512 Mar 12 09:34 bin/
drwxr-xr-x 2 alex ecs  512 Mar 12 09:34 docs/
drwxr-xr-x 3 alex ecs  512 Mar 12 09:34 examples/
-rw-r--r-- 1 alex ecs 2.3K Mar 12 09:34 readme.md
drwxr-xr-x 3 alex ecs  512 Mar 19 11:41 releases/
drwxr-xr-x 6 alex ecs  512 Mar 12 09:34 tools/
cuba: [wyvern] % l tools
total 28K
drwxr-xr-x 6 alex ecs  512 Mar 12 09:34 ./
drwxr-xr-x 8 alex ecs  512 Mar 13 14:28 ../
-rw-r--r-- 1 alex ecs  759 Mar 18 21:21 .classpath
-rw-r--r-- 1 alex ecs  371 Mar  9 15:56 .project
drwxr-xr-x 3 alex ecs  512 Mar 18 21:21 bin/
-rw-r--r-- 1 alex ecs 3.0K Mar  7 17:21 build.xml
drwxr-xr-x 4 alex ecs  512 Mar  9 16:10 copper-composer/
drwxr-xr-x 2 alex ecs  512 Mar 12 09:34 lib/
-rw-r--r-- 1 alex ecs 1.3K Mar 12 09:34 readme.txt
-rwxr-xr-x 1 alex ecs  608 Mar 12 09:34 run.sh*
drwxr-xr-x 3 alex ecs  512 Mar  7 17:21 src/
-rw-r--r-- 1 alex ecs 3.2K Mar 12 09:34 wyvern-tool.xml
cuba: [wyvern] % 

So the instructions are as follows:

1. Clone GitHub repository.

2. Run ant inside wyvern/tools that will generate Wyvern.java from Wyvern.x.

3. Open Eclipse (or similar) and use "import existing project into the workspace" and point it to "wyvern/tools" folder.

4. We do not include .classpath as it is platform specific but we include all libraries in wyvern/tools/lib/ folder:

cuba: [wyvern] % l tools/lib/
total 4.0M
drwxr-xr-x 2 alex ecs  512 Mar 12 09:34 ./
drwxr-xr-x 6 alex ecs  512 Mar 12 09:34 ../
-rw-r--r-- 1 alex ecs 961K Mar  7 17:21 CopperCompiler.jar
-rw-r--r-- 1 alex ecs  40K Mar  7 17:21 CopperRuntime.jar
-rw-r--r-- 1 alex ecs 2.0M Mar  7 17:21 ant.jar
-rw-r--r-- 1 alex ecs 372K Mar  7 17:21 asm-debug-all-5.0.1.jar
-rw-r--r-- 1 alex ecs  44K Mar 12 09:34 hamcrest-core-1.3.jar
-rw-r--r-- 1 alex ecs 229K Mar  7 17:21 javatuples-1.2-javadoc.jar
-rw-r--r-- 1 alex ecs  64K Mar  7 17:21 javatuples-1.2.jar
-rw-r--r-- 1 alex ecs 240K Mar 12 09:34 junit-4.11.jar
cuba: [wyvern] % 

5. After setting the Java Build Path in Eclipse to use Java 8, one need to (1) add library which is JUnit 4 and (2) add external jars which would be: CopperCompiler.jar, asm-debug-all-5.0.1.jar, hamcrest-core-1.3.jar, javatuples-1.2.jar from wyvern/tools/lib.

6. The above will allow Eclipse to build the project with no errors. At that point one can execute the tests (which are work in progress) by right clicjing on the entire source tree and selecting "Run As..." -> "JUnit tests" or more specifically by selecting the tests in wyvern/tools/tests if one prefers...

Please let us know if there are any further queries.

