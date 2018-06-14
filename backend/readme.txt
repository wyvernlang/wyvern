bytecode.proto contains the bytecode protobuf definition. It has comments and
should be laid out to correspond with the bytecode spec. 

proto_example contains an example javascript program which writes some simple
bytecode to the disk. Check out proto_example/src/main.js. Because the
javascript protobuf.js library loads protobuf definitions dynamically, no build
step is necessary.

To run:
$ cd proto_example
$ npm install # one time
$ node src/main.js

If you're curious about the protobuf toolchain and code generation, you can
install protobuf using your package manager and run the following command to
autogenerate java code into the current directory:

$ protoc bytecode.proto --java_out=.
