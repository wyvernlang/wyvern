package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class Cast extends Expression{

	private Expression toCastExpr;

	
	public Cast(Expression toCastExpr, ValueType exprType) {
		super(exprType);
		this.toCastExpr = toCastExpr;
	}

	public Expression getToCastExpr() {
		return toCastExpr;
	}

	@Override
	public ValueType typeCheck(TypeContext env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			wyvern.target.corewyvernIL.Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
