#include <string>

using namespace std;

#ifndef WYVERN_GLOBALS_H
#define WYVERN_GLOBALS_H

#define TYPE_BOOLEAN string ("bool")
#define TYPE_INT string ("int")
#define TYPE_RATIONAL string ("rational")
#define TYPE_STRING "string"

string getIfCondName ();
string getThenBBName ();
string getElseBBName ();
string getMergeBBName ();
string getIfTmpName ();
string getConstantString ();
extern "C"{
unsigned long __allocWyvernObject (unsigned int size, unsigned int classID);
}
#endif /* WYVERN_GLOBALS_H */
