package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public abstract class AbstractValue extends Expression implements Value {
	protected AbstractValue(ValueType exprType) {
		super(exprType);
	}


	@Override
	public Value interpret(EvalContext ctx) {
		return this;
	}
}
