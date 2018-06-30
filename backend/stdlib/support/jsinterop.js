exports.index = function(l, i) {
    return l[i];
}

exports.length = function(l) {
    if (l === undefined)
        debugger;
    return l.length;
}

exports.bufferToInteger = function(b) {
    var l = b.length;
    var n = 0;
    for (var i = l - 1; i >= 0; i--) {
        n *= 256;
        n += b[i];
    }
    return n;
}

exports.getFirstCommandLineArg = function() {
    return process.argv[2];
}
