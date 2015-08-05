package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;

public class VarDeclaration extends NamedDeclaration {

	private ValueType type;
	private Expression value;
	
	public VarDeclaration(String name, ValueType type, Expression value) {
		super(name);
		this.type = type;
		this.value = value;
	}
	
	public ValueType getType() {
		return type;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	public Expression getValue() {
		return value;
	}
	
	public void setValue(Expression value) {
		this.value = value;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public DeclType typeCheck(TypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Declaration interpret(EvalContext ctx) {
		Expression newValue = (Expression) value.interpret(ctx);
		return new VarDeclaration(getName(), type, newValue);
	}

}
