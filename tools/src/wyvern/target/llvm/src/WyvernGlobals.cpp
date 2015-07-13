#include "WyvernGlobals.h"

#include "llvm/Analysis/Passes.h"
#include "llvm/ExecutionEngine/ExecutionEngine.h"
#include "llvm/ExecutionEngine/MCJIT.h"
#include "llvm/ExecutionEngine/SectionMemoryManager.h"
#include "llvm/IR/DataLayout.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/LegacyPassManager.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Verifier.h"
#include "llvm/Support/TargetSelect.h"
#include "llvm/Transforms/Scalar.h"
#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/TypeBuilder.h"

#include <jni.h>

/* TODO: Conversion of String
 * Cast Expression
 * Interface 
 */

using namespace std;
using namespace llvm;

LLVMContext &Context = getGlobalContext();
Module *TheModule = new Module("wyvern", Context);
IRBuilder<> Builder(getGlobalContext());
map <string, Type*> strTypeMap;

static int ifElseThenCount = 0;

string getIfCondName ()
{
    ifElseThenCount ++;

    return "ifcond" + to_string (ifElseThenCount);    
}

string getThenBBName ()
{
    ifElseThenCount ++;
    return "then" + to_string (ifElseThenCount);    
}

string getElseBBName ()
{
    ifElseThenCount ++;
    return "else" + to_string (ifElseThenCount);    
}

string getMergeBBName ()
{
    ifElseThenCount ++;
    return "ifCont" + to_string (ifElseThenCount);    
}

string getIfTmpName ()
{
    ifElseThenCount ++;
    return "ifTmp"+to_string (ifElseThenCount);
}

string getConstantString ()
{
    static int num = 0;
    string toReturn;
    
    num += 1;
    toReturn = "constant" + to_string (num);
    
    return toReturn;    
}