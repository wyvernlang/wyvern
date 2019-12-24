exports.print = function(s) {
    process.stdout.write("" + s);
}

exports.printInt = exports.print;

exports.printBoolean = exports.print;

exports.printFloat = exports.print;

exports.println = function(s) {
    process.stdout.write("\n");
}

exports.flush = function(s) {
    // NOP, node doesn't have flush
}
