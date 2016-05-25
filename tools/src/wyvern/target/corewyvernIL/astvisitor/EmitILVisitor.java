package wyvern.target.corewyvernIL.astvisitor;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Literal;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;

public abstract class EmitILVisitor<T> extends ASTVisitor<T, Environment> {

	public abstract T visit (Environment env, New newExpr);
	public abstract T visit (Environment env, MethodCall methodCall);
	public abstract T visit (Environment env, Match match);
	public abstract T visit (Environment env, FieldGet fieldGet);
	public abstract T visit (Environment env, Let let);
	public abstract T visit (Environment env, FieldSet fieldSet);
	public abstract T visit (Environment env, Variable variable);
	public abstract T visit (Environment env, Literal literal);
	public abstract T visit (Environment env, Cast cast);
	public abstract T visit (Environment env, VarDeclaration varDecl);
	public abstract T visit (Environment env, DefDeclaration defDecl);
	public abstract T visit (Environment env, ValDeclaration valDecl);
}
