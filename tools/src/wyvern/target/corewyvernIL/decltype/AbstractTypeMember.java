package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


public abstract class AbstractTypeMember extends DeclType implements EmitOIR {

	public AbstractTypeMember(String name) {
		super(name);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
