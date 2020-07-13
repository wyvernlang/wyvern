#!/bin/bash
echo Usage example:
echo ./run.sh tests/testInt.wyv

echo COMPILING AND PRODUCING WYB FILE FOR $1
wyby main.wyv
node ../backend/boot.js main.wyb > main.js
node main.js $1

#echo FINISHED PRODUCING WYB FILE FOR $1 NOW RUNNING IT
#node ../backend/boot.js `echo $1 | sed 's/\.wyv/\.wyb/'` > `echo $1 | sed 's/\.wyv/\.js/'`
#node  `echo $1 | sed 's/\.wyv/\.js/'`
