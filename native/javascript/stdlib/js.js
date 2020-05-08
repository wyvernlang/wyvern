exports.req = function(s) {
    return require(s)
}

exports.isUndefined = function(x) {
    return x === undefined
}

exports.getUndefined = function(x) {
    return undefined
}

exports.log = function(x) {
    console.log(x);
}

exports.describe = function(x) {
    console.log("Describing ", x);
    console.log("type ", typeof x);
    console.log("length ", x.length);
}

exports.equalsJS = function(x, y) {
    //console.log("Testing: ", x, "=", y, "\n");
    return x === y
}

exports.toArray = function(list) {
	var curr = list;
	var result = [];
	while (curr.hasOwnProperty("value")) {
		result.push(curr.value);
		curr = curr.next;
	}
	return result;
}