package wyvern.target.oir;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRCast;
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

public class PrettyPrintVisitor extends ASTVisitor<String> {
    String indent = "";
    final String indentIncrement = "  ";
    int uniqueId = 0;
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
        return (oirFieldGet.getObjectExpr() +
                "." +
                oirFieldGet.getFieldName());
    }

    public String visit(OIREnvironment oirenv,
                        OIRFieldSet oirFieldSet) {
        return (oirFieldSet.getObjectExpr() +
                "." +
                oirFieldSet.getFieldName() +
                " = " +
                oirFieldSet.getExprToAssign());
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
            "(" + oirLet.getVarName() +"):\n" + indent + indentIncrement;
        String toReplaceString = oirLet.getToReplace().acceptVisitor(this, oirenv);
        String funCall = "\n" + indent + "letFn" + Integer.toString(letId) +
            "(" + toReplaceString + ")";

        return (funDecl + inString + funCall);
    }

    public String visit(OIREnvironment oirenv,
                        OIRMethodCall oirMethodCall) {
        return "OIRMethodCall unimplemented";
    }

    public String visit(OIREnvironment oirenv,
                        OIRNew oirNew) {
        return "OIRNew unimplemented";
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
        return "OIRClassDeclaration unimplemented";
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
        return "OIRMethod unimplemented";
    }
}
