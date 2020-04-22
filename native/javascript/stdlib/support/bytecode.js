const protobuf = require("protobufjs");
const fs = require("fs");

function wrap(o) {
    var wrapper = {};

    wrapper.inner = o;

    for (let p in o) {
        var capitalized = p[0].toUpperCase() + p.substring(1, p.length);
        if (capitalized[capitalized.length - 1] === "_") {
            capitalized = capitalized.substring(0, capitalized.length - 1);
        }
        var getterName = "get" + capitalized;
        var hasName = "has" + capitalized;
        if (o.hasOwnProperty(p)) {
            if (wrapper.inner[p] instanceof Array) {
                wrapper[getterName] = function(i) {
                    var x = this.inner[p][i];
                    if (typeof x === "object") {
                        return wrap(x);
                    } else {
                        return x;
                    }
                };
                wrapper[getterName + "List"] = function() {
                    var x = this.inner[p];
                    if (typeof x === "object") {
                        return x.map( i => wrap(i));
                    } else {
                        return x;
                    }
                };
                wrapper[getterName + "Count"] = function() {
                    return this.inner[p].length;
                };
            } else {
                wrapper[getterName] = function() {
                    var x = this.inner[p];
                    if (typeof x === "object" && !(x instanceof Buffer)) {
                        return wrap(x);
                    } else {
                        return x;
                    }
                };
            }
        } 
        if (!(p === "constructor" || p === "$type" || p === "toJSON")) {
            wrapper[hasName] = function() {
                return this.inner.hasOwnProperty(p);
            };
        }
    }
    
    return wrapper;
}

var root = undefined;

function setRoot() {
	if (root === undefined) {
		root = protobuf.loadSync(process.env.WYVERN_HOME + "/backend/bytecode.proto");
	}
}

exports.Type = function() {
	setRoot();
	return root.lookupType('Type');
}

exports.loadBytecode = function(path) {
	setRoot();
    const Bytecode = root.lookupType('Bytecode');
    var bytes = fs.readFileSync(path);
    var bytecode = Bytecode.decode(bytes);
    return wrap(bytecode);
}

exports.saveBytecode = function(path, bytecodeObject) {
	setRoot();
    const Bytecode = root.lookupType('Bytecode');
	var errMsg = Bytecode.verify(bytecodeObject);
    if (errMsg)
        throw Error(errMsg);
	var bmsg = Bytecode.create(bytecodeObject);
	var bbuffer = Bytecode.encode(bmsg).finish();
	fs.writeFileSync(path, bbuffer);
}

exports.encodeExpr = function(exprObject) {
	setRoot();
    const Expression= root.lookupType('Expression');
	var errMsg = Expression.verify(exprObject);
    if (errMsg)
        throw Error(errMsg);
	var bmsg = Expression.create(exprObject);
	var bbuffer = Expression.encode(bmsg).finish();
	var msg2 = Expression.decode(bbuffer);
	console.log(msg2)
}