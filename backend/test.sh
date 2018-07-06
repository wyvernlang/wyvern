#!/bin/bash
set -e

TIMEFORMAT='%3R'

# Builds and runs all command line arguments with javascript backend
# If no arguments are provided, builds and runs examples/*.wyv

FILES=( "${@:-examples/*.wyv}" )

WYBY=$WYVERN_HOME/bin/wyby

if [ ! -f boot.js ]; then
    echo "Could not find boot.js"
    echo "Did you run ./bootstrap.sh first?"
    exit 1
fi

for f in ${FILES[@]}; do
    echo "running $f:"
    (
    DIR="$(dirname "$f")"
    FILE="$(basename "$f")"
    cd "$DIR"
    $WYBY "$FILE"
    )
    echo -n "Time to compile: "
    time node boot.js "${f%.*}.wyb" > "${f%.*}.js"
    time node "${f%.*}.js"
done
