package wyvern.target.oir;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRFieldDeclaration;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.expressions.OIRBoolean;
import wyvern.target.oir.expressions.OIRCast;
import wyvern.target.oir.expressions.OIRCharacter;
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

public abstract class ASTVisitor<S, T> {
    public abstract T visit(S state, OIRInteger oirInteger);
    public abstract T visit(S state, OIRFloat oirFloat);
    public abstract T visit(S state, OIRBoolean oirBoolean);
    public abstract T visit(S state, OIRCast oirCast);
    public abstract T visit(S state, OIRFFIImport oirFFIImport);
    public abstract T visit(S state, OIRFieldGet oirFieldGet);
    public abstract T visit(S state, OIRFieldSet oirFieldSet);
    public abstract T visit(S state, OIRIfThenElse oirIfThenElse);
    public abstract T visit(S state, OIRLet oirLet);
    public abstract T visit(S state, OIRMethodCall oirMethodCall);
    public abstract T visit(S state, OIRNew oirNew);
    public abstract T visit(S state, OIRRational oirRational);
    public abstract T visit(S state, OIRString oirString);
    public abstract T visit(S state, OIRCharacter oirCharacter);
    public abstract T visit(S state, OIRVariable oirVariable);
    public abstract T visit(S state, OIRClassDeclaration oirClassDeclaration);
    public abstract T visit(S state, OIRProgram oirProgram);
    public abstract T visit(S state, OIRInterface oirInterface);
    public abstract T visit(S state, OIRFieldDeclaration oirFieldDeclaration);
    public abstract T visit(S state, OIRMethodDeclaration oirMethodDeclaration);
    public abstract T visit(S state, OIRMethod oirMethod);
}
