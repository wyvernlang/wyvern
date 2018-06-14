const protobuf = require("protobufjs");

const fs = require("fs");

protobuf.load("../bytecode.proto", function(err, root) {
    if (err) throw err;
    const Bytecode = root.lookupType('Bytecode');

    const bytecodeData = {
        version: {magic: 42, major: 0, minor: 1},
        path: "com.example",
        imports: [],
        modules: [{
            path: "HelloWorld",
            valueModule: {
                type: {
                    simpleType: root.lookupEnum("Type.SimpleType").values.Top
                },
                expression: {
                    literal: {
                        stringLiteral: "Hello, world!"
                    },
                },
            },
        }],
    };

    // Will throw if any of the required members are missing
    err = Bytecode.verify(bytecodeData);
    if (err) throw err;

    var buffer = Bytecode.encode(bytecodeData).finish();

    // Write it out
    fs.writeFileSync("bytecode", buffer);

    // Read it back
    var bytes = fs.readFileSync("bytecode");
    var bytecode = Bytecode.decode(bytes);
    console.log(bytecode.modules[0].valueModule.expression.literal.stringLiteral);
});
