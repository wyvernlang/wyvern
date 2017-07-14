/* Copied from wyvern.target.corewyvernIL.decltype.AbstractTypeMember */
// Type member declarations, more like types than values
// DefDeclType in structural type (ex. int -> {head} (int ))
// representations?: path + effect name (might be a set of these pairs)

package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.IASTNode;

import java.io.IOException;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;


public class EffectDeclType extends DeclType implements IASTNode {
	boolean isResource;
	// TODO: add metadata
	
	public EffectDeclType(String name) {
		this(name, false);
	}

	public EffectDeclType(String name, boolean isResource) {
		super(name);
		this.isResource = isResource;
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) { // may need to be changed
        return this.getName().equals(dt.getName());
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("effect ").append(getName()).append('\n');
	}

	@Override
	public DeclType adapt(View v) {
        return this;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) { 
		// always well-formed! // may need further work
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