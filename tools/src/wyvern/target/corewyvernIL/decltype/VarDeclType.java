package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class VarDeclType extends DeclTypeWithResult implements EmitOIR{
	
	public VarDeclType(String name, ValueType type) {
		super(name, type);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DeclType adapt(View v) {
		return new ValDeclType(getName(), this.getRawResultType().adapt(v));
	}
}
