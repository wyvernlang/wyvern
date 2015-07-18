To compile LLVM your system must follow following requirements:

*LLVM 3.5.0 and LLVM Libraries 3.5.0

*GCC C++ Compiler or Clang

*GNU Autotools including libtool, GNU Automake

To compile first you need to setup JAVA\_HOME environment variable, on many systems it is already setup. But, if it is not setup then you must setup the JAVA\_HOME variable. JAVA\_HOME variable points to the valid java installation. 
To compile follow these commands:

1. JAVA_HOME = java-dir ./autogen.sh
2. make

Now a file named libWyvernLLVM.so (on Linux) or WyvernLLVM.dll (on Windows) will be created in this directory. Search for this file and paste this path in the System.load () function call in Interpreter.java.
Now, LLVM JIT can be executed.
