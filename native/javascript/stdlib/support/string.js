const sprintfjs = require("sprintf-js");

exports.ofInt = function(x) {
    return x.toString();
}

exports.ofFloat = function(x) {
    return x.toString();
}

exports.ofFormattedFloat = function(format, f) {
    return sprintfjs.sprintf(format, f);
}

exports.ofCharacter = function(c) {
    return c;
}

exports.fromCharCode = function(i) {
    return String.fromCharCode(i);
}

exports.replace = function(string, s1, s2) {
    return string.replace(s1, s2);
}
