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

class PrettyPrintState {
    public OIREnvironment oirenv;
    public boolean expectingReturn;
}

public class PrettyPrintVisitor extends ASTVisitor<PrettyPrintState, String> {
    String indent = "";
    final String indentIncrement = "  ";
    int uniqueId = 0;

    HashSet<String> classesUsed;

    public PrettyPrintVisitor() {
        classesUsed = new HashSet<String>();
    }

    private String commaSeparatedExpressions(PrettyPrintState state,
                                             List<OIRExpression> exps) {
        String args = "";
        int nArgs = exps.size();
        for (int i = 0; i < nArgs; i++) {
            OIRExpression arg_i = exps.get(i);
            args += arg_i.acceptVisitor(this, state);
            if (i < nArgs - 1)
                args += ", ";
        }
        return args;
    }

    public String prettyPrint(OIRAST oirast,
                              OIREnvironment oirenv) {
        PrettyPrintState state = new PrettyPrintState();
        state.oirenv = oirenv;
        state.expectingReturn = false;
        String python =
             oirast.acceptVisitor(this, state);
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
            classDefs += type.acceptVisitor(this, state) + "\n";
        }

        return classDefs + out.toString();
    }

    public String visit(PrettyPrintState state,
                        OIRInteger oirInteger) {
        String strVal = Integer.toString(oirInteger.getValue());
        if (state.expectingReturn)
            return "return " + strVal;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRBoolean oirBoolean) {
        String strVal = (oirBoolean.isValue() ? "True" : "False");
        if (state.expectingReturn)
            return "return " + strVal;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRCast oirCast) {
        return "OIRCast unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRFieldGet oirFieldGet) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;
        String objExpr =
            oirFieldGet.getObjectExpr().acceptVisitor(this, state);
        String strVal =
            (objExpr + "." +
             oirFieldGet.getFieldName());
        state.expectingReturn = oldExpectingReturn;
        if (state.expectingReturn)
            return "return " + strVal;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRFieldSet oirFieldSet) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;
        String objExpr =
            oirFieldSet.getObjectExpr().acceptVisitor(this, state);
        String strVal =
            (objExpr +
             "." +
             oirFieldSet.getFieldName() +
             " = " +
             oirFieldSet.getExprToAssign().acceptVisitor(this, state));
        state.expectingReturn = oldExpectingReturn;
        if (state.expectingReturn)
            return strVal + "\n" + indent + "return " + objExpr;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRIfThenElse oirIfThenElse) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;
        String conditionString = oirIfThenElse.getCondition().acceptVisitor(this, state);
        state.expectingReturn = oldExpectingReturn;
        String oldIndent = indent;
        indent += indentIncrement;
        String thenString = oirIfThenElse.getThenExpression().acceptVisitor(this, state);
        String elseString = oirIfThenElse.getElseExpression().acceptVisitor(this, state);
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

    public String visit(PrettyPrintState state,
                        OIRLet oirLet) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = true;

        String oldIndent = indent;
        indent += indentIncrement;
        String inString = oirLet.getInExpr().acceptVisitor(this, state);
        indent = oldIndent;

        int letId = uniqueId;
        uniqueId++;

        String funDecl = "def letFn" + Integer.toString(letId) +
            "(" + oirLet.getVarName() +"):\n" + indent + indentIncrement;
        state.expectingReturn = false;
        String toReplaceString = oirLet.getToReplace().acceptVisitor(this, state);
        String funCall = "\n" + indent + "letFn" +
            Integer.toString(letId) + "(" + toReplaceString + ")";

        state.expectingReturn = oldExpectingReturn;

        return (funDecl + inString + funCall);
    }

    public String visit(PrettyPrintState state,
                        OIRMethodCall oirMethodCall) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;
        String objExpr =
            oirMethodCall.getObjectExpr().acceptVisitor(this, state);
        String args = commaSeparatedExpressions(state,
                                                oirMethodCall.getArgs());
        String strVal = objExpr + "." + oirMethodCall.getMethodName() + "(" + args + ")";
        state.expectingReturn = oldExpectingReturn;
        if (state.expectingReturn)
            return "return " + strVal;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRNew oirNew) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;
        String args = commaSeparatedExpressions(state,
                                                oirNew.getArgs());
        state.expectingReturn = oldExpectingReturn;
        classesUsed.add(oirNew.getTypeName());
        return oirNew.getTypeName() + "(" + args + ")";
    }

    public String visit(PrettyPrintState state,
                        OIRRational oirRational) {
        return "OIRRational unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRString oirString) {
        String strVal = "\"" + oirString.getValue() + "\"";
        if (state.expectingReturn)
            return "return " + strVal;
        return strVal;
    }

    public String visit(PrettyPrintState state,
                        OIRVariable oirVariable) {
        if (state.expectingReturn)
            return "return " + oirVariable.getName();
        return oirVariable.getName();
    }

    public String visit(PrettyPrintState state,
                        OIRClassDeclaration oirClassDeclaration) {
        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = false;

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
            constructor_body.append(value.acceptVisitor(this, state));

            constructor_args.append(", " + dec.getName() + "Ignored");
        }
        members += "\n" + indent +
            "def __init__(this" + constructor_args.toString() + "):";
        members += constructor_body.toString();

        for (OIRMemberDeclaration memberDec : oirClassDeclaration.getMembers()) {
            members += "\n" + indent;
            if (memberDec instanceof OIRMethod) {
                OIRMethod method = (OIRMethod)memberDec;
                members += method.acceptVisitor(this, state);
            }
        }

        indent = oldIndent;

        state.expectingReturn = oldExpectingReturn;

        return classDef + members;
    }

    public String visit(PrettyPrintState state,
                        OIRProgram oirProgram) {
        return "OIRProgram unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRInterface oirInterface) {
        return "OIRInterface unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRFieldDeclaration oirFieldDeclaration) {
        return "OIRFieldDeclaration unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRMethodDeclaration oirMethodDeclaration) {
        return "OIRMethodDeclaration unimplemented";
    }

    public String visit(PrettyPrintState state,
                        OIRMethod oirMethod) {
        String args = "this";
        for (OIRFormalArg formalArg : oirMethod.getDeclaration().getArgs()) {
            args += ", " + formalArg.getName();
        }
        String def = "def " + oirMethod.getDeclaration().getName() +
            "(" + args + ")"+ ":";

        String oldIndent = indent;
        indent += indentIncrement;

        boolean oldExpectingReturn = state.expectingReturn;
        state.expectingReturn = true;

        String body = "\n" + indent +
            oirMethod.getBody().acceptVisitor(this, state);

        state.expectingReturn = oldExpectingReturn;
        indent = oldIndent;

        if (state.expectingReturn)
            return def + body + "\n" + indent + "return " + oirMethod.getDeclaration().getName();
        return def + body;
    }
}
