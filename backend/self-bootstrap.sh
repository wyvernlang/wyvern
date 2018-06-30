#!/bin/bash
set -e

TIMEFORMAT='%3R'

# Builds and runs all command line arguments with javascript backend
# If no arguments are provided, builds and runs examples/*.wyv

WYBY=$WYVERN_HOME/bin/wyby

if [ ! -f boot.js ]; then
    echo "Could not find boot.js"
    echo "Did you run ./bootstrap.sh first?"
    exit 1
fi

(
cd src/
$WYBY backend.wyv
)
echo "Self-bootstrapping"
echo -n "time: "
time node boot.js src/backend.wyb > nextboot.js || rm nextboot.js
echo "Sanity check"
echo -n "time: "
time node nextboot.js src/backend.wyb > sanitycheck.js || rm sanitycheck.js nextboot.js
diff -q nextboot.js sanitycheck.js || (echo "sanity check failed" && exit 1)
mv boot.js boot.js.old
mv nextboot.js boot.js
rm sanitycheck.js
