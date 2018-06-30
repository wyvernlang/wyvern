#!/bin/bash
set -e

# Builds and runs all command line arguments with javascript backend
# If no arguments are provided, builds and runs examples/*.wyv

WYBY=$WYVERN_HOME/bin/wyby
WYVERN=$WYVERN_HOME/bin/wyvern

(
cd src/
$WYBY backend.wyv
echo "Bootstrapping..."
time $WYVERN backend.wyv > ../boot.js
) || rm -f boot.js

./self-bootstrap.sh
diff -q boot.js boot.js.old || (echo "sanity check failed" && exit 1)
