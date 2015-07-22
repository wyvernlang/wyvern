#!/bin/sh
export PATH=${PATH}:/media/sf_alexmac/Programs/clang+llvm-3.5.0-x86_64-linux-gnu/bin
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/ ./autogen.sh
make
