Wyvern Examples
===============

pong/           a pong game implemented in Wyvern on the Python platform
rosetta/        simple example programs in Wyvern from http://rosettacode.org
capabilities/   an example illustrating capability-based module systems
tsls/           an example illustrating type-specific languages

To run the examples on the Java platform, from the current directory "examples" execute the command:

wyvern rosetta/hello.wyv

(or similar for other examples)


To run the pong example, which requires python, see the readme.txt file in the pong subdirectory.

For more examples, look in the Wyvern standard library (../stdlib).  Some good examples there include:
 * wyvern/option.wyv - an option datatype
 * wyvern/internal/list.wyv - parameterized linked lists
 * prelude.wyv - the standard Wyvern prelude, automatically included before every Wyvern program
 * wyvern/util/matching/regexp.wyv - a regular expression library
 * platform/java/stdout.wyv - the Java standard output library
 * platform/python/stdout.wyv - the Python standard output library
 