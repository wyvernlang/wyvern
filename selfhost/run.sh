#!/bin/bash
echo Usage example:
echo ./run.sh tests/testInt.wyv

wyby main.wyv
node ../backend/boot.js main.wyb > main.js
node main.js $1 $2 $3 $4 $5
