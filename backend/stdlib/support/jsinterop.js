exports.index = function(l, i) {
    return l[i];
}

exports._length = function(l) {
    return l.length;
}

exports.bufferToInteger = function(b) {
    var l = b.length;
    var n = 0;
    for (var i = 0; i < b.length; i++) {
        n *= 256;
        n += b[i];
    }
    return n;
}
