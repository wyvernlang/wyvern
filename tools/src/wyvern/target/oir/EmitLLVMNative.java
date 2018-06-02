package wyvern.target.oir;

import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRIfThenElse;
import wyvern.target.oir.expressions.OIRInteger;
import wyvern.target.oir.expressions.OIRFloat;
import wyvern.target.oir.expressions.OIRLet;
import wyvern.target.oir.expressions.OIRLiteral;
import wyvern.target.oir.expressions.OIRMethodCall;
import wyvern.target.oir.expressions.OIRRational;
import wyvern.target.oir.expressions.OIRString;
import wyvern.target.oir.expressions.OIRValue;
import wyvern.target.oir.expressions.OIRVariable;

public final class EmitLLVMNative {
    private EmitLLVMNative() { }

    public static native void oirProgramToLLVMIR(OIRProgram oirProgram);
    public static native void createMainFunction();
    public static native void functionCreated(String toReturn);
    public static native void executeLLVMJIT();
    public static native String letToLLVMIR(OIRLet let, String toReplaceExpr, String toReplaceType);

    /* Constants */
    public static native String integerToLLVMIR(OIRInteger oirinteger);
    public static native String floatToLLVMIR(OIRFloat oirfloat);
    public static native String booleanToLLVMIR(OIRBoolean let);
    public static native String rationalToLLVMIR(OIRRational let);
    public static native String stringToLLVMIR(OIRString let);

    /* IfThenElse conversion functions */
    public static native String ifCondExprToLLVMIR(String condExpr);
    public static native String createThenBasicBlock();
    public static native String createElseBasicBlock();
    public static native String createMergeBasicBlock(String condExpr, String thenBB, String elseBB);
    public static native void setupThenBasicBlockEmit(String thenBB);
    public static native String emitThenBasicBlock(String thenExpr, String thenBB, String mergeBB);
    public static native void setupElseBasicBlockEmit(String thenBB);
    public static native String emitElseBasicBlock(String elseExpr, String elseBB, String mergeBB);
    public static native String emitMergeBasicBlock(String mergeBB, String strThenExpr, String thenBB, String strElseExpr, String elseBB);
    public static native String createMergeLocalVar();

    public static native void beginClassStructure(String className, String selfName);
    public static native void fieldDeclarationToLLVMIR(String fieldName, String typeName);
    public static native void endFieldDecls(String className);

    public static native String methodDeclToLLVMIR(String retTypeName, String methodName, String[] args);
    public static native void endClassStructure(String className);

    public static native void interfaceToLLVMIR(String interfaceName);
    public static native String fieldGetToLLVMIR(String objTypeName, String objName, String fieldName, String fieldTypeName);
    public static native String fieldSetToLLVMIR(String valueName, String objTypeName, String objName, String fieldName, String fieldTypeName);
    public static native void ifThenElseToLLVMIR(OIRIfThenElse let);
    public static native void literalToLLVMIR(OIRLiteral let);
    public static native String methodCallToLLVMIR(OIRMethodCall let, String objName, String[] argNames,
            String returnTypeName, String[] argTypeNames, String objTypeName);
    public static native String newToLLVMIR(String className, int classID, int[] fieldsToInitialize, String[] initializeValueName, String[] typeNames);
    public static native void valueToLLVMIR(OIRValue let);
    public static native String variableToLLVMIR(OIRVariable let);
}
