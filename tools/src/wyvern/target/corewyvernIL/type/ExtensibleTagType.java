package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public class ExtensibleTagType extends TagType {

    public ExtensibleTagType(NominalType parentType, ValueType valueType) {
        super(parentType, valueType);
    }

    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor,
                                  S state) {
        return emitILVisitor.visit(state, this);
    }
    
	@Override
	public Type adapt(View v) {
		return new ExtensibleTagType((NominalType)getParentType(v), getValueType().adapt(v));
	}
	
	@Override
	public Type doAvoid(String varName, TypeContext ctx, int depth) {
		final NominalType newPT = parentType!=null?(NominalType)parentType.doAvoid(varName, ctx, depth):null;
		return new ExtensibleTagType(newPT, getValueType().doAvoid(varName, ctx, depth));
	}
}
