package wyvern.target.oir;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.type.NominalType;
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

class PrettyPrintState {
    public OIREnvironment oirenv;
    public boolean expectingReturn;
    public String returnType;
    public int variableCounter;
    public HashMap <String, OIRClassDeclaration> classDecls;
    public HashSet <String> freeVarSet;
    public String currentMethod;
    public ArrayList <String> prefix;
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

    private String stringFromPrefix(List<String> prefix, String indent) {
        String result = "\n" + indent;
        for (String line : prefix) {
            result += line + "\n" + indent;
        }
        return result;
    }

  private String generateVariable(PrettyPrintState state) {
    state.variableCounter++;
    return "var" + state.variableCounter;
  }

  private void findClassDecls(PrettyPrintState state, OIREnvironment oirenv) {
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

  public String prettyPrint(OIRAST oirast,
                            OIREnvironment oirenv) {
    String classDefs = "";
    PrettyPrintState state = new PrettyPrintState();
    state.oirenv = oirenv;
    state.expectingReturn = false;
    state.returnType = "return";
    state.variableCounter = 0;
    state.classDecls = new HashMap<>();
    state.freeVarSet = new HashSet<>();
    state.currentMethod = "";
    state.prefix = new ArrayList<>();

    findClassDecls(state, oirenv);

    for (OIRClassDeclaration classDecl : state.classDecls.values()) {
      classDefs += classDecl.acceptVisitor(this, state) + "\n";
    }

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

    String prefix = stringFromPrefix(state.prefix, "");

    return classDefs + prefix + out.toString();
  }

  public String visit(PrettyPrintState state,
                      OIRInteger oirInteger) {
    String strVal = Integer.toString(oirInteger.getValue());
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(PrettyPrintState state,
                      OIRBoolean oirBoolean) {
    String strVal = (oirBoolean.isValue() ? "True" : "False");
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
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
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(PrettyPrintState state,
                      OIRFieldSet oirFieldSet) {
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;
    String objExpr =
      oirFieldSet.getObjectExpr().acceptVisitor(this, state);
    String fieldName = oirFieldSet.getFieldName();
    String setterName =
      "set" + fieldName.substring(0, 1).toUpperCase() +
      fieldName.substring(1);

    String strVal;
    if (state.currentMethod.equals(setterName)) {
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
    ArrayList<String> oldPrefix = state.prefix;
    state.prefix = new ArrayList<>();

    String oldIndent = indent;
    indent += indentIncrement;
    String inString = oirLet.getInExpr().acceptVisitor(this, state);
    indent = oldIndent;

    int letId = uniqueId;
    uniqueId++;

    String funDecl = "def letFn" + Integer.toString(letId) +
      "(" + oirLet.getVarName() +"):\n" + indent + indentIncrement;
    state.expectingReturn = false;
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
    } else {
      prefix = "";
      toReplaceString = oirLet.getToReplace().acceptVisitor(this, state);
    }
    String statePrefix = stringFromPrefix(state.prefix,
                                          indent + indentIncrement);
    state.expectingReturn = oldExpectingReturn;
    state.prefix = oldPrefix;

    String funCall = "\n" + indent;
    if (state.expectingReturn)
      funCall += state.returnType + " ";
     funCall += "letFn" + Integer.toString(letId) + "(" + toReplaceString + ")";

    return (prefix + funDecl + statePrefix + inString + funCall);
  }

  public String visit(PrettyPrintState state,
                      OIRMethodCall oirMethodCall) {
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;
    String objExpr =
      oirMethodCall.getObjectExpr().acceptVisitor(this, state);
    String args = commaSeparatedExpressions(state,
                                            oirMethodCall.getArgs());
    String methodName = oirMethodCall.getMethodName();
    String strVal;

    boolean isBool =
        oirMethodCall.getObjectType().equals(new NominalType("system",
                                                             "Boolean"));
    boolean isInt =
        oirMethodCall.getObjectType().equals(new NominalType("system",
                                                             "Int"));

    if (isBool && methodName.equals("ifTrue")) {
        OIRExpression trueBranch = oirMethodCall.getArgs().get(0);
        OIRExpression falseBranch = oirMethodCall.getArgs().get(1);

        String varName = generateVariable(state);
        String oldIndent = indent;
        indent = indent + indentIncrement;
        String pfx = "if " + objExpr + ":\n" +
            indent + varName + " = " + trueBranch.acceptVisitor(this, state) + ".apply()\n" +
            oldIndent + "else:\n" +
            indent + varName + " = " + falseBranch.acceptVisitor(this, state) + ".apply()\n" +
            oldIndent;
        indent = oldIndent;
        state.prefix.add(pfx);
        strVal = varName;
    } else {
        if (methodName.matches("[^a-zA-Z0-9]*"))
            strVal = "(" + objExpr + " " +
                methodName + " " + args + ")";
        else
            strVal = objExpr + "." + methodName + "(" + args + ")";
    }
    state.expectingReturn = oldExpectingReturn;
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(PrettyPrintState state,
                      OIRNew oirNew) {
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
      dict = "env={";
    else
      dict = ", env={";
    boolean first = true;
    for (String freeVar : freeVars) {
      if (!first)
        dict += ", ";
      first = false;

      dict += "'" + freeVar + "': " + freeVar;
    }
    dict += "}";

    String d = "";

    if (!decl.getDelegates().isEmpty()) {
      OIRDelegate delegate = decl.getDelegates().get(0);
      d = ", delegate=" + delegate.getField();
    }

    state.expectingReturn = oldExpectingReturn;
    classesUsed.add(oirNew.getTypeName());
    return oirNew.getTypeName() + "(" + args + dict + d + ")";
  }

  public String visit(PrettyPrintState state,
                      OIRRational oirRational) {
    return "OIRRational unimplemented";
  }

  public String visit(PrettyPrintState state,
                      OIRString oirString) {
    String strVal = "\"" + oirString.getValue() + "\"";
    if (state.expectingReturn)
      return state.returnType + " " + strVal;
    return strVal;
  }

  public String visit(PrettyPrintState state,
                      OIRVariable oirVariable) {
    String var;
    if (state.freeVarSet.contains(oirVariable.getName()))
        var = NameMangleVisitor.mangle("this") +
            ".env['" + oirVariable.getName() + "']";
    else
      var = oirVariable.getName();
    if (state.expectingReturn)
      return state.returnType + " " + var;
    return var;
  }

  public String visit(PrettyPrintState state,
                      OIRClassDeclaration oirClassDeclaration) {
    boolean oldExpectingReturn = state.expectingReturn;
    state.expectingReturn = false;

    HashSet<String> oldFreeVarSet = state.freeVarSet;
    state.freeVarSet = new HashSet<String>(oirClassDeclaration.getFreeVariables());

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
      constructor_body.append(dec.getName());

      constructor_args.append(", " + dec.getName());
    }
    members += "\n" + indent +
      "def __init__(this" + constructor_args.toString() + ", env={}, delegate=None):";
    members += "\n" + indent + indentIncrement + "this.env = env";
    members += "\n" + indent + indentIncrement + "this.delegate = delegate";
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
      String args = NameMangleVisitor.mangle("this");
      OIRMethodDeclaration decl = oirMethod.getDeclaration();
      for (OIRFormalArg formalArg : decl.getArgs()) {
          args += ", " + formalArg.getName();
      }
      String name = decl.getName();
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
              + indent + state.returnType + " " + decl.getName();
      return def + prefix + body;
  }

  public String visit(PrettyPrintState state,
                      OIRFFIImport oirImport) {
    if (oirImport.getFFIType() != FFIType.PYTHON) {
      throw new RuntimeException("Python backend does not support non-python FFIs!");
    }
    return "import " + oirImport.getModule();
  }
}
