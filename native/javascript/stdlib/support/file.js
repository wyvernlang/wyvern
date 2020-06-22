const fs = require("fs"); // https://nodejs.org/api/fs.html

exports.readFileAsString = function(fileName) {
    var contents = fs.readFileSync(fileName, 'utf8');
    return contents;
}
