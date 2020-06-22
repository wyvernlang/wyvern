#!/bin/bash
echo Expecting first argument to be the name of the file without the wyv extension.
wyby $1.wyv
node ../backend/boot.js $1.wyb > $1.js
node $1.js $2 $3 $4 $5
