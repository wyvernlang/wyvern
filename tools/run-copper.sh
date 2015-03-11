#!/usr/bin/bash
cd src/wyvern/tools/parsing
java -jar ../../../../lib/CopperCompiler-0.7.1.jar -o Wyvern.java Wyvern.x
cd -
cd src/wyvern/tools/parsing/quotelang
java -jar ../../../../../lib/CopperCompiler-0.7.1.jar -o WyvernQuote.java WyvernQuote.x
cd -
