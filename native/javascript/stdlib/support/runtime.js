exports.fail = function(description) {
    throw new Error(description);
};

exports.log = function(s) {
    console.log(s);
};