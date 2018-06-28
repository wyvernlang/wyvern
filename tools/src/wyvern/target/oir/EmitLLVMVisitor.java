package wyvern.target.oir;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRCast;
import wyvern.target.oir.expressions.OIRCharacter;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.target.oir.expressions.OIRFFIImport;
import wyvern.target.oir.expressions.OIRFieldGet;
import wyvern.target.oir.expressions.OIRFieldSet;
import wyvern.target.oir.expressions.OIRFloat;
import wyvern.target.oir.expressions.OIRIfThenElse;
import wyvern.target.oir.expressions.OIRInteger;
import wyvern.target.oir.expressions.OIRLet;
import wyvern.target.oir.expressions.OIRMethodCall;
import wyvern.target.oir.expressions.OIRNew;
import wyvern.target.oir.expressions.OIRRational;
import wyvern.target.oir.expressions.OIRString;
import wyvern.target.oir.expressions.OIRVariable;

public class EmitLLVMVisitor extends EmitILVisitor<String> {
    @Override
    public String visit(OIREnvironment oirenv, OIRInteger oirInteger) {
        return EmitLLVMNative.integerToLLVMIR(oirInteger);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRBoolean oirBoolean) {
        return EmitLLVMNative.booleanToLLVMIR(oirBoolean);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRCast oirCast) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRFieldGet oirFieldGet) {
        OIRType oirType = oirFieldGet.typeCheck(oirenv);
        OIRType oirObjectType = oirFieldGet.getObjectExpr().typeCheck(oirenv);
        String objName = oirFieldGet.getObjectExpr().acceptVisitor(this, oirenv);
        return EmitLLVMNative.fieldGetToLLVMIR(oirObjectType.getName(),
                objName, oirFieldGet.getFieldName(), oirType.getName());
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRFieldSet oirFieldSet) {
        OIRType oirType = oirFieldSet.typeCheck(oirenv);
        OIRType oirObjectType = oirFieldSet.getObjectExpr().typeCheck(oirenv);
        String objName = oirFieldSet.getObjectExpr().acceptVisitor(this, oirenv);
        String valueName = oirFieldSet.getExprToAssign().acceptVisitor(this, oirenv);
        return EmitLLVMNative.fieldSetToLLVMIR(valueName, oirObjectType.getName(), objName,
                oirFieldSet.getFieldName(), oirType.getName());
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRIfThenElse oirIfThenElse) {
        String strCondExpr = oirIfThenElse.getCondition().acceptVisitor(this, oirenv);
        strCondExpr = EmitLLVMNative.ifCondExprToLLVMIR(strCondExpr);
        String strThenBB = EmitLLVMNative.createThenBasicBlock();
        String strElseBB = EmitLLVMNative.createElseBasicBlock();
        String strMergeBB = EmitLLVMNative.createMergeBasicBlock(strCondExpr, strThenBB, strElseBB);

        EmitLLVMNative.setupThenBasicBlockEmit(strThenBB);
        String strThenExpr = oirIfThenElse.getThenExpression().acceptVisitor(this, oirenv);
        EmitLLVMNative.emitThenBasicBlock(strThenExpr, strThenBB, strMergeBB);

        EmitLLVMNative.setupElseBasicBlockEmit(strElseBB);
        String strElseExpr = oirIfThenElse.getElseExpression().acceptVisitor(this, oirenv);
        EmitLLVMNative.emitElseBasicBlock(strElseExpr, strElseBB, strMergeBB);

        return EmitLLVMNative.emitMergeBasicBlock(strMergeBB, strThenExpr, strThenBB, strElseExpr, strElseBB);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRLet oirLet) {
        String toReplaceName = oirLet.getToReplace().acceptVisitor(this, oirenv);
        EmitLLVMNative.letToLLVMIR(oirLet, toReplaceName, oirLet.getToReplace().getExprType().toString());
        return oirLet.getInExpr().acceptVisitor(this, oirenv);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRMethodCall oirMethodCall) {
        int i = 0;
        String[] argNames = new String[oirMethodCall.getArgs().size()];
        String[] argTypeNames = new String[argNames.length];
        for (OIRExpression arg : oirMethodCall.getArgs()) {
            argNames[i] = arg.acceptVisitor(this, oirenv);
            argTypeNames[i] = arg.typeCheck(oirenv).getName();
            i++;
        }
        String objName = oirMethodCall.getObjectExpr().acceptVisitor(this, oirenv);
        String objTypeName = oirMethodCall.getObjectExpr().typeCheck(oirenv).getName();
        OIRType retType = oirMethodCall.getExprType();
        return EmitLLVMNative.methodCallToLLVMIR(oirMethodCall, objName, argNames,
                retType.getName(), argTypeNames, objTypeName);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRNew oirNew) {
        String className = oirNew.getTypeName();
        int[] fieldsToInitialize = null;
        String[] initializeValueNames = null;
        String[] typeNames = null;
        int i = 0;

        OIREnvironment oirEnv = OIREnvironment.getRootEnvironment();
        OIRClassDeclaration classDecl = (OIRClassDeclaration) oirEnv.lookupType(className);
        int classID = classDecl.getClassID();
        if (classDecl.getFieldValuePairs() != null) {
            fieldsToInitialize = new int[classDecl.getFieldValuePairs().size()];
            initializeValueNames = new String[classDecl.getFieldValuePairs().size()];
            typeNames = new String[classDecl.getFieldValuePairs().size()];
            for (OIRFieldValueInitializePair pair : classDecl.getFieldValuePairs()) {
                initializeValueNames[i] = pair.getValueDeclaration().acceptVisitor(this, oirEnv);
                typeNames[i] = pair.getValueDeclaration().typeCheck(oirEnv).getName();
                fieldsToInitialize[i] = classDecl.getFieldPosition(pair.getFieldDeclaration().getName());
                i++;
            }
        }
        return EmitLLVMNative.newToLLVMIR(className, classID, fieldsToInitialize, initializeValueNames, typeNames);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRRational oirRational) {
        return EmitLLVMNative.rationalToLLVMIR(oirRational);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRString oirString) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String visit(OIREnvironment oirenv, OIRCharacter oirString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRVariable oirVariable) {
        return EmitLLVMNative.variableToLLVMIR(oirVariable);
    }
    @Override
    public String visit(OIREnvironment oirenv, OIRProgram oirProgram) {
        EmitLLVMNative.oirProgramToLLVMIR(oirProgram);

        /* First convert all the interfaces to LLVM IR*/
        for (OIRType oirType : oirProgram.typeDeclarations()) {
            if (oirType instanceof OIRInterface) {
                oirType.acceptVisitor(this, oirenv);
            }
        }

        /* Now convert classes */
        for (OIRType oirType : oirProgram.typeDeclarations()) {
            if (oirType instanceof OIRClassDeclaration) {
                oirType.acceptVisitor(this, oirenv);
            }
        }

        /* TODO Add main expression conversion here */
        return oirProgram.getMainExpression().acceptVisitor(this, oirenv);
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRInterface oirInterface) {
        EmitLLVMNative.interfaceToLLVMIR(oirInterface.getName());
        return oirInterface.getName();
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRClassDeclaration oirClassDeclaration) {
        EmitLLVMNative.beginClassStructure(oirClassDeclaration.getName(), oirClassDeclaration.getSelfName());
        for (OIRMemberDeclaration oirMemDecl : oirClassDeclaration.getMembers()) {
            if (oirMemDecl instanceof OIRFieldDeclaration) {
                oirMemDecl.acceptVisitor(this, oirenv);
            }
        }
        EmitLLVMNative.endFieldDecls(oirClassDeclaration.getName());
        for (OIRMemberDeclaration oirMemDecl : oirClassDeclaration.getMembers()) {
            if (oirMemDecl instanceof OIRMethod) {
                oirMemDecl.acceptVisitor(this, oirenv);
            }
        }
        return "";
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRFieldDeclaration oirFieldDeclaration) {
        ((OIRInterface) oirFieldDeclaration.getType()).acceptVisitor(this, oirenv);
        EmitLLVMNative.fieldDeclarationToLLVMIR(oirFieldDeclaration.getName(),
                ((OIRInterface) oirFieldDeclaration.getType()).getName());
        return oirFieldDeclaration.getName();
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRMethodDeclaration oirMethodDecl) {
        String[] args = new String[2 * oirMethodDecl.getArgs().size()];
        int i = 0;
        oirMethodDecl.getReturnType().acceptVisitor(this, oirenv);
        for (OIRFormalArg arg : oirMethodDecl.getArgs()) {
            arg.getType().acceptVisitor(this, oirenv);
            args[i] = arg.getType().getName();
            args[i + 1] = arg.getName();
            i += 2;
        }
        EmitLLVMNative.methodDeclToLLVMIR(oirMethodDecl.getReturnType().getName(), oirMethodDecl.getName(), args);
        return "";
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRMethod oirMethod) {
        oirMethod.getDeclaration().acceptVisitor(this, oirenv);
        String toReturn = oirMethod.getBody().acceptVisitor(this, oirenv);
        EmitLLVMNative.functionCreated(toReturn);
        return "";
    }

    @Override
    public String visit(OIREnvironment oirenv, OIRFFIImport ffiImport) {
        throw new RuntimeException("FFI not implemented in LLVM");
    }

    @Override
    public String visit(OIREnvironment state, OIRFloat oirFloat)  {
      return EmitLLVMNative.floatToLLVMIR(oirFloat);
    }
}
