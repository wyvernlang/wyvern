#include "WyvernClass.h"
#include "WyvernGlobals.h"

#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/TypeBuilder.h"

extern LLVMContext &Context;
extern Module *TheModule;

WyvernClass::WyvernClass (string className, string selfName)
{
    this->className = className;
    this->selfName = selfName;
    classType = StructType::create (Context, className);
    fieldTypes.push_back (Type::getInt32Ty (Context));
}

WyvernClass::~WyvernClass ()
{}