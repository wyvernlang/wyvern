const readline = require("readline");

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

exports.onLine = function(cb) {
	// invoke cb as a Wyvern function whenever input arrives from standard in
	const rl = readline.createInterface({
	  input: process.stdin,
	  output: process.stdout,
	  terminal: false
	});
	
	rl.on('line', function (line) {
	  cb.apply(line)
	});
}