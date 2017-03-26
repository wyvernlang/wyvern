package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.types.Type;

public class RefinementType extends ValueType {
	public RefinementType(ValueType base, List<ConcreteTypeMember> declTypes) {
		this.base = base;
		this.declTypes = declTypes;
	}
	
	public RefinementType(List<ValueType> typeParams, ValueType base) {
		this.base = base;
		this.typeParams = typeParams;
	}
	
	private ValueType base;
	private List<ConcreteTypeMember> declTypes = null; // may be computed lazily from typeParams
	private List<ValueType> typeParams;
	
	private List<ConcreteTypeMember> getDeclTypes(TypeContext ctx) {
		if (declTypes == null) {
			declTypes = new LinkedList<ConcreteTypeMember>();
			StructuralType st = base.getStructuralType(ctx);
			int index = 0;
			int declCount = st.getDeclTypes().size();
			for (ValueType vt : typeParams) {
				// advance the index to an AbstractTypeMember
				while (index < declCount && !(st.getDeclTypes().get(index) instanceof AbstractTypeMember)) {
					index++;
				}
				// add a corresponding ConcreteTypeMember
				AbstractTypeMember m = (AbstractTypeMember) st.getDeclTypes().get(index);
				declTypes.add(new ConcreteTypeMember(m.getName(), vt));
			}
		}
		return declTypes;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
		throw new RuntimeException("need to add a visitor method call here");
	}

	@Override
	public ValueType adapt(View v) {
		ValueType newBase = base.adapt(v);
		if (declTypes == null)
			return new RefinementType(typeParams, newBase);
		List<ConcreteTypeMember> newDTs = new LinkedList<ConcreteTypeMember>();
		for (ConcreteTypeMember dt : declTypes) {
			newDTs.add(dt.adapt(v));
		}
		return new RefinementType(newBase, newDTs);
	}

	@Override
	public ValueType doAvoid(String varName, TypeContext ctx, int depth) {
		List<ConcreteTypeMember> newDeclTypes = new LinkedList<ConcreteTypeMember>();
		boolean changed = false;
		ValueType newBase = base.doAvoid(varName, ctx, depth);
		if (declTypes == null) {
			List<ValueType> newTPs = typeParams.stream().map(p -> p.doAvoid(varName, ctx, depth)).collect(Collectors.toList());
			return new RefinementType(newTPs, base);
		}
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
		for (DeclType dt : getDeclTypes(ctx)) {
			dt.checkWellFormed(selfCtx);
		}
	}

	@Override
	public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
		StructuralType baseST = base.getStructuralType(ctx, theDefault);
		List<DeclType> newDTs = new LinkedList<DeclType>();
		int current = 0;
		int max = getDeclTypes(ctx).size();
		for (DeclType t : baseST.getDeclTypes()) {
			if (current < max && t.getName().equals(getDeclTypes(ctx).get(current).getName())) {
				newDTs.add(getDeclTypes(ctx).get(current));
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
	public int hashCode() {
		return Arrays.hashCode(new Object[] { base, declTypes, });
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RefinementType))
			return false;
		RefinementType other = (RefinementType)obj;
		if (declTypes == null) {
			if (other.declTypes == null) {
				return base.equals(other.base) && typeParams.equals(other.typeParams);				
			} else {
				return false;
			}
		}
		return base.equals(other.base) && declTypes.equals(other.declTypes);
	}
	
	@Override
	public boolean isSubtypeOf(ValueType t, TypeContext ctx) {
		// if they are equivalent to a DynamicType or equal to us, then return true
		if (equals(t))
			return true;
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
		if (declTypes != null) {
			for (ConcreteTypeMember ctm: declTypes) {
				ctm.getRawResultType().doPrettyPrint(dest, indent);
				dest.append(", ");
			}
		} else {
			for (ValueType vt: typeParams) {
				vt.doPrettyPrint(dest, indent);
				dest.append(", ");
			}			
		}
		dest.append(']');
	}
}
