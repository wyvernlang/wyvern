package wyvern.target.oir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRDelegate;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRCast;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.target.oir.expressions.OIRFFIImport;
import wyvern.target.oir.expressions.FFIType;
import wyvern.target.oir.expressions.OIRFieldGet;
import wyvern.target.oir.expressions.OIRFieldSet;
import wyvern.target.oir.expressions.OIRIfThenElse;
import wyvern.target.oir.expressions.OIRInteger;
import wyvern.target.oir.expressions.OIRLet;
import wyvern.target.oir.expressions.OIRMethodCall;
import wyvern.target.oir.expressions.OIRNew;
import wyvern.target.oir.expressions.OIRRational;
import wyvern.target.oir.expressions.OIRString;
import wyvern.target.oir.expressions.OIRVariable;

class NameMangleState {
}

public class NameMangleVisitor extends ASTVisitor<NameMangleState, OIRAST> {

    List<String> pythonKeywords;
    String keywordPrefix = "pykwd_";

    public NameMangleVisitor() {
        pythonKeywords = new ArrayList<String>() {{
                add("and");
                add("as");
                add("assert");
                add("break");
                add("class");
                add("continue");
                add("def");
                add("del");
                add("elif");
                add("else");
                add("except");
                add("exec");
                add("finally");
                add("for");
                add("from");
                add("global");
                add("if");
                add("import");
                add("in");
                add("is");
                add("lambda");
                add("not");
                add("or");
                add("pass");
                add("print");
                add("raise");
                add("return");
                add("try");
                add("while");
                add("with");
                add("yield");
                add("None");
            }};
    }

    public static OIRAST mangleAST(OIRAST ast) {
        return ast.acceptVisitor(new NameMangleVisitor(),
                                 new NameMangleState());
    }

    public static String mangle(String name) {
        name = name.replaceAll("\\$", "__");
        name = name.replaceAll("\\.", "___");
        return "wyv" + name;
    }

    public OIRAST visit(NameMangleState state,
                        OIRInteger oirInteger) {
        return oirInteger;
    }

    public OIRAST visit(NameMangleState state,
                        OIRBoolean oirBoolean) {
        return oirBoolean;
    }

    public OIRAST visit(NameMangleState state,
                        OIRCast oirCast) {
        OIRCast cast = new OIRCast((OIRExpression) oirCast.getToCastEXpr().acceptVisitor(this, state),
                                   oirCast.getExprType());
        cast.copyMetadata(oirCast);
        return cast;
    }

    public OIRAST visit(NameMangleState state,
                        OIRFieldGet oirFieldGet) {
        OIRFieldGet fieldGet = new OIRFieldGet((OIRExpression) oirFieldGet.getObjectExpr().acceptVisitor(this, state),
                                               oirFieldGet.getFieldName());
        fieldGet.copyMetadata(oirFieldGet);
        return fieldGet;
    }

    public OIRAST visit(NameMangleState state,
                        OIRFieldSet oirFieldSet) {
        OIRFieldSet fieldSet = new OIRFieldSet((OIRExpression) oirFieldSet.getObjectExpr().acceptVisitor(this, state),
                                               oirFieldSet.getFieldName(),
                                               (OIRExpression) oirFieldSet.getExprToAssign().acceptVisitor(this, state));
        fieldSet.copyMetadata(oirFieldSet);
        return fieldSet;
    }

    public OIRAST visit(NameMangleState state,
                        OIRIfThenElse oirIfThenElse) {
        OIRIfThenElse ifThenElse = new OIRIfThenElse((OIRExpression) oirIfThenElse.getCondition().acceptVisitor(this, state),
                                                     (OIRExpression) oirIfThenElse.getThenExpression().acceptVisitor(this, state),
                                                     (OIRExpression) oirIfThenElse.getElseExpression().acceptVisitor(this, state));
        ifThenElse.copyMetadata(oirIfThenElse);
        return ifThenElse;
    }

    public OIRAST visit(NameMangleState state,
                        OIRLet oirLet) {
        OIRLet let = new OIRLet(mangle(oirLet.getVarName()),
                                (OIRExpression) oirLet.getToReplace().acceptVisitor(this, state),
                                (OIRExpression) oirLet.getInExpr().acceptVisitor(this, state));
        let.copyMetadata(oirLet);
        return let;
    }

    public OIRAST visit(NameMangleState state,
                        OIRMethodCall oirMethodCall) {
        ArrayList<OIRExpression> newArgs = new ArrayList<>();
        for (OIRExpression arg : oirMethodCall.getArgs()) {
            if (arg == null)
                newArgs.add(null);
            else
                newArgs.add((OIRExpression) arg.acceptVisitor(this, state));
        }
        String methodName = oirMethodCall.getMethodName();
        if (pythonKeywords.contains(methodName))
            methodName = keywordPrefix + methodName;
        OIRMethodCall methodCall = new OIRMethodCall((OIRExpression) oirMethodCall.getObjectExpr().acceptVisitor(this, state),
                                                     oirMethodCall.getObjectType(),
                                                     methodName,
                                                     newArgs);
        methodCall.copyMetadata(oirMethodCall);
        return methodCall;
    }

    public OIRAST visit(NameMangleState state,
                        OIRNew oirNew) {
        ArrayList<OIRExpression> newArgs = new ArrayList<>();
        for (OIRExpression arg : oirNew.getArgs()) {
            newArgs.add((OIRExpression) arg.acceptVisitor(this, state));
        }
        OIRNew new_ = new OIRNew(newArgs, oirNew.getTypeName());
        new_.copyMetadata(oirNew);
        return new_;
    }

    public OIRAST visit(NameMangleState state,
                        OIRRational oirRational) {
        return oirRational;
    }

    public OIRAST visit(NameMangleState state,
                        OIRString oirString) {
        return oirString;
    }

    public OIRAST visit(NameMangleState state,
                        OIRVariable oirVariable) {
        OIRVariable var = new OIRVariable(mangle(oirVariable.getName()));
        var.copyMetadata(oirVariable);
        return var;
    }

    public OIRAST visit(NameMangleState state,
                        OIRClassDeclaration oirClassDeclaration) {
        OIREnvironment env = oirClassDeclaration.getEnvironment();
        String name = oirClassDeclaration.getName();
        String selfName = oirClassDeclaration.getSelfName();
        ArrayList<OIRDelegate> delegates = new ArrayList<>();
        for (OIRDelegate del : oirClassDeclaration.getDelegates()) {
            delegates.add(new OIRDelegate(del.getType(), mangle(del.getField())));
        }
        ArrayList<OIRMemberDeclaration> members = new ArrayList<>();
        for (OIRMemberDeclaration decl : oirClassDeclaration.getMembers()) {
            if (decl instanceof OIRMethod) {
                OIRMethod mdecl = (OIRMethod)decl;
                members.add((OIRMemberDeclaration) mdecl.acceptVisitor(this, state));
            } else {
                members.add(decl);
            }
        }
        List<OIRFieldValueInitializePair> fieldValuePairs = oirClassDeclaration.getFieldValuePairs();
        HashSet<String> freeVars = new HashSet<>();
        for (String freeVar : oirClassDeclaration.getFreeVariables()) {
            freeVars.add(mangle(freeVar));
        }
        OIRClassDeclaration classDecl = new OIRClassDeclaration(env, name, selfName, delegates,
                                                                members, fieldValuePairs,
                                                                freeVars);
        classDecl.copyMetadata(oirClassDeclaration);
        return classDecl;
    }

    public OIRAST visit(NameMangleState state,
                        OIRProgram oirProgram) {
        oirProgram.setMainExpression((OIRExpression)oirProgram.getMainExpression().acceptVisitor(this, state));
        return oirProgram;
    }

    public OIRAST visit(NameMangleState state,
                        OIRInterface oirInterface) {
        return oirInterface;
    }

    public OIRAST visit(NameMangleState state,
                        OIRFieldDeclaration oirFieldDeclaration) {
        return oirFieldDeclaration;
    }

    public OIRAST visit(NameMangleState state,
                        OIRMethodDeclaration oirMethodDeclaration) {
        List<OIRFormalArg> args = oirMethodDeclaration.getArgs();
        ArrayList<OIRFormalArg> newArgs = new ArrayList<>();
        for (OIRFormalArg arg : args) {
            newArgs.add(new OIRFormalArg(mangle(arg.getName()),
                                         arg.getType()));
        }
        OIRMethodDeclaration methodDecl = new OIRMethodDeclaration(oirMethodDeclaration.getReturnType(),
                                                                   oirMethodDeclaration.getName(),
                                                                   newArgs);
        return methodDecl;
    }

    public OIRAST visit(NameMangleState state,
                        OIRMethod oirMethod) {
        String methodName = oirMethod.getDeclaration().getName();
        if (pythonKeywords.contains(methodName))
            oirMethod.getDeclaration().setName(keywordPrefix + methodName);
        OIRMethod method = new OIRMethod(oirMethod.getEnvironment(),
                                         (OIRMethodDeclaration) oirMethod.getDeclaration().acceptVisitor(this, state),
                                         (OIRExpression) oirMethod.getBody().acceptVisitor(this, state));
        method.copyMetadata(oirMethod);
        return method;
    }

    public OIRAST visit(NameMangleState state,
                        OIRFFIImport oirImport) {
        return oirImport;
    }
}
