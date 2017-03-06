package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public class RefinementType extends ValueType {
	public RefinementType(ValueType base, List<ConcreteTypeMember> declTypes) {
		this.base = base;
		this.declTypes = declTypes;
	}
	
	private ValueType base;
	private List<ConcreteTypeMember> declTypes;
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
		throw new RuntimeException("need to add a visitor method call here");
	}

	@Override
	public ValueType adapt(View v) {
		List<ConcreteTypeMember> newDTs = new LinkedList<ConcreteTypeMember>();
		for (ConcreteTypeMember dt : declTypes) {
			newDTs.add(dt.adapt(v));
		}
		ValueType newBase = base.adapt(v);
		return new RefinementType(newBase, newDTs);
	}

	@Override
	public ValueType doAvoid(String varName, TypeContext ctx, int depth) {
		List<ConcreteTypeMember> newDeclTypes = new LinkedList<ConcreteTypeMember>();
		boolean changed = false;
		ValueType newBase = base.doAvoid(varName, ctx, depth);
		for (ConcreteTypeMember dt : declTypes) {
			ConcreteTypeMember newDT = dt.doAvoid(varName, ctx, depth+1);
			newDeclTypes.add(newDT);
			if (newDT != dt) {
				changed = true;
			}
		}
		if (!changed && base == newBase) {
			return this;
		} else {
			return new RefinementType(newBase, newDeclTypes);
		}
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		base.checkWellFormed(ctx);
		StructuralType t = base.getStructuralType(ctx);
		final TypeContext selfCtx = ctx.extend(t.getSelfName(), this);
		for (DeclType dt : declTypes) {
			dt.checkWellFormed(selfCtx);
		}
	}

	@Override
	public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
		StructuralType baseST = base.getStructuralType(ctx, theDefault);
		List<DeclType> newDTs = new LinkedList<DeclType>();
		int current = 0;
		int max = declTypes.size();
		for (DeclType t : baseST.getDeclTypes()) {
			if (current < max && t.getName().equals(declTypes.get(current).getName())) {
				newDTs.add(declTypes.get(current));
				current++;
			} else {
				newDTs.add(t);
			}
		}
		if (current != max)
			// TODO: replace with a nice warning
			throw new RuntimeException("invalid refinement type " + this);
		
		return new StructuralType(baseST.getSelfName(), newDTs, isResource(ctx));
	}
	@Override
	public boolean isResource(TypeContext ctx) {
		return base.isResource(ctx);
	}
	@Override
	public boolean isSubtypeOf(ValueType t, TypeContext ctx) {
		// if they are equivalent to a DynamicType or equal to us, then return true
		final ValueType ct = t.getCanonicalType(ctx);
		if (super.isSubtypeOf(ct, ctx))
			return true;
		
		// if their canonical type is a NominalType, check if our base is a subtype of it
		if (ct instanceof NominalType) {
			return base.isSubtypeOf(ct, ctx);
		}
		
		// if their canonical type is a RefinementType, compare the bases (for any tags) and separately check the structural types
		if (ct instanceof RefinementType) {
			if (!base.isSubtypeOf(((RefinementType)ct).base, ctx)) {
				return false;
			}
		}
		// compare structural types
		return this.getStructuralType(ctx).isSubtypeOf(ct.getStructuralType(ctx), ctx);		
	}
	
	@Override
	public ValueType getCanonicalType(TypeContext ctx) {
		ValueType baseCT = base.getCanonicalType(ctx);
		if (baseCT instanceof StructuralType) {
			return this.getStructuralType(ctx);
		} else {
			return this;
		}
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		base.doPrettyPrint(dest, indent);
		dest.append("[");
		for (ConcreteTypeMember ctm: declTypes) {
			ctm.getRawResultType().doPrettyPrint(dest, indent);
			dest.append(", ");
		}
		dest.append(']');
	}
}
