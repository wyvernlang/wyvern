const sprintfjs = require("sprintf-js");

exports.testEqual = function(s1, s2) {
    return s1 === s2;
}

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
