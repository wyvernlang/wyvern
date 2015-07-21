#include <string>

using namespace std;

#ifndef WYVERN_GLOBALS_H
#define WYVERN_GLOBALS_H

/* Types */
#define TYPE_BOOLEAN string ("bool")
#define TYPE_INT string ("int")
#define TYPE_RATIONAL string ("rational")
#define TYPE_STRING string("string")

/* Arithmatic Operations */
#define ARITH_ADD string ("add")
#define ARITH_SUBTRACT string ("subtract")
#define ARITH_MULTIPLY string ("multiply")
#define ARITH_DIVIDE string ("divide")
#define ARITH_MODULO string ("modulo")

/* Unary Operator */
#define UNARY_NEGATE string ("negate")

/* Logical Operator */
#define LOGICAL_NOT string ("logical_not")
#define LOGICAL_AND string ("logical_and")
#define LOGICAL_OR string ("logical_or")

/* Bitwise Operators */
#define BITWISE_AND string ("bitwise_and")
#define BITWISE_OR string ("bitwise_or")
#define BITWISE_XOR string ("bitwise_xor")
#define BITWISE_LEFT_SHIFT string ("bitwise_left_shift")
#define BITWISE_RIGHT_SHIFT string ("bitwise_right_shift")

#define METHOD_CALL_ADDRESS string("__method_call_address__")
#define METHOD_CALL_OBJ_ADDRESS string("__method_call_obj_address")

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
