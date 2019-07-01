package wyvern.target.oir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.metadata.IsTailCall;
import wyvern.target.corewyvernIL.metadata.Metadata;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRForward;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.FFIType;
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
import wyvern.tools.util.GetterAndSetterGeneration;

class EmitPythonState {
    private OIREnvironment oirenv;
    private boolean expectingReturn;
    private String returnType;
    private int variableCounter;
    private HashMap<String, OIRClassDeclaration> classDecls;
    private HashSet<String> freeVarSet;
    private String currentMethod;
    private ArrayList<String> prefix;
    private HashMap<String, String> classRecursiveNames;
    private String currentLetVar;
    private boolean inClass;

    EmitPythonState copy() {
        EmitPythonState eps = new EmitPythonState();
        eps.oirenv = oirenv;
        eps.expectingReturn = expectingReturn;
        eps.returnType = returnType;
        eps.variableCounter = variableCounter;
        eps.classDecls = classDecls;
        eps.freeVarSet = freeVarSet;
        eps.currentMethod = currentMethod;
        eps.prefix = prefix;
        eps.classRecursiveNames = classRecursiveNames;
        eps.currentLetVar = currentLetVar;
        eps.inClass = inClass;
        return eps;
    }

    EmitPythonState withExpectingReturn(Boolean er) {
        EmitPythonState eps = this.copy();
        eps.expectingReturn = er;
        return eps;
    }

    protected OIREnvironment getOirenv() {
        return oirenv;
    }

    protected void setOirenv(OIREnvironment oirenv) {
        this.oirenv = oirenv;
    }

    protected boolean isExpectingReturn() {
        return expectingReturn;
    }

    protected void setExpectingReturn(boolean expectingReturn) {
        this.expectingReturn = expectingReturn;
    }

    protected String getReturnType() {
        return returnType;
    }

    protected void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    protected int getVariableCounter() {
        return variableCounter;
    }

    protected void setVariableCounter(int variableCounter) {
        this.variableCounter = variableCounter;
    }

    protected HashMap<String, OIRClassDeclaration> getClassDecls() {
        return classDecls;
    }

    protected void setClassDecls(HashMap<String, OIRClassDeclaration> classDecls) {
        this.classDecls = classDecls;
    }

    protected HashSet<String> getFreeVarSet() {
        return freeVarSet;
    }

    protected void setFreeVarSet(HashSet<String> freeVarSet) {
        this.freeVarSet = freeVarSet;
    }

    protected String getCurrentMethod() {
        return currentMethod;
    }

    protected void setCurrentMethod(String currentMethod) {
        this.currentMethod = currentMethod;
    }

    protected ArrayList<String> getPrefix() {
        return prefix;
    }

    protected void setPrefix(ArrayList<String> prefix) {
        this.prefix = prefix;
    }

    protected HashMap<String, String> getClassRecursiveNames() {
        return classRecursiveNames;
    }

    protected void setClassRecursiveNames(HashMap<String, String> classRecursiveNames) {
        this.classRecursiveNames = classRecursiveNames;
    }

    protected String getCurrentLetVar() {
        return currentLetVar;
    }

    protected void setCurrentLetVar(String currentLetVar) {
        this.currentLetVar = currentLetVar;
    }

    protected boolean isInClass() {
        return inClass;
    }

    protected void setInClass(boolean inClass) {
        this.inClass = inClass;
    }
}

public class EmitPythonVisitor extends ASTVisitor<EmitPythonState, String> {
    private String indent = "";
    private final String indentIncrement = "  ";
    private final String tcoPrefix = "tco_";
    private int uniqueId = 0;

    private HashSet<String> classesUsed;

    public EmitPythonVisitor() {
        classesUsed = new HashSet<String>();
    }

    private boolean isTailCall(OIRAST oirast) {
        for (Metadata m : oirast.getMetadata()) {
            if (m instanceof IsTailCall) {
                return true;
            }
        }
        return false;
    }

    private String commaSeparatedExpressions(EmitPythonState state, List<OIRExpression> exps) {
        String args = "";
        int nArgs = exps.size();
        for (int i = 0; i < nArgs; i++) {
            OIRExpression argI = exps.get(i);
            if (argI != null) {
                args += argI.acceptVisitor(this, state);
            } else {
                args += "None";
            }
            if (i < nArgs - 1) {
                args += ", ";
            }
        }
        return args;
    }

    private String stringFromPrefix(List<String> prefix, String indent) {
        String result = "\n" + indent;
        for (String line : prefix) {
            result += line + "\n" + indent;
        }
        return result;
    }

    private String generateVariable(EmitPythonState state) {
        state.setVariableCounter(state.getVariableCounter() + 1);
        return "var" + state.getVariableCounter();
    }

    private void findClassDecls(EmitPythonState state, OIREnvironment oirenv) {
        for (HashMap.Entry<String, OIRType> pair : oirenv.getTypeTable().entrySet()) {
            String name = pair.getKey();
            OIRType type = pair.getValue();
            if (type instanceof OIRClassDeclaration) {
                state.getClassDecls().put(name, (OIRClassDeclaration) NameMangleVisitor.mangleAST(type));
            }
        }
        for (OIREnvironment child : oirenv.getChildren()) {
            findClassDecls(state, child);
        }
    }

    public String emitPython(OIRAST oirast, OIREnvironment oirenv, boolean printResult) {
        String classDefs = "";
        EmitPythonState state = new EmitPythonState();
        state.setOirenv(oirenv);
        state.setExpectingReturn(false);
        state.setReturnType("return");
        state.setVariableCounter(0);
        state.setClassDecls(new HashMap<>());
        state.setFreeVarSet(new HashSet<>());
        state.setCurrentMethod("");
        state.setPrefix(new ArrayList<>());
        state.setInClass(false);
        state.setClassRecursiveNames(new HashMap<>());

        String prelude =
                "def mergeDicts(l, r):\n"
                      + "  l.update(r)\n"
                      + "  return r\n\n"
                      + "def trampoline(f, *args, **kwargs):\n"
                      + "  res = f(*args, **kwargs)\n"
                      + "  while callable(res):\n"
                      + "    res = res()\n"
                      + "  return res\n\n"
                      + "class PythonPrelude:\n"
                      + "  def toString(self, x):\n"
                      + "    return str(x)\n"
                      + "ffi_python = PythonPrelude()\n\n";

        findClassDecls(state, oirenv);
        String python = NameMangleVisitor.mangleAST(oirast).acceptVisitor(this, state);
        String[] lines = python.split("\\n");
        int lastIndex = lines.length - 1;
        if (printResult) {
            lines[lastIndex] = "print(" + lines[lastIndex] + ")";
        }
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            out.append(line);
            out.append("\n");
        }
        for (OIRClassDeclaration classDecl : state.getClassDecls().values()) {
            classDefs += classDecl.acceptVisitor(this, state) + "\n";
        }
        String prefix = stringFromPrefix(state.getPrefix(), "");
        return prelude + classDefs + prefix + out.toString();
    }

    public String visit(EmitPythonState state, OIRInteger oirInteger) {
        state.setCurrentLetVar("");
        String strVal = Integer.toString(oirInteger.getValue());
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + strVal;
        }
        return strVal;
    }

    public String visit(EmitPythonState state, OIRBoolean oirBoolean) {
        state.setCurrentLetVar("");
        String strVal = (oirBoolean.isValue() ? "True" : "False");
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + strVal;
        }
        return strVal;
    }

    public String visit(EmitPythonState state, OIRCast oirCast) {
        state.setCurrentLetVar("");
        return oirCast.getToCastEXpr().acceptVisitor(this, state);
    }

    public String visit(EmitPythonState state, OIRFieldGet oirFieldGet) {
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(false);
        String objExpr = oirFieldGet.getObjectExpr().acceptVisitor(this, state);
        String strVal = objExpr + "." + oirFieldGet.getFieldName();
        state.setExpectingReturn(oldExpectingReturn);
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + strVal;
        }
        return strVal;
    }

    public String visit(EmitPythonState state, OIRFieldSet oirFieldSet) {
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(false);
        String objExpr = oirFieldSet.getObjectExpr().acceptVisitor(this, state);
        String fieldName = oirFieldSet.getFieldName();

        // Setting a field: turn this into a method call to the appropriate setter method for that field.
        String setterName = GetterAndSetterGeneration.varNameToSetter(fieldName);

        String strVal;
        if (state.getCurrentMethod().equals(setterName) || state.getCurrentMethod().equals("tco_" + setterName)) {
            strVal = objExpr + "." + fieldName + " = " + oirFieldSet.getExprToAssign().acceptVisitor(this, state);
        } else {
            strVal = objExpr + "." + setterName + "(" + oirFieldSet.getExprToAssign().acceptVisitor(this, state) + ")";
        }
        state.setExpectingReturn(oldExpectingReturn);
        // TODO: Handle case where objExpr has side effects
        if (state.isExpectingReturn()) {
            return strVal + "\n" + indent + state.getReturnType() + " " + objExpr + "." + oirFieldSet.getFieldName();
        }
        return strVal;
    }

    public String visit(EmitPythonState state, OIRIfThenElse oirIfThenElse) {
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(false);
        String conditionString = oirIfThenElse.getCondition().acceptVisitor(this, state);
        state.setExpectingReturn(oldExpectingReturn);
        String oldIndent = indent;
        indent += indentIncrement;
        String thenString = oirIfThenElse.getThenExpression().acceptVisitor(this, state);
        String elseString = oirIfThenElse.getElseExpression().acceptVisitor(this, state);
        indent = oldIndent;
        return ("if " + conditionString + ":\n"
                + indent + indentIncrement + thenString + "\n"
                + indent + "else:\n"
                + indent + indentIncrement + elseString);
    }

    public String visit(EmitPythonState state, OIRLet oirLet) {
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(true);
        ArrayList<String> oldPrefix = state.getPrefix();
        state.setPrefix(new ArrayList<>());
        String oldIndent = indent;
        indent += indentIncrement;
        String inString = oirLet.getInExpr().acceptVisitor(this, state);
        if (state.getPrefix().size() > 0) {
            inString = stringFromPrefix(state.getPrefix(), indent) + "\n" + indent + inString;
            state.setPrefix(new ArrayList<>());
        }
        indent = oldIndent;
        int letId = uniqueId;
        uniqueId++;
        String funDecl = "def letFn" + Integer.toString(letId)
                       + "(" + oirLet.getVarName() + "):\n" + indent + indentIncrement;
        state.setExpectingReturn(false);
        state.setCurrentLetVar(oirLet.getVarName());
        String toReplaceString, prefix;
        if (oirLet.getToReplace() instanceof OIRFieldSet) {
            OIRFieldSet fieldSet = (OIRFieldSet) oirLet.getToReplace();
            String newVar = generateVariable(state);
            OIRFieldSet newFieldSet = new OIRFieldSet(new OIRVariable(newVar),
                    fieldSet.getFieldName(),
                    fieldSet.getExprToAssign());
            prefix = newVar + " = " + fieldSet.getObjectExpr().acceptVisitor(this, state)
                    + "\n" + indent + newFieldSet.acceptVisitor(this, state)
                    + "\n" + indent;
            toReplaceString = newVar;
        } else if (oirLet.getToReplace() instanceof OIRFFIImport) {
            OIRFFIImport oirImport = (OIRFFIImport) oirLet.getToReplace();
            if (oirImport.getFFIType() == FFIType.PYTHON) {
                prefix = oirImport.acceptVisitor(this, state) + "\n" + indent;
                toReplaceString = oirImport.getModule();
            } else {
                prefix = "\n" + indent;
                toReplaceString = "None";
            }
        } else if (oirLet.getToReplace() instanceof OIRLet) {
            OIRLet innerLet = (OIRLet) oirLet.getToReplace();
            int innerLetId = uniqueId;
            uniqueId++;
            prefix = "def letFn" + Integer.toString(innerLetId) + "():\n" + indent + indentIncrement;
            oldIndent = indent;
            indent = indent + indentIncrement;
            prefix += innerLet.acceptVisitor(this, state.withExpectingReturn(true));
            indent = oldIndent;
            prefix += "\n" + indent;
            toReplaceString = "letFn" + Integer.toString(innerLetId) + "()";
        } else {
            prefix = "";
            toReplaceString = oirLet.getToReplace().acceptVisitor(this, state);
        }
        String statePrefix = stringFromPrefix(state.getPrefix(), indent);
        state.setExpectingReturn(oldExpectingReturn);
        state.setPrefix(oldPrefix);

        String funCall = "\n" + indent;
        if (state.isExpectingReturn()) {
            funCall += state.getReturnType() + " ";
        }
        funCall += "letFn" + Integer.toString(letId) + "(" + toReplaceString + ")";

        return (prefix + funDecl + inString + statePrefix + funCall);
    }

    private Boolean methodCallIsIfStmt(OIRMethodCall oirMethodCall) {
        return oirMethodCall.getObjectType().equals(new NominalType("system", "Boolean"))
                && oirMethodCall.getMethodName().equals("ifTrue");
    }

    private String visitMethodCallTco(EmitPythonState state, OIRMethodCall oirMethodCall, Boolean tco) {
        String objExpr = oirMethodCall.getObjectExpr().acceptVisitor(this, state.withExpectingReturn(false));
        String args = commaSeparatedExpressions(state.withExpectingReturn(false), oirMethodCall.getArgs());
        String methodName = oirMethodCall.getMethodName();
        boolean isOperator = methodName.matches("[^a-zA-Z0-9]*");
        if (tco && !isOperator) {
            methodName = tcoPrefix + methodName;
        }

        ValueType objType = oirMethodCall.getObjectType();
        boolean isBool = objType.equals(new NominalType("system", "Boolean"));
        boolean isInt = objType.equals(new NominalType("system", "Int"));

        if (isBool && methodName.equals("||")) {
            return "(" + objExpr + " or " + args + ")";
        } else if (isBool && methodName.equals("&&")) {
            return "(" + objExpr + " and " + args + ")";
        } else if (isBool && methodName.equals("!")) {
            return "(not " + objExpr + ")";
        } else if (isInt && methodName.equals("/")) {
            // Make int division result in an int on Python 3
            return "int(" + objExpr + " " + methodName + " " + args + ")";
        } else if (isInt && (methodName.equals("negate")
                || methodName.equals("tco_negate"))) {
            return "-(" + objExpr + ")";
        } else {
            if (isOperator) {
                return "(" + objExpr + " " + methodName + " " + args + ")";
            } else {
                return objExpr + "." + methodName + "(" + args + ")";
            }
        }
    }

    private String wrapTcoTry(EmitPythonState state, String tryTco, String tryNoTco, String indent) {
        String retStr = "";
        if (state.isExpectingReturn()) {
            retStr = state.getReturnType() + " ";
        }
        return "try:\n"
               + indent + indentIncrement + retStr + tryTco + "\n"
               + indent + "except AttributeError:\n"
               + indent + indentIncrement + retStr + tryNoTco + "\n"
               + indent;
    }

    public String visit(EmitPythonState state, OIRMethodCall oirMethodCall) {
        state.setCurrentLetVar("");

        if (methodCallIsIfStmt(oirMethodCall)) {
            OIRExpression trueBranch = oirMethodCall.getArgs().get(0);
            OIRExpression falseBranch = oirMethodCall.getArgs().get(1);
            String objExpr = oirMethodCall.getObjectExpr().acceptVisitor(this, state.withExpectingReturn(false));
            String varName = generateVariable(state);
            String oldIndent = indent;
            indent = indent + indentIncrement;
            String trueText, falseText;

            if (state.isExpectingReturn()) {
                EmitPythonState stateNoReturn = state.withExpectingReturn(false);
                if (isTailCall(oirMethodCall)) {
                    trueText = indent + "return " + trueBranch.acceptVisitor(this, stateNoReturn) + ".tco_apply()\n";
                    falseText = indent + "return " + falseBranch.acceptVisitor(this, stateNoReturn) + ".tco_apply()\n";
                } else {
                    trueText = indent + "return " + trueBranch.acceptVisitor(this, stateNoReturn) + ".apply()\n";
                    falseText = indent + "return " + falseBranch.acceptVisitor(this, stateNoReturn) + ".apply()\n";
                }
            } else {
                trueText = indent + varName + " = " + trueBranch.acceptVisitor(this, state) + ".apply()\n";
                falseText = indent + varName + " = " + falseBranch.acceptVisitor(this, state) + ".apply()\n";
            }

            String pfx = "if " + objExpr + ":\n"
                       + trueText
                       + oldIndent + "else:\n"
                       + falseText
                       + oldIndent;

            if (state.isExpectingReturn()) {
                return pfx;
            }
            indent = oldIndent;
            state.getPrefix().add(pfx);
            return varName;
        }

        String resultNoTco = visitMethodCallTco(state, oirMethodCall, false);
        if (isTailCall(oirMethodCall)) {
            String resultTco = visitMethodCallTco(state, oirMethodCall, true);
            if (state.isExpectingReturn()) {
                int tcoId = uniqueId;
                uniqueId++;
                String tcoWrapper =
                        "def tcoFn" + Integer.toString(tcoId) + "():\n"
                                    + indent + indentIncrement + wrapTcoTry(state, resultTco, resultNoTco, indent + indentIncrement) + "\n"
                                    + indent;
                state.getPrefix().add(tcoWrapper);
                return state.getReturnType() + " " + "tcoFn" + tcoId;
            } else {
                return wrapTcoTry(state, resultTco, resultNoTco, indent);
            }
        } else {
            if (state.isExpectingReturn()) {
                return state.getReturnType() + " " + resultNoTco;
            } else {
                return resultNoTco;
            }
        }
    }

    public String visit(EmitPythonState state, OIRNew oirNew) {
        String letName = state.getCurrentLetVar();
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(false);

        String args = commaSeparatedExpressions(state, oirNew.getArgs());

        // Collect free variables
        OIRClassDeclaration decl = state.getClassDecls().get(oirNew.getTypeName());
        if (decl == null) {
            throw new RuntimeException("OIRNew called with class " + oirNew.getTypeName() + ", but no OIRClassDeclaration was found.");
        }
        Set<String> freeVars = decl.getFreeVariables();
        String dict;
        if (args.equals("")) {
            dict = "env=";
        } else {
            dict = ", env=";
        }
        if (state.isInClass()) {
            dict += "mergeDicts(" + NameMangleVisitor.mangle("this") + ".env, {";
        } else {
            dict += "({";
        }
        boolean first = true;
        for (String freeVar : freeVars) {
            if (!first) {
                dict += ", ";
            }
            first = false;
            dict += "'" + freeVar + "': " + (new OIRVariable(freeVar)).acceptVisitor(this, state);
        }
        dict += "})";

        String d = "";

        if (!decl.getForwards().isEmpty()) {
            OIRForward forward = decl.getForwards().get(0);
            d = ", forward=" + forward.getField();
        }

        String thisName = "";

        if (!letName.equals("")) {
            thisName = ", thisName = \"" + letName + "\"";
            state.getClassRecursiveNames().put(oirNew.getTypeName(), letName);
        }

        state.setExpectingReturn(oldExpectingReturn);
        classesUsed.add(oirNew.getTypeName());
        String constructorCall =
                oirNew.getTypeName() + "(" + args + dict + d + thisName + ")";
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + constructorCall;
        }
        return constructorCall;
    }

    public String visit(EmitPythonState state, OIRRational oirRational) {
        state.setCurrentLetVar("");
        return "OIRRational unimplemented";
    }

    private String escapeString(String str) {
        HashMap<String, String> replacements = new HashMap<String, String>() {
            /**
             * The serialization runtime associates with each serializable class a version number (serialVersionUID),
             * which is used during deserialization to verify that the sender and receiver of a serialized object
             * have loaded classes for that object that are compatible with respect to serialization.
             * This is an autogenerated version number.
             */
            private static final long serialVersionUID = 8765957517274753070L;
        {
            put("\"", "\\\"");
            put("\b", "\\b");
            put("\f", "\\f");
            put("\n", "\\n");
            put("\r", "\\r");
            put("\t", "\\t");
        }};
        str = str.replace("\\", "\\\\");
        for (String key : replacements.keySet()) {
            str = str.replace(key, replacements.get(key));
        }
        return str;
    }

    public String visit(EmitPythonState state, OIRString oirString) {
        state.setCurrentLetVar("");
        String strVal = "\"" + escapeString(oirString.getValue()) + "\"";
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + strVal;
        }
        return strVal;
    }
    
    public String visit(EmitPythonState state, OIRCharacter oirCharacter) {
        state.setCurrentLetVar("");
        String strVal = "\"" + oirCharacter.getValue() + "\"";
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + strVal;
        }
        return strVal;
    }

    public String visit(EmitPythonState state, OIRVariable oirVariable) {
        state.setCurrentLetVar("");
        String var;
        if (state.getFreeVarSet().contains(oirVariable.getName())) {
            var = NameMangleVisitor.mangle("this") + ".env['" + oirVariable.getName() + "']";
        } else {
            var = oirVariable.getName();
        }
        if (state.isExpectingReturn()) {
            return state.getReturnType() + " " + var;
        }
        return var;
    }

    public String visit(EmitPythonState state, OIRClassDeclaration oirClassDeclaration) {
        state.setCurrentLetVar("");
        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(false);
        boolean oldInClass = state.isInClass();
        state.setInClass(true);
        HashSet<String> oldFreeVarSet = state.getFreeVarSet();
        state.setFreeVarSet(new HashSet<String>(oirClassDeclaration.getFreeVariables()));
        String className = oirClassDeclaration.getName();

        if (state.getClassRecursiveNames().containsKey(className)) {
            state.getFreeVarSet().add(state.getClassRecursiveNames().get(className));
        }

        String classDef = "class " + className + ":";
        String oldIndent = indent;
        indent += indentIncrement;
        String members = "";

        // Build a constructor
        StringBuilder constructorArgs = new StringBuilder();
        StringBuilder constructorBody = new StringBuilder();
        for (OIRFieldValueInitializePair pair : oirClassDeclaration.getFieldValuePairs()) {
            OIRFieldDeclaration dec = pair.getFieldDeclaration();
            constructorBody.append("\n");
            constructorBody.append(indent + indentIncrement);
            constructorBody.append("this.");
            constructorBody.append(dec.getName());
            constructorBody.append(" = ");
            constructorBody.append(dec.getName());

            constructorArgs.append(", " + dec.getName());
        }
        members += "\n" + indent
                + "def __init__(this" + constructorArgs.toString() + ", env={}, forward=None, thisName=None):";
        members += "\n" + indent + indentIncrement + "this.env = env";
        members += "\n" + indent + indentIncrement + "this.forward = forward";
        members += "\n" + indent + indentIncrement + "if thisName is not None:";
        members += "\n" + indent + indentIncrement + indentIncrement + "this.env[thisName] = this";
        members += constructorBody.toString();

        if (!oirClassDeclaration.getForwards().isEmpty()) {
            members += "\n\n" + indent + "def __getattr__(self, name):";
            members += "\n" + indent + indentIncrement + "return getattr(self.forward, name)";
        }

        for (OIRMemberDeclaration memberDec : oirClassDeclaration.getMembers()) {
            members += "\n" + indent;
            if (memberDec instanceof OIRMethod) {
                OIRMethod method = (OIRMethod) memberDec;
                members += method.acceptVisitor(this, state);
            }
        }
        indent = oldIndent;
        state.setExpectingReturn(oldExpectingReturn);
        state.setFreeVarSet(oldFreeVarSet);
        state.setInClass(oldInClass);
        return classDef + members;
    }

    public String visit(EmitPythonState state, OIRProgram oirProgram) {
        state.setCurrentLetVar("");
        return "OIRProgram unimplemented";
    }

    public String visit(EmitPythonState state, OIRInterface oirInterface) {
        state.setCurrentLetVar("");
        return "OIRInterface unimplemented";
    }

    public String visit(EmitPythonState state, OIRFieldDeclaration oirFieldDeclaration) {
        state.setCurrentLetVar("");
        return "OIRFieldDeclaration unimplemented";
    }

    public String visit(EmitPythonState state, OIRMethodDeclaration oirMethodDeclaration) {
        state.setCurrentLetVar("");
        return "OIRMethodDeclaration unimplemented";
    }

    public String visit(EmitPythonState state, OIRMethod oirMethod) {
        state.setCurrentLetVar("");
        String args = NameMangleVisitor.mangle("this");
        String argsWithoutThis = "";
        OIRMethodDeclaration decl = oirMethod.getDeclaration();
        HashSet<String> oldFreeVarSet = state.getFreeVarSet();
        state.setFreeVarSet(new HashSet<String>(state.getFreeVarSet()));

        for (OIRFormalArg formalArg : decl.getArgs()) {
            argsWithoutThis += ", " + formalArg.getName();
            args += ", " + formalArg.getName();
            state.getFreeVarSet().remove(formalArg.getName());
        }
        String name = decl.getName();

        String trampolineBody =
                indent + indentIncrement + "return trampoline(" + NameMangleVisitor.mangle("this") + "."
                + tcoPrefix + name + argsWithoutThis + ")\n"
                + indent;
        String trampolineDecl = "def " + name + "(" + args + ")" + ":\n" + trampolineBody;

        String callFnDecl = "";
        if (name.equals("apply")) {
            // This is a callable. So, we add the __call__ function so that it can be used as an ordinary function.
            // This is required, for example, to make callback functions work in ROS.
            callFnDecl = "def __call__(" + args + "):\n" + trampolineBody;
        }
        name = tcoPrefix + name;

        String oldMethod = state.getCurrentMethod();
        state.setCurrentMethod(name);
        String def = "def " + name + "(" + args + ")" + ":";

        String oldIndent = indent;
        indent += indentIncrement;

        ArrayList<String> oldPrefix = state.getPrefix();
        state.setPrefix(new ArrayList<String>());

        boolean oldExpectingReturn = state.isExpectingReturn();
        state.setExpectingReturn(true);

        String body = "\n" + indent + oirMethod.getBody().acceptVisitor(this, state);

        String prefix = stringFromPrefix(state.getPrefix(), indent);
        state.setPrefix(oldPrefix);

        state.setExpectingReturn(oldExpectingReturn);
        indent = oldIndent;
        state.setCurrentMethod(oldMethod);
        state.setFreeVarSet(oldFreeVarSet);

        if (state.isExpectingReturn()) {
            return def + prefix + body + "\n"
                    + indent + state.getReturnType() + " " + name + "\n"
                    + indent + trampolineDecl;
        }
        return def + prefix + body + "\n" + indent + trampolineDecl + callFnDecl;
    }

    public String visit(EmitPythonState state, OIRFFIImport oirImport) {
        state.setCurrentLetVar("");
        if (oirImport.getFFIType() != FFIType.PYTHON) {
            return "Exception(\"Python backend does not support non-Python FFIs!\")";
            //throw new RuntimeException("Python backend does not support non-python FFIs!");
        }
        return "import " + oirImport.getModule();
    }

    public String visit(EmitPythonState state, OIRFloat oirFloat) {
      state.setCurrentLetVar("");
      String strVal = oirFloat.getValue().toString();
      if (state.isExpectingReturn()) {
          return state.getReturnType() + " " + strVal;
      }
      return strVal;
    }
}
