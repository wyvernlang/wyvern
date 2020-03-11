const moo = require("moo");

exports.makeLexer = function (lexerSource) {
    // TODO: for security, should parse lexerSource to ensure it is a valid JavaScript literal (including embedded regular expressions - so can't just use a JSON or JSON5 reader)
    return moo.compile(eval('({' + lexerSource + '})'));
}
