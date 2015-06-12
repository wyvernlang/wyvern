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

public class EmitLLVMVisitor<T> extends EmitILVisitor<T> {

	@Override
	public T visit(Environment env, New newExpr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, MethodCall methodCall) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, Match match) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, FieldGet fieldGet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, Let let) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, FieldSet fieldSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, Variable variable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, Literal literal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, Cast cast) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, VarDeclaration varDecl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, DefDeclaration defDecl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T visit(Environment env, ValDeclaration valDecl) {
		// TODO Auto-generated method stub
		return null;
	}
}
