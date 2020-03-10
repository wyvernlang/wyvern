exports.doMatch = function(s,r) {
    var matchInfo = s.match(r);
    var found = !(matchInfo === null) && matchInfo.index === 0;
    var matched = "";
    var after = s;
    if (found) {
        matched = matchInfo[0];
        after = s.substring(matched.length);
    }
    return {found:found, matched:matched, after:after};
}

exports.RegExp = function(s) { return new RegExp(s); }