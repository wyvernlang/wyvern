package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;

public class VarDeclaration extends DeclarationWithRHS {

	private ValueType type;
	
	@Override
	public String toString() {
		return "VarDeclaration[" + getName() + " : " + type + " = " + getDefinition() + "]";
	}

	public VarDeclaration(String name, ValueType type, Expression value) {
		super(name, value);
		if (type == null) throw new RuntimeException();
		this.type = type;
	}
	
	@Override
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
	public DeclType getDeclType() {
		return new VarDeclType(getName(), type);
	}
	
	@Override
	public Declaration interpret(EvalContext ctx) {
		Expression newValue = (Expression) getDefinition().interpret(ctx);
		return new VarDeclaration(getName(), type, newValue);
	}

}
