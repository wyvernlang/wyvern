package wyvern.target.oir;

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
  public HashSet<String> doNotMangle;
  public NameMangleState() {
    doNotMangle = new HashSet<String>();
  }
}

public class NameMangleVisitor extends ASTVisitor<NameMangleState, OIRAST> {

  public NameMangleVisitor() {
  }

  public static OIRAST mangleAST(OIRAST ast) {
    return ast.acceptVisitor(new NameMangleVisitor(),
                             new NameMangleState());
  }

  private String mangle(String name) {
    return "wyv" + name.substring(0,1).toUpperCase() + name.substring(1);
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
    return oirCast;
  }

  public OIRAST visit(NameMangleState state,
                      OIRFieldGet oirFieldGet) {
    return oirFieldGet;
  }

  public OIRAST visit(NameMangleState state,
                      OIRFieldSet oirFieldSet) {
    return oirFieldSet;
  }

  public OIRAST visit(NameMangleState state,
                      OIRIfThenElse oirIfThenElse) {
    return oirIfThenElse;
  }

  public OIRAST visit(NameMangleState state,
                      OIRLet oirLet) {
    return oirLet;
  }

  public OIRAST visit(NameMangleState state,
                      OIRMethodCall oirMethodCall) {
    return oirMethodCall;
  }

  public OIRAST visit(NameMangleState state,
                      OIRNew oirNew) {
    return oirNew;
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
    return oirVariable;
  }

  public OIRAST visit(NameMangleState state,
                      OIRClassDeclaration oirClassDeclaration) {
    return oirClassDeclaration;

  }

  public OIRAST visit(NameMangleState state,
                      OIRProgram oirProgram) {
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
    return oirMethodDeclaration;
  }

  public OIRAST visit(NameMangleState state,
                      OIRMethod oirMethod) {
    return oirMethod;

  }

  public OIRAST visit(NameMangleState state,
                      OIRFFIImport oirImport) {
    return oirImport;
  }
}
