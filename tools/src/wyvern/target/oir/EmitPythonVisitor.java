package wyvern.target.oir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.metadata.HasMetadata;
import wyvern.target.corewyvernIL.metadata.Metadata;
import wyvern.target.corewyvernIL.metadata.IsTailCall;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
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

class EmitPythonState {
    public OIREnvironment oirenv;
    public boolean expectingReturn;
    public String returnType;
    public int variableCounter;
    public HashMap <String, OIRClassDeclaration> classDecls;
    public HashSet <String> freeVarSet;
    public String currentMethod;
    public ArrayList <String> prefix;
    public HashMap <String, String> classRecursiveNames;
    public String currentLetVar;
    public boolean inClass;

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
}

public class EmitPythonVisitor extends ASTVisitor<EmitPythonState, String> {
    String indent = "";
    final String indentIncrement = "  ";
    final String tco_prefix = "tco_";
    int uniqueId = 0;

  HashSet<String> classesUsed;

  public EmitPythonVisitor() {
    classesUsed = new HashSet<String>();
  }

    private boolean isTailCall(OIRAST oirast) {
        for (Metadata m : oirast.getMetadata()) {
            if (m instanceof IsTailCall) return true;
        }
        return false;
    }

  private String commaSeparatedExpressions(EmitPythonState state,
                                           List<OIRExpression> exps) {
    String args = "";
    int nArgs = exps.size();
    for (int i = 0; i < nArgs; i++) {
      OIRExpression arg_i = exps.get(i);
      if (args != null)
          args += arg_i.acceptVisitor(this, state);
      else
          args += "None";
      if (i < nArgs - 1)
        args += ", ";
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
    state.variableCounter++;
    return "var" + state.variableCounter;
  }

  private void findClassDecls(EmitPythonState state, OIREnvironment oirenv) {
    for (HashMap.Entry<String, OIRType> pair : oirenv.getTypeTable().entrySet()) {
      String name = pair.getKey();
      OIRType type = pair.getValue();
      if (type instanceof OIRClassDeclaration) {
          state.classDecls.put(name, (OIRClassDeclaration)NameMangleVisitor.mangleAST(type));
      }
    }
    for (OIREnvironment child : oirenv.getChildren())
      findClassDecls(state, child);
  }

  public String emitPython(OIRAST oirast,
                           OIREnvironment oirenv) {
    String classDefs = "";
    EmitPythonState state = new EmitPythonState();
    state.oirenv = oirenv;
    state.expectingReturn = false;
    state.returnType = "return";
    state.variableCounter = 0;
    state.classDecls = new HashMap<>();
    state.freeVarSet = new HashSet<>();
    state.currentMethod = "";
    state.prefix = new ArrayList<>();
    state.inClass = false;
    state.classRecursiveNames = new HashMap<>();

    String methodDecls =
        "def mergeDicts(l, r):\n" +
        "  l.update(r)\n" +
        "  return r\n\n" +
        "def trampoline(f, *args, **kwargs):\n" +
        "  res = f(*args, **kwargs)\n" +
        "  while callable(res):\n" +
        "    res = res()\n" +
        "  return res\n\n";

    findClassDecls(state, oirenv);

    String python =
      NameMangleVisitor.mangleAST(oirast).acceptVisitor(this, state);
    String[] lines = python.split("\\n");
    int lastIndex = lines.length-1;
    lines[lastIndex] = "print(" + lines[lastIndex] + ")";
    StringBuilder out = new StringBuilder();
    for (String line : lines) {
      out.append(line);
      out.append("\n");
    }

    for (OIRClassDeclaration classDecl : state.classDecls.values()) {
        classDefs += classDecl.acceptVisitor(this, state) + "\n";
    }

    String prefix = stringFromPrefix(state.prefix, "");

    return methodDecls + classDefs + prefix + out.toString();
  }

  public String visit(EmitPythonState state,
                      OIRInteger oirInteger) {
      state.currentLetVar = "";
    String strVal = Integer.toString(oirInteger.getValue());
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(EmitPythonState state,
                      OIRBoolean oirBoolean) {
      state.currentLetVar = "";
    String strVal = (oirBoolean.isValue() ? "True" : "False");
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(EmitPythonState state,
                      OIRCast oirCast) {
      state.currentLetVar = "";
    return "OIRCast unimplemented";
  }

  public String visit(EmitPythonState state,
                      OIRFieldGet oirFieldGet) {
      state.currentLetVar = "";
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;
    String objExpr =
      oirFieldGet.getObjectExpr().acceptVisitor(this, state);
    String strVal =
      (objExpr + "." +
       oirFieldGet.getFieldName());
    state.expectingReturn = oldExpectingReturn;
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(EmitPythonState state,
                      OIRFieldSet oirFieldSet) {
      state.currentLetVar = "";
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;
    String objExpr =
      oirFieldSet.getObjectExpr().acceptVisitor(this, state);
    String fieldName = oirFieldSet.getFieldName();
    String setterName =
      "set" + fieldName.substring(0, 1).toUpperCase() +
      fieldName.substring(1);

    String strVal;
    if (state.currentMethod.equals(setterName) ||
        state.currentMethod.equals("tco_" + setterName)) {
      strVal =
        objExpr + "." + fieldName + " = " +
        oirFieldSet.getExprToAssign().acceptVisitor(this, state);
    } else {
      strVal = objExpr + "." + setterName + "(" +
        oirFieldSet.getExprToAssign().acceptVisitor(this, state) + ")";
    }
    state.expectingReturn = oldExpectingReturn;
    // TODO: Handle case where objExpr has side effects
    if (state.expectingReturn)
      return strVal + "\n" + indent + state.returnType + " " + objExpr + "." +
        oirFieldSet.getFieldName();
    return strVal;
  }

  public String visit(EmitPythonState state,
                      OIRIfThenElse oirIfThenElse) {
      state.currentLetVar = "";
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

  public String visit(EmitPythonState state,
                      OIRLet oirLet) {
      state.currentLetVar = "";
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = true;
    ArrayList<String> oldPrefix = state.prefix;
    state.prefix = new ArrayList<>();

    String oldIndent = indent;
    indent += indentIncrement;
    String inString = oirLet.getInExpr().acceptVisitor(this, state);
    if (state.prefix.size() > 0) {
        inString = stringFromPrefix(state.prefix, indent) + "\n" + indent + inString;
        state.prefix = new ArrayList<>();
    }
    indent = oldIndent;

    int letId = uniqueId;
    uniqueId++;

    String funDecl = "def letFn" + Integer.toString(letId) +
      "(" + oirLet.getVarName() +"):\n" + indent + indentIncrement;
    state.expectingReturn = false;
    state.currentLetVar = oirLet.getVarName();
    String toReplaceString, prefix;
    if (oirLet.getToReplace() instanceof OIRFieldSet) {
      OIRFieldSet fieldSet = (OIRFieldSet)oirLet.getToReplace();
      String newVar = generateVariable(state);
      OIRFieldSet newFieldSet = new OIRFieldSet(new OIRVariable(newVar),
                                                fieldSet.getFieldName(),
                                                fieldSet.getExprToAssign());
      prefix = newVar + " = " + fieldSet.getObjectExpr().acceptVisitor(this, state)
        + "\n" + indent + newFieldSet.acceptVisitor(this, state)
        + "\n" + indent;
      toReplaceString = newVar;
    } else if (oirLet.getToReplace() instanceof OIRFFIImport) {
      OIRFFIImport oirImport = (OIRFFIImport)oirLet.getToReplace();
      prefix = oirImport.acceptVisitor(this, state) + "\n" + indent;
      toReplaceString = oirImport.getModule();
    } else if (oirLet.getToReplace() instanceof OIRLet) {
        OIRLet innerLet = (OIRLet)oirLet.getToReplace();
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
    String statePrefix = stringFromPrefix(state.prefix,
                                          indent);
    state.expectingReturn = oldExpectingReturn;
    state.prefix = oldPrefix;

    String funCall = "\n" + indent;
    if (state.expectingReturn)
        funCall += state.returnType + " ";
    funCall += "letFn" + Integer.toString(letId) + "(" + toReplaceString + ")";

    return (prefix + funDecl + inString + statePrefix + funCall);
  }

    private Boolean methodCallIsIfStmt(OIRMethodCall oirMethodCall) {
        return oirMethodCall.getObjectType().equals(new NominalType("system", "Boolean"))
            && oirMethodCall.getMethodName().equals("ifTrue");
    }

    private String visitMethodCallTco(EmitPythonState state,
                                      OIRMethodCall oirMethodCall,
                                      Boolean tco) {
        String objExpr = oirMethodCall.getObjectExpr().acceptVisitor(this, state.withExpectingReturn(false));
        String args = commaSeparatedExpressions(state.withExpectingReturn(false), oirMethodCall.getArgs());
        String methodName = oirMethodCall.getMethodName();
        if (tco)
            methodName = tco_prefix + methodName;
        boolean isOperator = methodName.matches("[^a-zA-Z0-9]*");

        ValueType objType = oirMethodCall.getObjectType();
        boolean isBool = objType.equals(new NominalType("system", "Boolean"));
        boolean isInt = objType.equals(new NominalType("system", "Int"));

        if (isBool && methodName.equals("||")) {
            return "(" + objExpr + " or " + args + ")";
        } else if (isBool && methodName.equals("&&")) {
            return "(" + objExpr + " and " + args + ")";
        } else if (isInt && methodName.equals("/")) {
            // Make int division result in an int on Python 3
            return "int(" + objExpr + " " + methodName + " " + args + ")";
        } else if (isInt && (methodName.equals("negate")
                             || methodName.equals("tco_negate"))) {
            return "-(" + objExpr + ")";
        } else {
            if (isOperator)
                return "(" + objExpr + " " + methodName + " " + args + ")";
            else
                return objExpr + "." + methodName + "(" + args + ")";
        }
    }

    private String wrapTcoTry(EmitPythonState state, String tryTco, String tryNoTco, String indent) {
        String retStr = "";
        if (state.expectingReturn) {
            retStr = state.returnType + " ";
        }
        return "try:\n" +
            indent + indentIncrement + retStr + tryTco + "\n" +
            indent + "except AttributeError:\n" +
            indent + indentIncrement + retStr + tryNoTco + "\n" +
            indent;
    }

    public String visit(EmitPythonState state,
                        OIRMethodCall oirMethodCall) {
        state.currentLetVar = "";

        if (methodCallIsIfStmt(oirMethodCall)) {
            OIRExpression trueBranch = oirMethodCall.getArgs().get(0);
            OIRExpression falseBranch = oirMethodCall.getArgs().get(1);

            String objExpr = oirMethodCall.getObjectExpr().acceptVisitor(this, state.withExpectingReturn(false));

            String varName = generateVariable(state);
            String oldIndent = indent;
            indent = indent + indentIncrement;

            String trueText, falseText;

            if (state.expectingReturn) {
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

            String pfx = "if " + objExpr + ":\n" +
                trueText +
                oldIndent + "else:\n" +
                falseText +
                oldIndent;

            if (state.expectingReturn) {
                return pfx;
            }
            indent = oldIndent;
            state.prefix.add(pfx);
            return varName;
        }

        String resultNoTco = visitMethodCallTco(state, oirMethodCall, false);
        if (isTailCall(oirMethodCall)) {
            String resultTco = visitMethodCallTco(state, oirMethodCall, true);
            if (state.expectingReturn) {
                int tcoId = uniqueId;
                uniqueId++;

                String tcoWrapper =
                    "def tcoFn" + Integer.toString(tcoId) + "():\n" +
                    indent + indentIncrement + wrapTcoTry(state, resultTco, resultNoTco, indent + indentIncrement) + "\n" +
                    indent;
                state.prefix.add(tcoWrapper);
                return state.returnType + " " + "tcoFn" + tcoId;
            } else {
                return wrapTcoTry(state, resultTco, resultNoTco, indent);
            }
        } else {
            if (state.expectingReturn) {
                return state.returnType + " " + resultNoTco;
            } else {
                return resultNoTco;
            }
        }
    }

  public String visit(EmitPythonState state,
                      OIRNew oirNew) {
      String letName = state.currentLetVar;
      state.currentLetVar = "";
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;


    String args = commaSeparatedExpressions(state,
                                            oirNew.getArgs());

    // Collect free variables
    OIRClassDeclaration decl = state.classDecls.get(oirNew.getTypeName());
    if (decl == null) {
      throw new RuntimeException("OIRNew called with class " + oirNew.getTypeName() + ", but no OIRClassDeclaration was found.");
    }
    Set<String> freeVars = decl.getFreeVariables();
    String dict;
    if (args.equals(""))
      dict = "env=";
    else
      dict = ", env=";
    if (state.inClass)
        dict += "mergeDicts(" + NameMangleVisitor.mangle("this") + ".env, {";
    else
        dict += "({";
    boolean first = true;
    for (String freeVar : freeVars) {
      if (!first)
        dict += ", ";
      first = false;

      OIRVariable var = new OIRVariable(freeVar);

      dict += "'" + freeVar + "': " + var.acceptVisitor(this, state);
    }
    dict += "})";

    String d = "";

    if (!decl.getDelegates().isEmpty()) {
      OIRDelegate delegate = decl.getDelegates().get(0);
      d = ", delegate=" + delegate.getField();
    }

    String thisName = "";

    if (!letName.equals("")) {
        thisName = ", thisName = \"" + letName + "\"";
        state.classRecursiveNames.put(oirNew.getTypeName(), letName);
    }

    state.expectingReturn = oldExpectingReturn;
    classesUsed.add(oirNew.getTypeName());
    String constructorCall =
        oirNew.getTypeName() + "(" + args + dict + d + thisName + ")";
    if (state.expectingReturn)
        return state.returnType + " " + constructorCall;
    return constructorCall;
  }

  public String visit(EmitPythonState state,
                      OIRRational oirRational) {
      state.currentLetVar = "";
    return "OIRRational unimplemented";
  }

    private String escapeString(String str) {
        HashMap<String,String> replacements = new HashMap<String, String>() {{
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

    public String visit(EmitPythonState state,
                        OIRString oirString) {
        state.currentLetVar = "";
        String stringValue = oirString.getValue();
        String strVal = "\"" + escapeString(stringValue) + "\"";
        if (state.expectingReturn)
            return state.returnType + " " + strVal;
        return strVal;
    }

  public String visit(EmitPythonState state,
                      OIRVariable oirVariable) {
      state.currentLetVar = "";
    String var;
    if (state.freeVarSet.contains(oirVariable.getName())) {
        var = NameMangleVisitor.mangle("this") +
            ".env['" + oirVariable.getName() + "']";
    } else {
      var = oirVariable.getName();
    }
    if (state.expectingReturn)
      return state.returnType + " " + var;
    return var;
  }

  public String visit(EmitPythonState state,
                      OIRClassDeclaration oirClassDeclaration) {
      state.currentLetVar = "";
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;
    boolean oldInClass = state.inClass;
    state.inClass = true;

    HashSet<String> oldFreeVarSet = state.freeVarSet;
    state.freeVarSet = new HashSet<String>(oirClassDeclaration.getFreeVariables());

    String className = oirClassDeclaration.getName();

    if (state.classRecursiveNames.containsKey(className)) {
        state.freeVarSet.add(state.classRecursiveNames.get(className));
    }

    String classDef = "class " + className + ":";
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
      constructor_body.append(dec.getName());

      constructor_args.append(", " + dec.getName());
    }
    members += "\n" + indent +
      "def __init__(this" + constructor_args.toString() + ", env={}, delegate=None, thisName=None):";
    members += "\n" + indent + indentIncrement + "this.env = env";
    members += "\n" + indent + indentIncrement + "this.delegate = delegate";
    members += "\n" + indent + indentIncrement + "if thisName is not None:";
    members += "\n" + indent + indentIncrement + indentIncrement + "this.env[thisName] = this";
    members += constructor_body.toString();

    if (!oirClassDeclaration.getDelegates().isEmpty()) {
      OIRDelegate delegate = oirClassDeclaration.getDelegates().get(0);
      members += "\n\n" + indent + "def __getattr__(self, name):";
      members += "\n" + indent + indentIncrement +
        "return getattr(self.delegate, name)";
    }

    for (OIRMemberDeclaration memberDec : oirClassDeclaration.getMembers()) {
      members += "\n" + indent;
      if (memberDec instanceof OIRMethod) {
        OIRMethod method = (OIRMethod)memberDec;
        members += method.acceptVisitor(this, state);
      }
    }

    indent = oldIndent;

    state.expectingReturn = oldExpectingReturn;
    state.freeVarSet = oldFreeVarSet;
    state.inClass = oldInClass;

    return classDef + members;
  }

  public String visit(EmitPythonState state,
                      OIRProgram oirProgram) {
      state.currentLetVar = "";
    return "OIRProgram unimplemented";
  }

  public String visit(EmitPythonState state,
                      OIRInterface oirInterface) {
      state.currentLetVar = "";
    return "OIRInterface unimplemented";
  }

  public String visit(EmitPythonState state,
                      OIRFieldDeclaration oirFieldDeclaration) {
      state.currentLetVar = "";
    return "OIRFieldDeclaration unimplemented";
  }

  public String visit(EmitPythonState state,
                      OIRMethodDeclaration oirMethodDeclaration) {
      state.currentLetVar = "";
    return "OIRMethodDeclaration unimplemented";
  }

  public String visit(EmitPythonState state,
                      OIRMethod oirMethod) {
      state.currentLetVar = "";
      String args = NameMangleVisitor.mangle("this");
      String argsWithoutThis = "";
      OIRMethodDeclaration decl = oirMethod.getDeclaration();
      boolean firstArg = true;
      for (OIRFormalArg formalArg : decl.getArgs()) {
          argsWithoutThis += ", " + formalArg.getName();
          args += ", " + formalArg.getName();
      }
      String name = decl.getName();
      String trampolineDecl = "";

      trampolineDecl =
          "def " + name + "(" + args + ")" + ":\n" +
          indent + indentIncrement + "return trampoline(" + NameMangleVisitor.mangle("this") + "." +
          tco_prefix + name + argsWithoutThis + ")\n"
          + indent;
      name = tco_prefix + name;

      String oldMethod = state.currentMethod;
      state.currentMethod = name;
      String def = "def " + name +
          "(" + args + ")"+ ":";

      String oldIndent = indent;
      indent += indentIncrement;

      ArrayList<String> oldPrefix = state.prefix;
      state.prefix = new ArrayList<String>();

      boolean oldExpectingReturn = state.expectingReturn;
      state.expectingReturn = true;

      String body = "\n" + indent +
          oirMethod.getBody().acceptVisitor(this, state);

      String prefix = stringFromPrefix(state.prefix, indent);
      state.prefix = oldPrefix;

      state.expectingReturn = oldExpectingReturn;
      indent = oldIndent;
      state.currentMethod = oldMethod;

      if (state.expectingReturn)
          return def + prefix + body + "\n"
              + indent + state.returnType + " " + name + "\n" + indent + trampolineDecl;
      return def + prefix + body + "\n" + indent + trampolineDecl;
  }

  public String visit(EmitPythonState state,
                      OIRFFIImport oirImport) {
      state.currentLetVar = "";
      if (oirImport.getFFIType() != FFIType.PYTHON) {
          return "Exception(\"Python backend does not support non-Python FFIs!\")";
          //throw new RuntimeException("Python backend does not support non-python FFIs!");
      }
      return "import " + oirImport.getModule();
  }
}
