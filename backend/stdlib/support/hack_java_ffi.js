exports.assertion = function(description, expression) {
    if (!expression) {
        console.trace();
        throw description;
    }
}

exports.assertionFail = function(description) {
    exports.assertion(description, false);
}
