package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class ValDeclaration extends DeclarationWithRHS {

	@Override
	public String toString() {
		return "ValDeclaration[" + getName() + " : " + type + " = " + getDefinition() + "]";
	}

	public ValDeclaration(String fieldName, ValueType type, Expression value) {
		super(fieldName, value);
		this.type = type;
	}

	private ValueType type;
	
	public ValueType getType() {
		return type;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public DeclType typeCheck(TypeContext ctx) {
		return new ValDeclType(getName(), type);
	}
	
	@Override
	public Declaration interpret(EvalContext ctx) {
		Expression newValue = (Expression) getDefinition().interpret(ctx);
		return new ValDeclaration(getName(), type, newValue);
	}

}
