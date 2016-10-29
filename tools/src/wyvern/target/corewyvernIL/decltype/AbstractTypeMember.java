package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.IASTNode;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIREnvironment;


public class AbstractTypeMember extends DeclType implements IASTNode {
	boolean isResource;

	public AbstractTypeMember(String name) {
		this(name, false);
	}

	public AbstractTypeMember(String name, boolean isResource) {
		super(name);
		this.isResource = isResource;
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
        return this.getName().equals(dt.getName());
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("type ").append(getName()).append('\n');
	}

	@Override
	public DeclType adapt(View v) {
        return this;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		// always well-formed!
	}

	@Override
	public DeclType doAvoid(String varName, TypeContext ctx, int count) {
		return this;
	}
	
	public boolean isResource() {
		return isResource;
	}

	@Override
	public boolean isTypeDecl() {
		return true;
	}
}
