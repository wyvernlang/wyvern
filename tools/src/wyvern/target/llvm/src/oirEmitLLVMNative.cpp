/*
 * oirEmitLLVMNative.cpp
 *
 *  Created on: Jul 2, 2015
 *      Author: abhi
 */
#include <iostream>
#include <string>
#include <vector>
#include <stdlib.h>
#include <unordered_set>
#include <unordered_map>

#include "wyvern_target_oir_EmitLLVMNative.h"
#include "WyvernFunction.h"
#include "WyvernGlobals.h"
#include "WyvernClass.h"
#include "wyvern_target_oir_DelegateNative.h"
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

/* TODO: Free the method C strings passed to getWyvernFunction
 * */
/* NOTE: When getting field position either using StructLayout in LLVM or
 * by using OIR. Do not add 1 to the returned value
 */
using namespace std;

extern LLVMContext &Context;
extern Module *TheModule;
extern IRBuilder<> Builder;
extern map<string, Type*> strTypeMap;
extern unordered_set <string> arithOpsSet;

static WyvernFunction* currentFunction;
static WyvernClass* currentClass;

jclass oirProgramClass;
jobject oirProgramObject;
JNIEnv* globalJNIEnv;
ExecutionEngine* execEngine;


static string jstringToString (JNIEnv *jnienv, jstring javastring)
{
    const char *cStr;
    string n;
    
    cStr = (jnienv)->GetStringUTFChars (javastring, NULL);
    n = cStr;
    jnienv->ReleaseStringUTFChars (javastring, cStr);
    
    return n;
}

#ifdef __cplusplus
extern "C"
{
#endif
unsigned long __allocWyvernObject (unsigned int size, unsigned int classID)
{
    void* obj;
    
    obj = malloc(size);
    for (unsigned int i = 0; i < size; i++)
    {
        ((char *)obj)[i] = 0;
    }

    ((int*)obj)[0] = classID;
    
    return (unsigned long)obj;
}

uint64_t getWyvernFunction (uint64_t obj, uint64_t methodNameAddress,
                            uint64_t callSiteID, uint64_t* addressToPass)
{
    char* methodName = (char *)methodNameAddress;
    jmethodID methodID;
    jfieldID fieldID;
    jstring jClassName;
    string className;
    string jitMethodName;
    jobject methodAddress;
    jclass javaclass;

    oirProgramClass = globalJNIEnv->FindClass ("wyvern/target/oir/OIRProgram");
    fieldID = globalJNIEnv->GetStaticFieldID (oirProgramClass, "program", 
                                              "Lwyvern/target/oir/OIRProgram;");
    oirProgramObject = globalJNIEnv->GetStaticObjectField (oirProgramClass, fieldID);
    methodID = globalJNIEnv->GetMethodID (oirProgramClass, "getClassNameForCallSite", 
                                          "(JIILjava/lang/String;)Lwyvern/target/oir/MethodAddress;");
    methodAddress = globalJNIEnv->CallObjectMethod (oirProgramObject,
                                                      methodID,
                                                      obj,
                                                      ((int *)obj)[0],
                                                      callSiteID,
                                                      globalJNIEnv->NewStringUTF (methodName));
    javaclass = globalJNIEnv->GetObjectClass (methodAddress);
    methodID = globalJNIEnv->GetMethodID (javaclass, "getClassName",
                                          "()Ljava/lang/String;");
    jClassName = (jstring)globalJNIEnv->CallObjectMethod (methodAddress, methodID);
    className = jstringToString (globalJNIEnv, jClassName);
    jitMethodName = className + string ("_") + string (methodName);
    methodID = globalJNIEnv->GetMethodID (javaclass, "getObjectAddress",
                                          "()J");
    *addressToPass = globalJNIEnv->CallIntMethod (methodAddress, methodID);

    return execEngine->getFunctionAddress (string (jitMethodName));
}

uint64_t getFieldForObject (uint64_t objectAddr, uint64_t fieldNameAddr,
                            uint64_t fieldTypeSize)
{
    char* fieldName;
    jmethodID methodID;
    jfieldID fieldID;
    jstring jClassName;
    string className;
    string jitMethodName;
    int fieldPos;
    Type* structType;
    const DataLayout* dataLayout;
    const StructLayout* structLayout;
    int offset;
    
    fieldName = (char *) fieldNameAddr;
    oirProgramClass = globalJNIEnv->FindClass ("wyvern/target/oir/OIRProgram");
    fieldID = globalJNIEnv->GetStaticFieldID (oirProgramClass, "program", 
                                              "Lwyvern/target/oir/OIRProgram;");
    oirProgramObject = globalJNIEnv->GetStaticObjectField (oirProgramClass, 
                                                           fieldID);
    methodID = globalJNIEnv->GetMethodID (oirProgramClass, "getFieldPositionInClass", 
                                          "(ILjava/lang/String;)I");
    fieldPos = globalJNIEnv->CallIntMethod (oirProgramObject, 
                                            methodID,
                                            ((int *)objectAddr)[0],
                                            fieldName);
    methodID = globalJNIEnv->GetMethodID (oirProgramClass, "getClassName", 
                                          "(I)Ljava/lang/String;");
    jClassName = (jstring)globalJNIEnv->CallObjectMethod (oirProgramObject, 
                                                          methodID, 
                                                          ((int *)objectAddr)[0]);
    className = jstringToString (globalJNIEnv, jClassName);
    
    structType = strTypeMap [className];
    
    if (structType->isStructTy () == false)
    {
        printf ("Error accessing field %d: StructType is not valid for class %s\n",
                fieldPos, className.c_str ());
        return 0;
    }
    
    dataLayout = TheModule->getDataLayout ();
    structLayout = dataLayout->getStructLayout ((StructType*)structType);
    /* Add 1 because first field always represent the type */
    fieldPos += 1; 
    offset = structLayout->getElementOffset (fieldPos);
    
    if (fieldTypeSize == 1)
    {
        /* Boolean */
        return ((unsigned char *)objectAddr + offset)[0];
    }
    else if (fieldTypeSize == 4)
    {
        /* Integer */
        return ((int *)((unsigned char *)objectAddr + offset))[0];
    }
    
    /* Else it will be of size 8, which could be double or pointer */
    return ((unsigned long *)((unsigned char *)objectAddr + offset))[0];
}

void setFieldForObject (uint64_t objectAddr, uint64_t fieldNameAddr,
                        uint64_t fieldTypeSize, uint64_t value)
{
    char* fieldName;
    jmethodID methodID;
    jfieldID fieldID;
    jstring jClassName;
    string className;
    string jitMethodName;
    int fieldPos;
    Type* structType;
    const DataLayout* dataLayout;
    const StructLayout* structLayout;
    int offset;
    
    fieldName = (char *) fieldNameAddr;
    oirProgramClass = globalJNIEnv->FindClass ("wyvern/target/oir/OIRProgram");
    fieldID = globalJNIEnv->GetStaticFieldID (oirProgramClass, "program", 
                                              "Lwyvern/target/oir/OIRProgram;");
    oirProgramObject = globalJNIEnv->GetStaticObjectField (oirProgramClass, 
                                                           fieldID);
    methodID = globalJNIEnv->GetMethodID (oirProgramClass, "getFieldPositionInClass", 
                                          "(ILjava/lang/String;)I");
    fieldPos = globalJNIEnv->CallIntMethod (oirProgramObject, 
                                            methodID,
                                            ((int *)objectAddr)[0],
                                            fieldName);
    methodID = globalJNIEnv->GetMethodID (oirProgramClass, "getClassName", 
                                          "(I)Ljava/lang/String;");
    jClassName = (jstring)globalJNIEnv->CallObjectMethod (oirProgramObject, 
                                                          methodID, 
                                                          ((int *)objectAddr)[0]);
    className = jstringToString (globalJNIEnv, jClassName);
    
    structType = strTypeMap [className];
    
    if (structType->isStructTy () == false)
    {
        printf ("Error accessing field %d: StructType is not valid for class %s\n",
                fieldPos, className.c_str ());
        return;
    }
    
    dataLayout = TheModule->getDataLayout ();
    structLayout = dataLayout->getStructLayout ((StructType*)structType);
    /* Add 1 because first field always represent the type */
    fieldPos += 1; 
    offset = structLayout->getElementOffset (fieldPos);
    
    if (fieldTypeSize == 1)
    {
        /* Boolean */
        ((unsigned char *)objectAddr + offset)[0] = (char)value;
        return;
    }
    else if (fieldTypeSize == 4)
    {
        /* Integer */
        ((int *)((unsigned char *)objectAddr + offset))[0] = (int)value;
        return;
    }
    
    /* Else it will be of size 8, which could be double or pointer */
    ((unsigned long *)((unsigned char *)objectAddr + offset))[0] = value;
}

#ifdef __cplusplus
}
#endif

FunctionType* allocObjFuncType = FunctionType::get (Type::getInt64Ty (Context),
                                                    vector<Type*> (2, Type::getInt32Ty (Context)), false);
FunctionType* getWyvernFunctionFuncType = FunctionType::get (Type::getInt64Ty (Context),
                                                    vector<Type*> (3, Type::getInt64Ty (Context)), false);

FunctionType* getFieldForObjectFunctionType = FunctionType::get (Type::getInt64Ty (Context),
                                                                 vector<Type*> (3, Type::getInt64Ty (Context)), false);
FunctionType* setFieldForObjectFunctionType = FunctionType::get (Type::getVoidTy (Context),
                                                                 vector<Type*> (4, Type::getInt64Ty (Context)), false);
Value* GetFunctionToCall (unsigned long address, FunctionType* funcType)
{
    /* This function takes the address of the function. cast it to 
     * function type. and call the function
     * I know it is a hack but there is no other way because of 
     * C++ name mangling */
    Value* iv;
    static int castNum = 0;
    Value *castInst;
    
    castNum++;
    iv = ConstantInt::get (Type::getInt64Ty(getGlobalContext()), address, 
                           true);
    castInst = CastInst::CreatePointerCast (iv, 
                                            PointerType::getUnqual (funcType),
                                            "funcPtrCast" + to_string(castNum),
                                             Builder.GetInsertBlock());
    return castInst;
}

static Function *printf_prototype(LLVMContext &ctx, Module *mod) {

FunctionType* printf_type = FunctionType::get (Type::getInt32Ty (Context),
                                      vector<Type*> (), false);

  Function *func = Function::Create (printf_type, Function::ExternalLinkage, 
                                     "__allocWyvernObject", mod);
  return func;
}

Function* allocObject_func = printf_prototype (Context, TheModule);
static unsigned long allocObjAddress = (unsigned long)(void*)__allocWyvernObject;

Constant* geti8StrVal(Module& M, char const* str, Twine const& name) {
  LLVMContext& ctx = getGlobalContext();
  Constant* strConstant = ConstantDataArray::getString(ctx, str);
  GlobalVariable* GVStr =
      new GlobalVariable(M, strConstant->getType(), true,
                         GlobalValue::InternalLinkage, strConstant, name);
  Constant* zero = Constant::getNullValue(IntegerType::getInt32Ty(ctx));
  Constant* indices[] = {zero, zero};
  Constant* strVal = ConstantExpr::getGetElementPtr(GVStr, indices, true);
  return strVal;
}

/*
 * Class:     wyvern_target_oir_DelegateNative
 * Method:    getFieldAddress
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_wyvern_target_oir_DelegateNative_getFieldAddress
  (JNIEnv *jnienv, jclass javaclass, jstring jClassName, jlong objectAddress, jlong fieldPos)
{
    Type* structType;
    int offset;
    string className;
    const DataLayout* dataLayout;
    const StructLayout* structLayout;
        
    className = jstringToString (jnienv, jClassName);
    structType = strTypeMap [className];
    
    if (structType->isStructTy () == false)
    {
        printf ("Error accessing field %ld: StructType is not valid for class %s\n",
                fieldPos, className.c_str ());
        return -1;
    }
    
    dataLayout = TheModule->getDataLayout ();
    structLayout = dataLayout->getStructLayout ((StructType*)structType);
    offset =  structLayout->getElementOffset (fieldPos);

    return ((uint64_t *)((unsigned char *)objectAddress + offset))[0];
}

/*
 * Class:     wyvern_target_oir_DelegateNative
 * Method:    getObjectClassID
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_wyvern_target_oir_DelegateNative_getObjectClassID
  (JNIEnv *jnienv, jclass javaclass, jlong objectAddress)
{
    return ((int*)objectAddress)[0];
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    letToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRLet;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_letToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject, jstring valueString, jstring typeStr)
{
    Value *value;
    const char *cStr;
    string n;
    AllocaInst *local;
    jmethodID varNameFID;
    jstring varName ;
    
    cStr = (jnienv)->GetStringUTFChars (valueString, NULL);
    n = cStr;
    jnienv->ReleaseStringUTFChars (valueString, cStr);
    value = currentFunction->getNamedValue (n);
    javaclass = jnienv->GetObjectClass (javaobject);
    varNameFID = (jnienv)->GetMethodID (javaclass, "getVarName", 
                                        "()Ljava/lang/String;");
    varName = (jstring)(jnienv)->CallObjectMethod (javaobject, 
                                                   varNameFID);
    cStr = (jnienv)->GetStringUTFChars (varName, NULL);
    n = cStr;
    jnienv->ReleaseStringUTFChars (varName, cStr);
    local = currentFunction->CreateAlloca (jstringToString (jnienv, typeStr), 
                                           n);
    Builder.CreateStore (value, local);

    return varName;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    integerToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRInteger;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_integerToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject)
{
    Value *value;
    string n;
    jmethodID valueIntFID;
    jint valueInt;
    
    valueInt = 0;
    javaclass = jnienv->GetObjectClass (javaobject);

    valueIntFID = jnienv->GetMethodID (javaclass, "getValue", "()I");
    valueInt = jnienv->CallIntMethod (javaobject, valueIntFID);
    
    value = ConstantInt::get (Type::getInt32Ty(getGlobalContext()),
                              valueInt, true);
    n = getConstantString ();
    currentFunction->setNamedValue (n, value);

    return jnienv->NewStringUTF(n.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    booleanToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRBoolean;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_booleanToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject)
{
    Value *value;
    string n;
    jmethodID valueIntFID;
    jint valueInt;
    
    valueInt = 0;
    javaclass = jnienv->GetObjectClass (javaobject);

    valueIntFID = jnienv->GetMethodID (javaclass, "getValue", "()I");
    valueInt = jnienv->CallIntMethod (javaobject, valueIntFID);
    
    value = ConstantInt::get (Type::getInt1Ty(getGlobalContext()),
                              valueInt, true);
    n = getConstantString ();
    currentFunction->setNamedValue (n, value);

    return jnienv->NewStringUTF(n.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    ifCondExprToLLVMIR
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_ifCondExprToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring strCondExpr)
{
    Value *condV;
    string strCond;
    
    strCond = getIfCondName ();
    condV = currentFunction->getNamedValue (jstringToString (jnienv, strCondExpr));
    condV = Builder.CreateICmpEQ (condV, 
                                  ConstantInt::get (Type::getInt32Ty(getGlobalContext()), 0, true),
                                  strCond);
    ((WyvernFunction* )currentFunction)->setNamedValue (strCond, condV);
    
    return jnienv->NewStringUTF(strCond.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    createThenBasicBlock
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_createThenBasicBlock
  (JNIEnv *jnienv, jclass javaclass)
{
    BasicBlock *thenBB;
    Function *TheFunction;
    string strThenBB;
    
    TheFunction = Builder.GetInsertBlock()->getParent ();
    
    strThenBB = getThenBBName ();
    thenBB = BasicBlock::Create (getGlobalContext (), strThenBB, TheFunction);
    currentFunction->setBasicBlockForString (strThenBB, thenBB);
    
    return jnienv->NewStringUTF(strThenBB.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    createElseBasicBlock
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_createElseBasicBlock
  (JNIEnv *jnienv, jclass javaclass)
{
    BasicBlock *elseBB;
    string strElseBB;
        
    strElseBB = getElseBBName ();
    elseBB = BasicBlock::Create (getGlobalContext (), strElseBB);
    currentFunction->setBasicBlockForString (strElseBB, elseBB);
    
    return jnienv->NewStringUTF(strElseBB.c_str());
}
/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    createMergeBasicBlock
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_createMergeBasicBlock
  (JNIEnv *jnienv, jclass, jstring strCondExpr, jstring strThenBB, jstring strElseBB)
{
    BasicBlock *mergeBB;
    string strMergeBB;
        
    strMergeBB = getMergeBBName ();
    mergeBB = BasicBlock::Create (getGlobalContext (), strMergeBB);
    currentFunction->setBasicBlockForString (strMergeBB, mergeBB);
    
    Builder.CreateCondBr (currentFunction->getNamedValue (jstringToString (jnienv, strCondExpr)),
                          currentFunction->getBasicBlockForString (jstringToString (jnienv, strThenBB)),
                          currentFunction->getBasicBlockForString (jstringToString (jnienv, strElseBB)));
    return jnienv->NewStringUTF(strMergeBB.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    setupThenBasicBlockEmit
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_setupThenBasicBlockEmit
  (JNIEnv *jnienv, jclass javaclass, jstring strThenBB)
{
    Builder.SetInsertPoint (
        currentFunction->getBasicBlockForString (jstringToString (jnienv, strThenBB)));
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    emitThenBasicBlock
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_emitThenBasicBlock
  (JNIEnv *jnienv, jclass javaclass, jstring strThenExpr, jstring strThenBB, jstring strMergeBB)
{
    Builder.CreateBr (
        ((WyvernFunction* )currentFunction)->getBasicBlockForString (jstringToString (jnienv, strMergeBB)));
    ((WyvernFunction* )currentFunction)->setBasicBlockForString (jstringToString (jnienv, strThenBB), 
                                             Builder.GetInsertBlock ());

    return strThenBB;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    setupElseBasicBlockEmit
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_setupElseBasicBlockEmit
  (JNIEnv *jnienv, jclass javaclass, jstring strElseBB)
{
    ((WyvernFunction* )currentFunction)->getFunction()->getBasicBlockList ().push_back (
        ((WyvernFunction* )currentFunction)->getBasicBlockForString (jstringToString (jnienv, strElseBB)));
    Builder.SetInsertPoint (
        ((WyvernFunction* )currentFunction)->getBasicBlockForString (jstringToString (jnienv, strElseBB)));
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    emitElseBasicBlock
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_emitElseBasicBlock
  (JNIEnv *jnienv, jclass javaclass, jstring strElseExpr, jstring strElseBB, jstring strMergeBB)
{
    Builder.CreateBr (
        ((WyvernFunction* )currentFunction)->getBasicBlockForString (jstringToString (jnienv, strMergeBB)));
    ((WyvernFunction* )currentFunction)->setBasicBlockForString (jstringToString (jnienv, strElseBB),
                                             Builder.GetInsertBlock ());
    
    return strElseBB;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    emitMergeBasicBlock
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_emitMergeBasicBlock
  (JNIEnv *jnienv, jclass javaclass, jstring strMergeBB, jstring strThenExpr, 
   jstring strThenBB, jstring strElseExpr, jstring strElseBB)
{
    PHINode* PN;
    string strIfTmpName;
    Function* function;
    BasicBlock* mergeBB;
    BasicBlock* thenBB;
    BasicBlock* elseBB;
    Value* thenValue;
    Value* elseValue;

    function = currentFunction->getFunction();
    mergeBB = currentFunction->getBasicBlockForString (
        jstringToString (jnienv, strMergeBB));
    strIfTmpName = getIfTmpName ();
    function->getBasicBlockList().push_back (mergeBB);
    Builder.SetInsertPoint (mergeBB);
    PN = Builder.CreatePHI (Type::getInt32Ty (getGlobalContext ()), 2,
                            strIfTmpName);
    thenValue = currentFunction->getNamedValue (
        jstringToString (jnienv, strThenExpr));
    thenBB = currentFunction->getBasicBlockForString (
        jstringToString (jnienv, strThenBB));
    PN->addIncoming (thenValue, thenBB);

    elseValue = currentFunction->getNamedValue (
        jstringToString (jnienv, strElseExpr));
    elseBB = currentFunction->getBasicBlockForString (
        jstringToString (jnienv, strElseBB));
    PN->addIncoming (elseValue, elseBB);
    
    currentFunction->setNamedValue (strIfTmpName, PN);

    return jnienv->NewStringUTF(strIfTmpName.c_str());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    literalToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRLiteral;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_literalToLLVMIR
  (JNIEnv *, jclass, jobject);

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    createMainFunction
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_createMainFunction
  (JNIEnv *jnienv, jclass javaclass)
{
    BasicBlock *BB;
    WyvernFunction *mainFunction;
    
    mainFunction = new WyvernFunction (Type::getInt32Ty(getGlobalContext()),
                                       vector<Type*> (), vector<string> (),
                                       false, "main", TheModule);
    currentFunction = mainFunction;
    BB = BasicBlock::Create(getGlobalContext(), "entry", 
                            currentFunction->getFunction ());
    Builder.SetInsertPoint(BB);
    
    /*IRBuilder<> TmpB(&currentFunction->getFunction()->getEntryBlock(),
                     currentFunction->getFunction()->getEntryBlock().begin());
    AllocaInst *allocaInst;
    allocaInst = TmpB.CreateAlloca(currentClass->getClassType(), 0,
                                       "fdf");*/
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    functionCreated
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_functionCreated
  (JNIEnv *jnienv, jclass javaclass, jstring jToReturnName)
{
    Value *toReturn;
    
    toReturn = currentFunction->getNamedValue (
        jstringToString (jnienv, jToReturnName));
    Builder.CreateRet (toReturn);
   
    if (currentFunction->getName () == "main")
    {
        TheModule->dump ();
    }
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    fieldSetToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRFieldSet;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_fieldSetToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jValueName, jstring jObjTypeName, 
   jstring jObjName, jstring jFieldName, jstring jFieldTypeName)
{
    string objTypeName;
    string objName;
    string fieldName;
    string fieldTypeName;
    string fieldSetName;
    string valueToSetName;
    Value* object;
    Type* fieldType;
    vector<Value*> stubArgs;
    char* argFieldName;
    static int fieldGetNum = 0;
    Value* valueToSet;
    
    fieldGetNum++;
    valueToSetName = jstringToString (jnienv, jValueName);
    objTypeName = jstringToString (jnienv, jObjTypeName);
    objName = jstringToString (jnienv, jObjName);
    fieldName = jstringToString (jnienv, jFieldName);
    fieldTypeName = jstringToString (jnienv, jFieldTypeName);
    argFieldName = new char [fieldName.length ()];
    strcpy (argFieldName, fieldName.c_str ());
    object = currentFunction->getNamedValue (objName);
    fieldType = strTypeMap[fieldTypeName];

    if (fieldType->isStructTy ())
    {
        fieldType = PointerType::getUnqual (fieldType);
    }
    
    stubArgs.push_back (CastInst::CreateIntegerCast (object,
                                                     Type::getInt64Ty (Context),
                                                     false,
                                                     "objToIntCastGet" + to_string (fieldGetNum),
                                                     Builder.GetInsertBlock()));
    stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          (unsigned long) argFieldName, true));
    if (fieldTypeName == TYPE_BOOLEAN)
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          1, true));
    }
    else if (fieldTypeName == TYPE_INT)
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          4, true));
    }
    else
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          8, true));
    }
    
    valueToSet = currentFunction->getNamedValue (valueToSetName);
    stubArgs.push_back (CastInst::CreateIntegerCast (valueToSet,
                                                     Type::getInt64Ty (Context),
                                                     false,
                                                     "valueToIntCast" + to_string (fieldGetNum),
                                                     Builder.GetInsertBlock()));

    CallInst::Create (GetFunctionToCall ((unsigned long)setFieldForObject, 
                                                        setFieldForObjectFunctionType),
                                      stubArgs, "", 
                                      Builder.GetInsertBlock ());
    
    return jValueName;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    fieldGetToLLVMIR
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_fieldGetToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jObjTypeName, jstring jObjName, 
   jstring jFieldName, jstring jFieldTypeName)
{
    string objTypeName;
    string objName;
    string fieldName;
    string fieldTypeName;
    string fieldGetName;
    Value* object;
    Type* fieldType;
    vector<Value*> stubArgs;
    char* argFieldName;
    Value* stubRetValue;
    Value* fieldGetValue;
    static int fieldGetNum = 0;
    
    fieldGetNum++;
    objTypeName = jstringToString (jnienv, jObjTypeName);
    objName = jstringToString (jnienv, jObjName);
    fieldName = jstringToString (jnienv, jFieldName);
    fieldTypeName = jstringToString (jnienv, jFieldTypeName);
    argFieldName = new char [fieldName.length ()];
    strcpy (argFieldName, fieldName.c_str ());
    object = currentFunction->getNamedValue (objName);
    fieldType = strTypeMap[fieldTypeName];
    
    if (fieldType->isStructTy ())
    {
        fieldType = PointerType::getUnqual (fieldType);
    }
    
    stubArgs.push_back (CastInst::CreateIntegerCast (object,
                                                     Type::getInt64Ty (Context),
                                                     false,
                                                     "objToIntCastGet" + to_string (fieldGetNum),
                                                     Builder.GetInsertBlock()));
    stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          (unsigned long) argFieldName, true));
    if (fieldTypeName == TYPE_BOOLEAN)
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          1, true));
    }
    else if (fieldTypeName == TYPE_INT)
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          4, true));
    }
    else
    {
        stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          8, true));
    }

    stubRetValue = CallInst::Create (GetFunctionToCall ((unsigned long)getFieldForObject, 
                                                        getFieldForObjectFunctionType),
                                      stubArgs, objTypeName + string("_")+fieldName+to_string(fieldGetNum), 
                                      Builder.GetInsertBlock ());
    
    fieldGetName = "fieldGetCast" + to_string(fieldGetNum);
    
    if (fieldType->isStructTy ())
    {
        fieldGetValue = CastInst::CreatePointerCast (stubRetValue, 
                                                     fieldType,
                                                     fieldGetName,
                                                     Builder.GetInsertBlock());
    }
    else
    {
        fieldGetValue = CastInst::CreateTruncOrBitCast (stubRetValue, 
                                                        fieldType,
                                                        fieldGetName,
                                                        Builder.GetInsertBlock());
    }
    
    currentFunction->setNamedValue (fieldGetName, fieldGetValue);
    
    return jnienv->NewStringUTF (fieldGetName.c_str ());
}

jstring convertOperatorsForInteger (JNIEnv* jnienv, string methodName, Value* left, Value* right)
{
    string toReturn;
    static int intArith = 0;
    Value* v;
    
    intArith++;
    v = NULL;
    toReturn = "intArithResult" + to_string (intArith);
    
    if (methodName == ARITH_ADD)
    {
        v = Builder.CreateAdd (left, right);
    }
    else if (methodName == ARITH_SUBTRACT)
    {
        v = Builder.CreateSub (left, right);
    }
    else if (methodName == ARITH_MULTIPLY)
    {
        v = Builder.CreateMul (left, right);
    }
    else if (methodName == ARITH_DIVIDE)
    {
        v = Builder.CreateSDiv (left, right);
    }
    else if (methodName == ARITH_MODULO)
    {
        v = Builder.CreateSRem (left, right);
    }
    else if (methodName == UNARY_NEGATE)
    {
        v = Builder.CreateNeg (left);
    }
    else if (methodName == LOGICAL_NOT)
    {
        v = Builder.CreateNot (left);
    }
    else if (methodName == BITWISE_AND)
    {
        v = Builder.CreateAnd (left, right);
    }
    else if (methodName == BITWISE_OR)
    {
        v = Builder.CreateOr (left, right);
    }
    else if (methodName == BITWISE_XOR)
    {
        v = Builder.CreateXor (left, right);
    }
    else if (methodName == BITWISE_LEFT_SHIFT)
    {
        v = Builder.CreateShl (left, right);
    }
    else if (methodName == BITWISE_RIGHT_SHIFT)
    {
        v = Builder.CreateLShr (left, right);
    }

    if (v != NULL)
    {
        currentFunction->setNamedValue (toReturn, v);
        return jnienv->NewStringUTF (toReturn.c_str ());
    }

    return NULL;    
}

jstring convertOperatorsForBoolean (JNIEnv* jnienv, string methodName, Value* left, Value* right)
{
    string toReturn;
    static int intArith = 0;
    Value* v;
    
    intArith++;
    v = NULL;
    toReturn = "boolArithResult" + to_string (intArith);
    
    if (methodName == LOGICAL_NOT)
    {
        v = Builder.CreateNot (left);
    }
    else if (methodName == LOGICAL_AND)
    {
        v = Builder.CreateAnd (left, right);
    }
    else if (methodName == LOGICAL_OR)
    {
        v = Builder.CreateOr (left, right);
    }

    if (v != NULL)
    {
        currentFunction->setNamedValue (toReturn, v);
        return jnienv->NewStringUTF (toReturn.c_str ());
    }

    return NULL;    
}

jstring convertOperatorsForRational (JNIEnv* jnienv, string methodName, Value* left, Value* right)
{
    string toReturn;
    static int intArith = 0;
    Value* v;
    
    intArith++;
    toReturn = "intArithResult" + to_string (intArith);
    v = NULL;
    
    if (methodName == ARITH_ADD)
    {
        v = Builder.CreateFAdd (left, right);
    }
    else if (methodName == ARITH_SUBTRACT)
    {
        v = Builder.CreateFSub (left, right);
    }
    else if (methodName == ARITH_MULTIPLY)
    {
        v = Builder.CreateFMul (left, right);
    }
    else if (methodName == ARITH_DIVIDE)
    {
        v = Builder.CreateFDiv (left, right);
    }
    else if (methodName == ARITH_MODULO)
    {
        v = Builder.CreateFRem (left, right);
    }
    else if (methodName == UNARY_NEGATE)
    {
        v = Builder.CreateFNeg (left);
    }
    else if (methodName == LOGICAL_NOT)
    {
        v = Builder.CreateNot (left);
    }
    else if (methodName == BITWISE_AND)
    {
        v = Builder.CreateAnd (left, right);
    }
    else if (methodName == BITWISE_OR)
    {
        v = Builder.CreateOr (left, right);
    }
    else if (methodName == BITWISE_XOR)
    {
        v = Builder.CreateXor (left, right);
    }
    else if (methodName == BITWISE_LEFT_SHIFT)
    {
        v = Builder.CreateShl (left, right);
    }
    else if (methodName == BITWISE_RIGHT_SHIFT)
    {
        v = Builder.CreateLShr (left, right);
    }

    if (v != NULL)
    {
        currentFunction->setNamedValue (toReturn, v);
        return jnienv->NewStringUTF (toReturn.c_str ());
    }

    return NULL;
}
/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    methodCallToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRMethodCall;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_methodCallToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject, jstring jobjName, jobjectArray javaarray, jstring jReturnTypeName,
   jobjectArray jArgTypeArray, jstring jObjTypeName)
{
    jmethodID methodNameFID;
    jstring jMethodName;
    vector<Value*> args;
    string objName;
    string objTypeName;
    string firstArgTypeName;
    string methodName;
    char *argMethodName;
    vector<Value*> stubArgs;
    static int methodCallNum = 0;
    Value* methodToCall;
    Value* methodAddress;
    Value* returnValue;
    string returnValueName;
    string returnTypeName;
    Type* returnType;
    FunctionType* methodType;

    methodCallNum++;
    currentFunction->CreateAlloca (Type::getInt64Ty(Context), METHOD_CALL_ADDRESS);
    currentFunction->CreateAlloca (Type::getInt64Ty(Context), METHOD_CALL_OBJ_ADDRESS);
    javaclass = jnienv->GetObjectClass (javaobject);
    methodNameFID = jnienv->GetMethodID (javaclass, "getMethodName",
                                         "()Ljava/lang/String;");
    jMethodName = (jstring)jnienv->CallObjectMethod (javaobject, methodNameFID);
    methodName = jstringToString (jnienv, jMethodName);
    objName = jstringToString (jnienv, jobjName);
    returnTypeName = jstringToString (jnienv, jReturnTypeName);
    returnType = strTypeMap [returnTypeName];
    
    if ((jnienv->GetArrayLength (jArgTypeArray) == 1 &&
        (arithOpsSet.find (methodName) != arithOpsSet.end ())) ||
        (jnienv->GetArrayLength (jArgTypeArray) == 0 &&
        (methodName == UNARY_NEGATE || methodName == LOGICAL_NOT)))
    {
        Value* left;
        Value* right;
        string firstArg;
        jstring toReturn;
        
        toReturn = NULL;
        firstArgTypeName = jstringToString (jnienv, 
                                            (jstring)jnienv->GetObjectArrayElement (jArgTypeArray, 0));
        objTypeName = jstringToString (jnienv, jObjTypeName);
        firstArg = jstringToString (jnienv, 
                                    (jstring)jnienv->GetObjectArrayElement (javaarray, 0));
        left = currentFunction->getNamedValue (objName);
        
        if (methodName == UNARY_NEGATE || methodName == LOGICAL_NOT)
        {
            right = NULL;
        }
        else
        {
            right = currentFunction->getNamedValue (firstArg);
        }
        
        if (firstArgTypeName == TYPE_BOOLEAN && 
            objTypeName == TYPE_BOOLEAN)
        {
            toReturn = convertOperatorsForInteger (jnienv, methodName, left, right);
        }
        else if (firstArgTypeName == TYPE_INT && 
                 objTypeName == TYPE_INT)
        {
            toReturn = convertOperatorsForInteger (jnienv, methodName, left, right);
        }
        else if (firstArgTypeName == TYPE_RATIONAL && 
                 objTypeName == TYPE_RATIONAL)
        {
            toReturn = convertOperatorsForInteger (jnienv, methodName, left, right);
        }
        else if (firstArgTypeName == TYPE_STRING && 
                 objTypeName == TYPE_STRING)
        {
            toReturn = convertOperatorsForInteger (jnienv, methodName, left, right);
        }
    
        if (toReturn)
        {
            return toReturn;
        }        
    }
    
    methodType = FunctionType::get (returnType, true);
    argMethodName = new char [methodName.length ()];
    strcpy (argMethodName, methodName.c_str ());
    stubArgs.push_back (CastInst::CreateIntegerCast (currentFunction->getNamedValue (objName),
                                                     Type::getInt64Ty (Context),
                                                     false,
                                                     "objToIntCast" + to_string (methodCallNum),
                                                     Builder.GetInsertBlock()));
    stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          (unsigned long) argMethodName, true));
    stubArgs.push_back (ConstantInt::get (Type::getInt64Ty(getGlobalContext()),
                                          (unsigned long) methodCallNum, true));
    stubArgs.push_back (currentFunction->getNamedValueWithoutLoad (METHOD_CALL_OBJ_ADDRESS));

    methodAddress = CallInst::Create (GetFunctionToCall ((unsigned long)getWyvernFunction, getWyvernFunctionFuncType),
                                     stubArgs, "methodAddress"+to_string(methodCallNum), 
                                     Builder.GetInsertBlock ());

    args.push_back (currentFunction->getNamedValue (METHOD_CALL_OBJ_ADDRESS));
        
    if (returnType->isStructTy ())
    {
        returnType = PointerType::getUnqual (returnType);
    }

    for (int i = 0; i < jnienv->GetArrayLength (javaarray); i++)
    {
        jstring jargName = (jstring)jnienv->GetObjectArrayElement (javaarray, i);
        string argName = jstringToString (jnienv, jargName);
        
        args.push_back (currentFunction->getNamedValue (argName));
    }

    methodToCall = CastInst::CreatePointerCast (methodAddress, 
                                                PointerType::getUnqual (methodType),
                                                "methodPtrCast" + to_string(methodCallNum),
                                                Builder.GetInsertBlock());
    returnValue = CallInst::Create (methodToCall,
                                    args, "methodToCall"+to_string(methodCallNum), 
                                    Builder.GetInsertBlock ());
    returnValueName = "returnValue" + to_string (methodCallNum);
    currentFunction->setNamedValue (returnValueName , returnValue);

    return jnienv->NewStringUTF (returnValueName.c_str ());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    newToLLVMIR
 * Signature: (Ljava/lang/String;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_newToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jClassName, jint classID, jintArray jFieldsToInit, 
   jobjectArray jValueNames, jobjectArray jTypeNames)
{
    Type* classType;
    vector<Value*> args;
    Value* allocValue;
    CastInst* castInst;
    string name;
    static int classCast = 0;
    static int newCall = 0;
    jint* fieldsArray;
    
    classType = strTypeMap[jstringToString (jnienv, jClassName)];
    
    if (classType == NULL)
    {
        printf ("Error: No type with name '%s' present\n",
                jstringToString (jnienv, jClassName).c_str());
    }
    
    if (!classType->isStructTy ())
    {
        printf ("Error: In New Expression %s is not a class\n", 
                jstringToString (jnienv, jClassName).c_str ());
        return NULL;
    }

    args.push_back (ConstantExpr::getSizeOf (classType));
    args.push_back (ConstantInt::get (Type::getInt32Ty(getGlobalContext()),
                                      classID, true));
    allocValue = CallInst::Create (GetFunctionToCall (allocObjAddress, allocObjFuncType),
                                   args, "new"+to_string(newCall), 
                                   Builder.GetInsertBlock ());
    castInst = CastInst::CreatePointerCast (allocValue,
                                            PointerType::getUnqual (classType),
                                            "classCast"+to_string(classCast),
                                            Builder.GetInsertBlock());
    name = getConstantString ();
    currentFunction->setNamedValue (name, castInst);

    if (jFieldsToInit && jValueNames && jTypeNames)
    {
        fieldsArray = jnienv->GetIntArrayElements (jFieldsToInit, NULL);
        
        for (int i = 0; i < jnienv->GetArrayLength (jFieldsToInit); i++)
        {
            jint fieldPos;
            string argName;
            jstring jTypeName;
            string typeName;
            jstring jValueName;
            string valueName;
            GetElementPtrInst* gep;
            vector<Value*> gepIdx;

            /* field pos is according to OIR. 1 field Pos for first field */
            fieldPos = fieldsArray [i];
            jTypeName = (jstring)jnienv->GetObjectArrayElement (jTypeNames, i);
            typeName = jstringToString (jnienv, jTypeName);
            jValueName = (jstring)jnienv->GetObjectArrayElement (jValueNames, i);
            valueName = jstringToString (jnienv, jValueName);
            gepIdx.push_back (ConstantInt::get (Type::getInt32Ty(getGlobalContext()),
                              0, true));
            gepIdx.push_back (ConstantInt::get (Type::getInt32Ty(getGlobalContext()),
                              fieldPos, true));
            
            gep = GetElementPtrInst::Create (castInst, gepIdx, 
                                             "gep"+to_string (i), 
                                             Builder.GetInsertBlock ());
            Builder.CreateAlignedStore (currentFunction->getNamedValue (valueName),
                                        gep, true);
        }
    }

    return jnienv->NewStringUTF (name.c_str());    
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    rationalToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OnamedValuesIRRational;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_rationalToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject)
{
    Value *value;
    string n;
    jmethodID valueIntFID;
    jint valueInt;
    
    valueInt = 0;
    javaclass = jnienv->GetObjectClass (javaobject);

    valueIntFID = jnienv->GetMethodID (javaclass, "getValue", "()I");
    valueInt = jnienv->CallIntMethod (javaobject, valueIntFID);
    
    value = ConstantInt::get (Type::getDoubleTy(getGlobalContext()),
                              valueInt, true);
    n = getConstantString ();
    currentFunction->setNamedValue (n, value);

    return jnienv->NewStringUTF(n.c_str());
}
/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    stringToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRString;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_stringToLLVMIR
  (JNIEnv *, jclass, jobject);

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    valueToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRValue;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_valueToLLVMIR
  (JNIEnv *, jclass, jobject);

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    variableToLLVMIR
 * Signature: (Lwyvern/target/oir/expressions/OIRVariable;)V
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_variableToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject javaobject)
{
    javaclass = jnienv->GetObjectClass (javaobject);
    jmethodID varNameFID = (jnienv)->GetMethodID (javaclass, "getName", "()Ljava/lang/String;");
    jstring varName = (jstring)(jnienv)->CallObjectMethod (javaobject, varNameFID);
    
    return varName;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    interfaceToLLVMIR
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_interfaceToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jInterfaceName)
{
    string interfaceName;
    StructType* type;
    
    interfaceName = jstringToString (jnienv, jInterfaceName);
    if (strTypeMap [interfaceName] != NULL)
        return;
    
    type = StructType::create (Context, interfaceName);
    type->setBody (vector<Type*> (1, Type::getInt32Ty (Context)), false);
    strTypeMap [interfaceName] = type;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    oirProgramToLLVMIR
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_oirProgramToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jobject oirProgram)
{
    vector<Type*> argsType;
    
    oirProgramObject = oirProgram;
    oirProgramClass = jnienv->GetObjectClass (oirProgram);
    
    strTypeMap[TYPE_BOOLEAN] = Type::getInt8Ty (Context);
    strTypeMap[TYPE_INT] = Type::getInt32Ty (Context);
    strTypeMap[TYPE_RATIONAL] = Type::getDoubleTy (Context);
    //strTypeMap[TYPE_STRING] = Type::getInt8Ty (Context);

    arithOpsSet.emplace (TYPE_BOOLEAN);
    arithOpsSet.emplace (TYPE_INT);
    arithOpsSet.emplace (TYPE_RATIONAL);
    arithOpsSet.emplace (TYPE_STRING);
    
    arithOpsSet.emplace (ARITH_ADD);
    arithOpsSet.emplace (ARITH_SUBTRACT);
    arithOpsSet.emplace (ARITH_MULTIPLY);
    arithOpsSet.emplace (ARITH_DIVIDE);
    arithOpsSet.emplace (ARITH_MODULO);
    
    arithOpsSet.emplace (UNARY_NEGATE);
    
    arithOpsSet.emplace (LOGICAL_NOT);
    arithOpsSet.emplace (LOGICAL_AND);
    arithOpsSet.emplace (LOGICAL_OR);
    
    arithOpsSet.emplace (BITWISE_AND);
    arithOpsSet.emplace (BITWISE_OR);
    arithOpsSet.emplace (BITWISE_XOR);
    arithOpsSet.emplace (BITWISE_LEFT_SHIFT);
    arithOpsSet.emplace (BITWISE_RIGHT_SHIFT);

    argsType.push_back (Type::getInt64Ty (Context));
    argsType.push_back (Type::getInt64Ty (Context));
    argsType.push_back (Type::getInt64Ty (Context));
    argsType.push_back (PointerType::getUnqual (Type::getInt64Ty (Context)));
    
    getWyvernFunctionFuncType = FunctionType::get (Type::getInt64Ty (Context),
                                                   argsType, false);
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    beginClassStructure
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_beginClassStructure
  (JNIEnv *jnienv, jclass javaclass, jstring jClassName, jstring jSelfName)
{
    string className;
    string selfName;
    
    className = jstringToString (jnienv, jClassName);
    selfName = jstringToString (jnienv, jSelfName);
    currentClass = new WyvernClass (className, selfName);
    strTypeMap [className] = currentClass->getClassType ();
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    fieldDeclarationToLLVMIR
 * Signature: (Ljava/lang/String;Lwyvern/target/oir/declarations/OIRInterface;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_fieldDeclarationToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jFieldName, jstring jTypeName)
{
    string fieldName;
    string typeName;
    Type* fieldType;

    fieldName = jstringToString (jnienv, jFieldName);
    typeName = jstringToString (jnienv, jTypeName);
    fieldType = PointerType::getUnqual (strTypeMap [typeName]);
    currentClass->addFieldType (fieldType);
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    endClassStructure
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_endFieldDecls
  (JNIEnv *jnienv, jclass javaclass, jstring className)
{
    currentClass->addFieldTypesToClassType ();
    //delete currentClass;
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    methodDeclToLLVMIR
 * Signature: (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wyvern_target_oir_EmitLLVMNative_methodDeclToLLVMIR
  (JNIEnv *jnienv, jclass javaclass, jstring jReturnType, jstring jMethodName, jobjectArray jArgs)
{
    string returnTypeName;
    string methodName;
    BasicBlock *BB;
    vector <Type*> argTypes;
    vector <string> argNames;
    
    returnTypeName = jstringToString (jnienv, jReturnType);
    methodName = currentClass->getClassName () + "_" + jstringToString (jnienv, jMethodName);
    argTypes.push_back (currentClass->getClassType ());
    argNames.push_back (currentClass->getSelfName ());

    for (int i = 0; i < jnienv->GetArrayLength (jArgs); i += 2)
    {
        jstring jargName;
        jstring jargTypeName;
        string argName;
        string argTypeName;
        
        jargTypeName = (jstring)jnienv->GetObjectArrayElement (jArgs, i);
        jargName = (jstring)jnienv->GetObjectArrayElement (jArgs, i+1);
        
        argName = jstringToString (jnienv, jargName);
        argTypeName = jstringToString (jnienv, jargTypeName);
        
        argTypes.insert (argTypes.begin (), strTypeMap [argTypeName]);
        argNames.insert (argNames.begin (), argName);
    }

    currentFunction = new WyvernFunction (strTypeMap [returnTypeName],
                                          argTypes, argNames, false, 
                                          methodName, TheModule);

    BB = BasicBlock::Create(Context, "entry", 
                            currentFunction->getFunction ());

    Builder.SetInsertPoint(BB);

    return jnienv->NewStringUTF (methodName.c_str ());
}

/*
 * Class:     wyvern_target_oir_EmitLLVMNative
 * Method:    executeLLVMJIT
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_wyvern_target_oir_EmitLLVMNative_executeLLVMJIT
  (JNIEnv *jnienv, jclass javaclass)
{
    string errorMessage;
    int (*main) ();
    int res;
    
    globalJNIEnv = jnienv;
    InitializeNativeTarget ();
    InitializeNativeTargetAsmPrinter ();
    InitializeNativeTargetAsmParser ();    
    
    execEngine = EngineBuilder (TheModule).setUseMCJIT (true).create ();
    execEngine->addGlobalMapping(allocObject_func, (void*)(__allocWyvernObject));
    execEngine->finalizeObject();
    main = (int (*) ())execEngine->getFunctionAddress (string("main"));
    res = main ();
    cout<<res<<"main result"<<endl;
}
