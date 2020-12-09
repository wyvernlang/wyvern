exports.execute = function(environment, code) {
    console.log("executing " + code);
	// environment is a linked list; traverse collecting value elements
	// each element is a pair
	const args = [];
	// put the second part into an array
	// build a let-binding based on the first part and FUNCTION_ARGS[currentIndex]
	// prepend the let bindings to the code
	const extendedCode = code;
	const encapsulatedCode = new Function('FUNCTION_ARGS', extendedCode);
	// TODO: implement all of the above, and then:
	// run the extended code
	encapsulatedCode(args)
}
