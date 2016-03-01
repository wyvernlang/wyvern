package wyvern.target.oir;

import java.util.List;
import java.util.HashSet;

import wyvern.target.oir.declarations.OIRClassDeclaration;
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

public class PrettyPrintVisitor extends ASTVisitor<OIREnvironment, String> {
    String indent = "";
    final String indentIncrement = "  ";
    int uniqueId = 0;

    HashSet<String> classesUsed;

    public PrettyPrintVisitor() {
        classesUsed = new HashSet<String>();
    }

    private String commaSeparatedExpressions(OIREnvironment oirenv,
                                             List<OIRExpression> exps) {
        String args = "";
        int nArgs = exps.size();
        for (int i = 0; i < nArgs; i++) {
            OIRExpression arg_i = exps.get(i);
            args += arg_i.acceptVisitor(this, oirenv);
            if (i < nArgs - 1)
                args += ", ";
        }
        return args;
    }

    public String prettyPrint(OIRAST oirast,
                              OIREnvironment oirenv) {
        String python =
             oirast.acceptVisitor(this, oirenv);
        String[] lines = python.split("\\n");
        int lastIndex = lines.length-1;
        lines[lastIndex] = "print(" + lines[lastIndex] + ")";
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            out.append(line);
            out.append("\n");
        }

        String classDefs = "";
        for (String className : classesUsed) {
            OIRType type = oirenv.lookup(className);
            classDefs += type.acceptVisitor(this, oirenv) + "\n";
        }

        return classDefs + out.toString();
    }

    public String visit(OIREnvironment oirenv,
                        OIRInteger oirInteger) {
        return Integer.toString(oirInteger.getValue());
    }

    public String visit(OIREnvironment oirenv,
                        OIRBoolean oirBoolean) {
        return (oirBoolean.isValue() ? "True" : "False");
    }

    public String visit(OIREnvironment oirenv,
                        OIRCast oirCast) {
        return "OIRCast unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRFieldGet oirFieldGet) {
        return (oirFieldGet.getObjectExpr().acceptVisitor(this, oirenv) +
                "." +
                oirFieldGet.getFieldName());
    }

    public String visit(OIREnvironment oirenv,
                        OIRFieldSet oirFieldSet) {
        return (oirFieldSet.getObjectExpr().acceptVisitor(this, oirenv) +
                "." +
                oirFieldSet.getFieldName() +
                " = " +
                oirFieldSet.getExprToAssign().acceptVisitor(this, oirenv));
    }

    public String visit(OIREnvironment oirenv,
                        OIRIfThenElse oirIfThenElse) {
        String conditionString = oirIfThenElse.getCondition().acceptVisitor(this, oirenv);
        String oldIndent = indent;
        indent += indentIncrement;
        String thenString = oirIfThenElse.getThenExpression().acceptVisitor(this, oirenv);
        String elseString = oirIfThenElse.getElseExpression().acceptVisitor(this, oirenv);
        indent = oldIndent;
        return ("if " +
                conditionString +
                ":\n" +
                indent + indentIncrement +
                thenString +
                "\n" + indent + "else:\n" +
                indent + indentIncrement +
                elseString);
    }

    public String visit(OIREnvironment oirenv,
                        OIRLet oirLet) {
        String oldIndent = indent;
        indent += indentIncrement;
        String inString = oirLet.getInExpr().acceptVisitor(this, oirenv);
        indent = oldIndent;

        int letId = uniqueId;
        uniqueId++;

        String funDecl = "def letFn" + Integer.toString(letId) +
            "(" + oirLet.getVarName() +"):\n" + indent + indentIncrement
            + "return ";
        String toReplaceString = oirLet.getToReplace().acceptVisitor(this, oirenv);
        String funCall = "\n" + indent + "letFn" + Integer.toString(letId) +
            "(" + toReplaceString + ")";

        return (funDecl + inString + funCall);
    }

    public String visit(OIREnvironment oirenv,
                        OIRMethodCall oirMethodCall) {
        String objExpr =
            oirMethodCall.getObjectExpr().acceptVisitor(this, oirenv);
        String args = commaSeparatedExpressions(oirenv,
                                                oirMethodCall.getArgs());
        return objExpr + "." + oirMethodCall.getMethodName() + "(" + args + ")";
    }

    public String visit(OIREnvironment oirenv,
                        OIRNew oirNew) {
        String args = commaSeparatedExpressions(oirenv,
                                                oirNew.getArgs());
        classesUsed.add(oirNew.getTypeName());
        return oirNew.getTypeName() + "(" + args + ")";
    }

    public String visit(OIREnvironment oirenv,
                        OIRRational oirRational) {
        return "OIRRational unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRString oirString) {
        return "\"" + oirString.getValue() + "\"";
    }

    public String visit(OIREnvironment oirenv,
                        OIRVariable oirVariable) {
        return oirVariable.getName();
    }

    public String visit(OIREnvironment oirenv,
                        OIRClassDeclaration oirClassDeclaration) {
        String classDef = "class " + oirClassDeclaration.getName() + ":";
        String oldIndent = indent;
        indent += indentIncrement;
        String members = "";

        // Build a constructor
        StringBuilder constructor_args = new StringBuilder();
        StringBuilder constructor_body = new StringBuilder();
        for (OIRFieldValueInitializePair pair : oirClassDeclaration.getFieldValuePairs()) {
            OIRFieldDeclaration dec = pair.fieldDeclaration;
            OIRExpression value = pair.valueDeclaration;
            constructor_body.append("\n");
            constructor_body.append(indent + indentIncrement);
            constructor_body.append("this.");
            constructor_body.append(dec.getName());
            constructor_body.append(" = ");
            constructor_body.append(value.acceptVisitor(this, oirenv));

            constructor_args.append(", " + dec.getName() + "Ignored");
        }
        members += "\n" + indent +
            "def __init__(this" + constructor_args.toString() + "):";
        members += constructor_body.toString();

        for (OIRMemberDeclaration memberDec : oirClassDeclaration.getMembers()) {
            members += "\n" + indent;
            if (memberDec instanceof OIRMethod) {
                OIRMethod method = (OIRMethod)memberDec;
                members += method.acceptVisitor(this, oirenv);
            }
        }

        indent = oldIndent;

        return classDef + members;
    }

    public String visit(OIREnvironment oirenv,
                        OIRProgram oirProgram) {
        return "OIRProgram unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRInterface oirInterface) {
        return "OIRInterface unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRFieldDeclaration oirFieldDeclaration) {
        return "OIRFieldDeclaration unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRMethodDeclaration oirMethodDeclaration) {
        return "OIRMethodDeclaration unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRMethod oirMethod) {
        String args = "this";
        for (OIRFormalArg formalArg : oirMethod.getDeclaration().getArgs()) {
            args += ", " + formalArg.getName();
        }
        String def = "def " + oirMethod.getDeclaration().getName() +
            "(" + args + ")"+ ":";

        String oldIndent = indent;
        indent += indentIncrement;

        String body = "\n" + indent + "return " +
            oirMethod.getBody().acceptVisitor(this, oirenv);

        indent = oldIndent;

        return def + body;
    }
}
