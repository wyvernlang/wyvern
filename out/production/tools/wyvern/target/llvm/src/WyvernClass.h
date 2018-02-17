#include <vector>

#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/TypeBuilder.h"

#ifndef WYVERN_CLASS_H
#define WYVERN_CLASS_H

using namespace std;
using namespace llvm;

class WyvernClass
{
    private:
        vector<Type*> fieldTypes;
        string className;
        string selfName;
        StructType* classType;
        
    public:
        WyvernClass (string className, string selfName);
        
        virtual ~WyvernClass ();
        
        StructType* getClassType ()
        {
            return classType;
        }
        
        void addFieldType (Type* fieldType)
        {
            fieldTypes.push_back (fieldType);
        }
    
        void addFieldTypesToClassType ()
        {
            classType->setBody (fieldTypes, false);
        }
    
        string getClassName ()
        {
            return className;
        }
    
        string getSelfName ()
        {
            return selfName;
        }
};

#endif /* WYVERN_CLASS_H */