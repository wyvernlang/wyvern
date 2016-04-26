package wyvern.target.corewyvernIL.astvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FFIImport;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.CaseType;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRProgram;
import wyvern.target.oir.OIRTypeBinding;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRDelegate;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRFieldValueInitializePair;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRIntegerType;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRMethodDeclarationGroup;
import wyvern.target.oir.declarations.OIRType;
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
import wyvern.tools.tests.tagTests.TestUtil;

public class EmitOIRVisitor extends ASTVisitor<OIRAST> {
  private int classCount = 0;
  private int interfaceCount = 0;

  private String generateClassName ()
  {
    classCount++;
    return "Class"+classCount;
  }

  private String generateInterfaceName ()
  {
    interfaceCount++;
    return "Interface"+interfaceCount;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv, New newExpr) {
    OIRClassDeclaration cd;
    ValueType exprType;
    OIRType oirtype;
    OIREnvironment classenv;
    List<OIRMemberDeclaration> oirMemDecls;
    String className;
    List<OIRDelegate> delegates;
    OIRTypeBinding oirTypeBinding;
    OIRExpression oirexpr;
    List<OIRFieldValueInitializePair> fieldValuePairs;
    List<OIRExpression> args;

    // exprType = newExpr.getExprType();
    // if (exprType != null)
    //   oirtype = (OIRType) exprType.acceptVisitor(this, env, oirenv);
    classenv = new OIREnvironment (oirenv);
    oirMemDecls = new Vector<OIRMemberDeclaration> ();
    delegates = new Vector<OIRDelegate> ();
    fieldValuePairs = new Vector<OIRFieldValueInitializePair> ();
    args = new Vector<OIRExpression> ();

    for (Declaration decl : newExpr.getDecls())
    {
      if (decl instanceof DelegateDeclaration)
      {
        OIRDelegate oirdelegate;

        oirdelegate = (OIRDelegate)decl.acceptVisitor(this, env,
            classenv);
        delegates.add(oirdelegate);
      }
      else
      {
        OIRMemberDeclaration oirMemDecl;

        oirMemDecl = (OIRMemberDeclaration) decl.acceptVisitor(this,
            env, classenv);

        if (decl instanceof VarDeclaration)
        {
          OIRExpression oirvalue;
          OIRFieldValueInitializePair pair;
          VarDeclaration varDecl;

          varDecl = (VarDeclaration)decl;
          oirvalue = (OIRExpression) varDecl.getDefinition().acceptVisitor(this,
              env, oirenv);
          pair = new OIRFieldValueInitializePair (
              (OIRFieldDeclaration)oirMemDecl, oirvalue);
          fieldValuePairs.add (pair);
          args.add(oirvalue);
        }
        else if (decl instanceof ValDeclaration)
        {
          OIRExpression oirvalue;
          OIRFieldValueInitializePair pair;
          ValDeclaration varDecl;

          varDecl = (ValDeclaration)decl;
          oirvalue = (OIRExpression) varDecl.getDefinition().acceptVisitor(this,
              env, oirenv);
          pair = new OIRFieldValueInitializePair (
              (OIRFieldDeclaration)oirMemDecl, oirvalue);
          fieldValuePairs.add (pair);
          args.add(oirvalue);
        }

        classenv.addName(oirMemDecl.getName (), oirMemDecl.getType ());
        oirMemDecls.add(oirMemDecl);
      }
    }

    className = generateClassName ();
    cd = new OIRClassDeclaration (classenv, className, newExpr.getSelfName(),
                                  delegates, oirMemDecls, fieldValuePairs,
                                  newExpr.getFreeVariables());
    oirenv.addType(className, cd);
    classenv.addName(newExpr.getSelfName(), cd);
    OIRProgram.program.addTypeDeclaration(cd);
    oirexpr = new OIRNew (args, className);

    return oirexpr;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv, MethodCall methodCall) {
    OIRExpression oirbody;
    Expression body;
    List<OIRExpression> args;
    OIRMethodCall oirMethodCall;

    args =	new Vector<OIRExpression> ();

    for (Expression e : methodCall.getArgs())
    {
      args.add ((OIRExpression)e.acceptVisitor(this, env, oirenv));
    }

    body = methodCall.getObjectExpr();

    oirbody = (OIRExpression)body.acceptVisitor(this, env, oirenv);
    oirMethodCall = new OIRMethodCall (oirbody,
        methodCall.getMethodName(),  args);

    return oirMethodCall;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, Match match) {
    OIRLet oirParentLet;
    OIRIfThenElse oirIfExpr;
    OIRLet oirThenLet;
    OIRExpression oirElseExpr;
    OIRExpression oirMatchExpr;

    oirMatchExpr = (OIRExpression) match.getMatchExpr().acceptVisitor(this,
        env, oirenv);
    oirElseExpr = (OIRExpression) match.getElseExpr().acceptVisitor(this,
        env, oirenv);

    /* Build the let in if let in else let in if chain bottom up */
    for (int i = match.getCases().size() - 1; i >= 0; i--)
    {
      Case matchCase;
      OIRExpression oirTag;
      OIRExpression body;
      OIRExpression condition;
      List<OIRExpression> arg;

      matchCase = match.getCases().get(i);
      body = (OIRExpression)matchCase.getBody().acceptVisitor(this,
          env, oirenv);
      oirTag = (OIRExpression)matchCase.getPattern().acceptVisitor(
          this, env, oirenv);
      oirThenLet = new OIRLet (matchCase.getVarName(), oirMatchExpr, body);
      arg = new Vector<OIRExpression> ();
      arg.add(oirTag);
      condition = new OIRMethodCall (new OIRFieldGet (
          new OIRVariable ("tmp"), "tag"),
          "isSubtag", arg);
      oirIfExpr = new OIRIfThenElse (condition, oirThenLet, oirElseExpr);
      oirElseExpr = oirIfExpr;
    }

    oirParentLet = new OIRLet ("tmp", oirMatchExpr, oirElseExpr);
    return oirParentLet;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, FieldGet fieldGet) {
    OIRFieldGet oirFieldGet;
    OIRExpression oirObject;
    Expression object;

    object = (Expression) fieldGet.getObjectExpr();
    oirObject = (OIRExpression) object.acceptVisitor(this,
        env, oirenv);
    oirFieldGet = new OIRFieldGet (oirObject, fieldGet.getName());

    return oirFieldGet;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, Let let) {

    OIRLet oirLet;
    OIRExpression oirToReplace;
    OIRExpression oirInExpr;
    Expression toReplace;
    Expression inExpr;

    toReplace = let.getToReplace();
    oirToReplace = (OIRExpression)toReplace.acceptVisitor(this,
        env, oirenv);
    oirenv.addName(let.getVarName(), null);
    inExpr = let.getInExpr();
    oirInExpr = (OIRExpression)inExpr.acceptVisitor(this, env, oirenv);
    oirLet = new OIRLet (let.getVarName(), oirToReplace, oirInExpr);

    return oirLet;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, FieldSet fieldSet) {
    OIRFieldSet oirFieldSet;
    OIRExpression oirObject;
    OIRExpression oirToSet;
    Expression object;
    Expression toSet;

    object = (Expression) fieldSet.getObjectExpr();
    toSet = fieldSet.getExprToAssign();
    oirObject = (OIRExpression) object.acceptVisitor(this, env,
        oirenv);
    oirToSet = (OIRExpression) toSet.acceptVisitor(this, env,
        oirenv);
    oirFieldSet = new OIRFieldSet (oirObject, fieldSet.getFieldName(),
        oirToSet);

    return oirFieldSet;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, Variable variable) {
    OIRVariable oirVar;
    OIRType oirType;
    ValueType type;

    oirVar = new OIRVariable (variable.getName());

    return oirVar;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, Cast cast) {
    OIRCast oirCast;
    OIRType oirType;
    OIRExpression oirExpr;
    Expression expr;

    expr = cast.getToCastExpr();
    oirExpr = (OIRExpression)expr.acceptVisitor(this, env, oirenv);
    oirType = (OIRType)cast.getExprType().acceptVisitor(this, env,
        oirenv);
    oirCast = new OIRCast (oirExpr, oirType);

    return oirCast;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv, VarDeclaration varDecl) {
    OIRFieldDeclaration oirMember;
    OIRType type;
    ValueType _type;

    _type = varDecl.getType();
    type = (OIRType)_type.acceptVisitor(this, env, oirenv);
    oirMember = new OIRFieldDeclaration (varDecl.getName(), type);
    return oirMember;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv, DefDeclaration defDecl) {
    OIRMethodDeclaration oirMethodDecl;
    OIRMethod oirMethod;
    OIRType oirReturnType;
    List<OIRFormalArg> listOIRFormalArgs;
    OIRExpression oirBody;
    OIREnvironment defEnv;

    listOIRFormalArgs = new Vector <OIRFormalArg> ();
    defEnv = new OIREnvironment (oirenv);

    for (FormalArg arg : defDecl.getFormalArgs())
    {
      OIRFormalArg formalArg;

      formalArg = (OIRFormalArg) arg.acceptVisitor(this, env,
          oirenv);
      defEnv.addName(formalArg.getName(), formalArg.getType());
      listOIRFormalArgs.add(formalArg);
    }

    // oirReturnType = (OIRType) defDecl.getType().acceptVisitor(this,
    //     env, oirenv);
    oirMethodDecl = new OIRMethodDeclaration (null,
        defDecl.getName(), listOIRFormalArgs);
    oirBody = (OIRExpression) defDecl.getBody().acceptVisitor(this,
        env, defEnv);
    oirMethod = new OIRMethod (defEnv, oirMethodDecl, oirBody);

    return oirMethod;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv, ValDeclaration valDecl) {
    OIRFieldDeclaration oirMember;
    OIRType type;
    ValueType _type;

    _type = valDecl.getType();
    type = (OIRType)_type.acceptVisitor(this, env, oirenv);
    oirMember = new OIRFieldDeclaration (valDecl.getName(), type, true);

    return oirMember;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      IntegerLiteral integerLiteral) {

    return new OIRInteger (integerLiteral.getValue());
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      RationalLiteral rational) {
    return new OIRRational (rational.getNumerator(),
        rational.getDenominator());
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      FormalArg formalArg) {
    OIRType oirtype;
    OIRFormalArg oirarg;

    // oirtype = (OIRType) formalArg.getType().acceptVisitor(this,
    //     env, oirenv);
    oirarg = new OIRFormalArg (formalArg.getName(), null);
    return oirarg;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      VarDeclType varDeclType) {
    OIRInterface oirtype;
    ValueType type;
    OIRMethodDeclarationGroup methoDecls;
    List<OIRFormalArg> args;
    String fieldName;

    fieldName = varDeclType.getName();
    methoDecls = new OIRMethodDeclarationGroup ();
    type = varDeclType.getRawResultType();
    oirtype = (OIRInterface)type.acceptVisitor(this, env, oirenv);
    methoDecls.addMethodDeclaration(new OIRMethodDeclaration (oirtype,
        "get"+fieldName, null));
    args = new Vector<OIRFormalArg> ();
    args.add(new OIRFormalArg ("_"+fieldName, oirtype));
    methoDecls.addMethodDeclaration(new OIRMethodDeclaration (null,
        "set"+fieldName, args));

    return methoDecls;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv,
      ValDeclType valDeclType) {
    OIRInterface oirtype;
    ValueType type;
    OIRMethodDeclaration methodDecl;
    OIRMethodDeclarationGroup methoDecls;

    type = valDeclType.getRawResultType();
    oirtype = (OIRInterface)type.acceptVisitor(this, env, oirenv);
    methodDecl = new OIRMethodDeclaration (oirtype,
        "set"+valDeclType.getName(), null);
    methoDecls = new OIRMethodDeclarationGroup ();
    methoDecls.addMethodDeclaration(methodDecl);

    return methoDecls;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      DefDeclType defDeclType) {
    OIRMethodDeclaration oirMethodDecl;
    OIRType oirReturnType;
    List<OIRFormalArg> listOIRFormalArgs;
    ValueType returnType;
    OIRMethodDeclarationGroup methodDecls;

    listOIRFormalArgs = new Vector <OIRFormalArg> ();

    for (FormalArg arg : defDeclType.getFormalArgs())
    {
      OIRFormalArg formalArg;

      formalArg = (OIRFormalArg) arg.acceptVisitor(this, env,
          oirenv);
      listOIRFormalArgs.add(formalArg);
    }

    returnType = defDeclType.getRawResultType();
    oirReturnType = (OIRType)returnType.acceptVisitor(this,
        env, oirenv);
    oirMethodDecl = new OIRMethodDeclaration (oirReturnType,
        defDeclType.getName(), listOIRFormalArgs);
    methodDecls = new OIRMethodDeclarationGroup ();
    methodDecls.addMethodDeclaration(oirMethodDecl);

    return methodDecls;
  }


  public OIRAST visit(Environment env, OIREnvironment oirenv,
      AbstractTypeMember abstractDeclType) {
    OIRType oirtype;
    OIRMethodDeclaration methDecl;
    OIRMethodDeclarationGroup methodDecls;

    oirtype = (OIRType) abstractDeclType.acceptVisitor(this,
        env, oirenv);
    methDecl = new OIRMethodDeclaration (oirtype, "get"+abstractDeclType.getName(), null);
    methodDecls = new OIRMethodDeclarationGroup ();
    methodDecls.addMethodDeclaration(methDecl);

    return methodDecls;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv,
      StructuralType structuralType) {
    OIRInterface oirinterface;
    List<OIRMethodDeclaration> methodDecls;
    String interfaceName;
    OIREnvironment oirInterfaceEnv;

    interfaceName = generateInterfaceName();
    methodDecls = new Vector<OIRMethodDeclaration> ();
    oirInterfaceEnv = new OIREnvironment (oirenv);

    for (DeclType declType : structuralType.getDeclTypes())
    {
      OIRMethodDeclarationGroup declTypeGroup;

      OIRAST declAST = declType.acceptVisitor(this, env, oirenv);
      declTypeGroup = (OIRMethodDeclarationGroup) declAST;
      for (int i = 0; i < declTypeGroup.size(); i++)
      {
        oirInterfaceEnv.addName(declTypeGroup.elementAt(i).getName(),
            declTypeGroup.elementAt(i).getReturnType());
        methodDecls.add (declTypeGroup.elementAt(i));
      }
    }

    oirinterface = new OIRInterface (oirInterfaceEnv, interfaceName,
        structuralType.getSelfName(), methodDecls);
    oirenv.addType(interfaceName, oirinterface);
    OIRProgram.program.addTypeDeclaration(oirinterface);

    return oirinterface;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv,
      NominalType nominalType) {
    // Note: This code belongs more in the Match case
    // OIRExpression oirfieldget;
    // Path path;

    // path = nominalType.getPath();
    // oirfieldget = (OIRExpression) path.acceptVisitor(this, env, oirenv);

    // return new OIRFieldGet (oirfieldget, nominalType.getTypeMember()+"tag");

    StructuralType defaultType =
        new StructuralType("emptyType",
                           new ArrayList<DeclType>());
    return defaultType.acceptVisitor(this, env, oirenv);

    // TODO: This should also take into account types available in the OIREnvironment
    // TypeContext context = TestUtil.getStandardGenContext();

    // StructuralType st = nominalType.getStructuralType(context,
    //                                                   defaultType);
    // return st.acceptVisitor(this, env, oirenv);
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv,
      StringLiteral stringLiteral) {
    OIRString oirstring;

    oirstring = new OIRString (stringLiteral.getValue());

    return oirstring;
  }

  public OIRAST visit(Environment env, OIREnvironment oirenv,
      DelegateDeclaration delegateDecl) {
    OIRDelegate oirdelegate;
    OIRType oirtype;
    ValueType type;

    // type = delegateDecl.getValueType();
    // oirtype = (OIRType) type.acceptVisitor(this, env, oirenv);
    oirdelegate = new OIRDelegate (null, delegateDecl.getFieldName());

    return oirdelegate;
  }

  @Override
  public OIRAST visit(Environment env, OIREnvironment oirenv, Bind bind) {
    throw new RuntimeException("not implemented");
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      ConcreteTypeMember concreteTypeMember) {
    /*OIRType type = (OIRType)concreteTypeMember.getRawResultType()
      .acceptVisitor(this, env, oirenv);
    oirenv.addType(concreteTypeMember.getName(), type);

    return type;*/

  return new OIRMethodDeclarationGroup();
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      TypeDeclaration typeDecl) {
    // the tag field
    return new OIRFieldDeclaration(typeDecl.getName()+"tag",
                                   OIRIntegerType.getIntegerType());
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      CaseType caseType) {
    throw new RuntimeException("CaseType -> OIR unimplemented");
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      ExtensibleTagType extensibleTagType) {
    throw new RuntimeException("ExtensibleTagType -> OIR unimplemented");
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      DataType dataType) {
    throw new RuntimeException("DataType -> OIR unimplemented");
  }

  @Override
  public OIRAST visit(Environment env,
                      OIREnvironment oirenv,
                      FFIImport ffiImport) {
    NominalType javaType = new NominalType("system", "Java");
    NominalType pythonType = new NominalType("system", "Python");

    if (ffiImport.getFFIType().equals(javaType)) {
      System.out.println("Java FFI!");
      return new OIRFFIImport(FFIType.JAVA, ffiImport.getPath());
    } else if (ffiImport.getFFIType().equals(pythonType)) {
      return new OIRFFIImport(FFIType.PYTHON, ffiImport.getPath());
    } else {
      throw new RuntimeException("Unknown FFI type!");
    }
  }

}
