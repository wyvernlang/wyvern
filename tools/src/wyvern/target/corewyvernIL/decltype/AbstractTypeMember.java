package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIREnvironment;


public class AbstractTypeMember extends DeclType implements EmitOIR {

	public AbstractTypeMember(String name) {
		super(name);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public DeclType adapt(View v) {
		throw new RuntimeException("not implemented");
	}
}
