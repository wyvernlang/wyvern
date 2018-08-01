#!/bin/bash
set -e

TIMEFORMAT='%3R'

WYBY=$WYVERN_HOME/bin/wyby

if [ ! -f boot.js ]; then
    echo "Could not find boot.js"
    echo "Did you run ./bootstrap.sh first?"
    exit 1
fi

(
cd src/
rm -f main.wyb
$WYBY main.wyv
)
echo "Self-bootstrapping"
echo -n "time: "
time node boot.js src/main.wyb > selfboot.js
mv boot.js boot.js.old
mv selfboot.js boot.js
