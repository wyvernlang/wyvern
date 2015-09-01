#include "WyvernFunction.h"
#include "WyvernGlobals.h"

#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/TypeBuilder.h"

#include <iostream>

extern map <string, Type*> strTypeMap;
extern IRBuilder<> Builder;
extern LLVMContext &Context;

WyvernFunction::WyvernFunction (Type* returnType, vector<Type*> typeArgs, 
                                vector<string> nameArgs, bool isVarArg, 
                                std::string name, Module* module)
{
    this->returnType = returnType;
    this->isVarArg = isVarArg;
    this->name = name;
    this->module = module;
    
    if (returnType->isStructTy ())
    {
        functionType = FunctionType::get (PointerType::getUnqual (returnType),
                                          vector<Type*> (), isVarArg);
    }
    else
    {
        functionType = FunctionType::get (returnType,
                                          vector<Type*> (), isVarArg);
    }
    
    llvmFunction = Function::Create (functionType, Function::ExternalLinkage, 
                                     name, module);
    IRBuilder<> TmpB(&llvmFunction->getEntryBlock(),
                     llvmFunction->getEntryBlock().begin());

    for (uint64_t i = 0; i < typeArgs.size (); i++)
    {
        this->typeArgs.push_back (typeArgs[i]);
        this->nameArgs.push_back (nameArgs[i]);

        /* As type of all arguments are interface so, 
         * create a pointer for all */
        AllocaInst* inst =  TmpB.CreateAlloca(PointerType::getUnqual (typeArgs[i]));
        namedValues[nameArgs[i]] = inst;
    }
}

AllocaInst *WyvernFunction::CreateAlloca (Type* type, 
                                          const string &VarName)
{    
    IRBuilder<> TmpB(&llvmFunction->getEntryBlock(),
                     llvmFunction->getEntryBlock().begin());
    AllocaInst *allocaInst;

    if (namedValues[VarName] != NULL)
        return static_cast<AllocaInst*> (namedValues[VarName]);
        
    allocaInst = TmpB.CreateAlloca(type, 0, VarName);
    
    setNamedValue (VarName, allocaInst);
    
    return allocaInst;
}

AllocaInst *WyvernFunction::CreateAlloca (const string type, 
                                          const string &VarName)
{    
    IRBuilder<> TmpB(&llvmFunction->getEntryBlock(),
                     llvmFunction->getEntryBlock().begin());
    AllocaInst *allocaInst;

    if (type == TYPE_BOOLEAN)
    {
        allocaInst = TmpB.CreateAlloca(Type::getInt1Ty(getGlobalContext()), 0,
                                       VarName.c_str());
    }
    else if (type == TYPE_INT)
    {
        allocaInst = TmpB.CreateAlloca(Type::getInt32Ty(getGlobalContext()), 0,
                                       VarName.c_str());
    }
    else if (type == TYPE_RATIONAL)
    {
        allocaInst = TmpB.CreateAlloca(Type::getDoubleTy(getGlobalContext()), 0,
                                       VarName.c_str());
    }
    else if (type == TYPE_STRING)
    {
        /* TODO Add String type also. It depends how string will be represented
         * inside Wyvern. If it is array of characters then Type will be i8*
         * or it may be a class having an array and length like std::string 
         */
        allocaInst = TmpB.CreateAlloca(Type::getInt32Ty(getGlobalContext()), 0,
                                       VarName.c_str());
    }
    else
    {
        /* This is an object */
        Type* _type;
        
        _type = strTypeMap [type];
        allocaInst = TmpB.CreateAlloca(PointerType::getUnqual (_type), 0,
                                       VarName.c_str());
    }
    
    setNamedValue (VarName, allocaInst);
    
    return allocaInst;
}

WyvernFunction::~WyvernFunction ()
{
}

Value* WyvernFunction::getNamedValue (string name)
{
    Value* value;
    
    value = namedValues[name];
    
    if (dynamic_cast<AllocaInst*> (value) == NULL)
        return value;
        
    return Builder.CreateLoad (value);
}

Value* WyvernFunction::getNamedValueWithoutLoad (string name)
{   
    return namedValues[name];
}