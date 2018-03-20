#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/TypeBuilder.h"

#include <vector>
#include <string>
#include <map>

#ifndef WYVERN_FUNCTION_H
#define WYVERN_FUNCTION_H

using namespace llvm;
using namespace std;

extern "C" {
class WyvernFunction
{
    private:
        std::map<std::string, Value*> namedValues;
        std::map<std::string, BasicBlock*> strBasicBlockMap;
        FunctionType *functionType;
        Function *llvmFunction;
        vector<Type*> typeArgs;
        vector<string> nameArgs;
        Type* returnType;
        bool isVarArg;
        std::string name;
        Module* module;

    public:
        WyvernFunction (Type* returnType, vector<Type*> argsType, vector<string> nameArgs,
                        bool isVarArg, std::string name, Module* module);
        
        AllocaInst* CreateAlloca (string type,
                                  const std::string &VarName);
        AllocaInst* CreateAlloca (Type* type, 
                                  const string &VarName);
        void setNamedValue (string name, Value *value)
        {
            namedValues[name] = value;
        }
        
        Value* getNamedValue (string name);
        Value* getNamedValueWithoutLoad (string name);
        void setBasicBlockForString (string name, BasicBlock *value)
        {
            strBasicBlockMap [name] = value;
        }
        
        BasicBlock* getBasicBlockForString (string name)
        {
            return strBasicBlockMap[name];
        }
        
        Function* getFunction ()
        {
            return llvmFunction;
        }
        
        string getName ()
        {
            return name;
        }
        
        virtual ~WyvernFunction ();
};
}
#endif /* WYVERN_FUNCTION_H */