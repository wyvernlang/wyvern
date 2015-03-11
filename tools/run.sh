#########################################################################
# File Name: run.sh
# Author: Stanley Wang
# mail: stanley.chenglongwang@gmail.com
# Created Time: Thu 10 Jul 2014 08:09:17 AM PDT
#########################################################################
#!/bin/bash
if [ $# -gt 0 ]
then
	echo "[RUNNING] Input file "$0
	input=$1
	java -cp .:bin/:lib/CopperCompiler.jar:lib/CopperRuntime.jar:lib/asm-debug-all-5.0.1.jar:lib/ant.jar:lib/hamcrest-core-1.3.jar:lib/javatuples-1.2.jar:lib/junit-4.11.jar wyvern.tools.util.CLI $input
else
	echo "[ERROR] Please specify the input file."
fi
