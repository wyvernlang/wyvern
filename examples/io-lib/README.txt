</examples/io-lib>
This directory contains a simple server client example. When the two are connected, then the
server sends the client a string, which the client prints.

Instructions on running the example:

=== Library Files ===
The following are files that are needed to support the IO library used in this example:
/stdlib/platform/java/io.wyv
/tools/src/wyvern/stdlib/support/IO.java


=== Usage or Running the Example ===
From the command line, in the /examples/io-lib/ directory, run the server, then the client.
These must each be run from different terminal windows/tabs.

1. wyvern server.wyv
    After running this, the program should print in the terminal "Waiting for a client connection..."
2. wyvern client.wyv
    If this was successful the program should output the message provided by the server and both programs
    should terminate.