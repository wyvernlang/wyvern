package wyvern.tools.types.extensions;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Type;

public class TypeExtension extends AbstractTypeImpl implements Type {
	private Type base;
	private List<Type> parameters;
	
	public TypeExtension(Type base, List<Type> parameters) {
		this.base = base;
		this.parameters = parameters;
	}

	@Override
	public ValueType generateILType() {
		throw new RuntimeException("deprecated");
	}

	@Override
	public ValueType getILType(GenContext ctx) {
		final ValueType baseType = base.getILType(ctx);
		/*List<ConcreteTypeMember> decls = new LinkedList<ConcreteTypeMember>();
		StructuralType st = baseType.getStructuralType(ctx);
		int index = 0;
		int declCount = st.getDeclTypes().size();
		for (Type t : parameters) {
			ValueType vt = t.getILType(ctx);
			// advance the index to an AbstractTypeMember
			while (index < declCount && !(st.getDeclTypes().get(index) instanceof AbstractTypeMember)) {
				index++;
			}
			// add a corresponding ConcreteTypeMember
			AbstractTypeMember m = (AbstractTypeMember) st.getDeclTypes().get(index);
			decls.add(new ConcreteTypeMember(m.getName(), vt));
		}
		return new RefinementType(baseType, decls);*/
		List<ValueType> params = parameters.stream().map(p -> p.getILType(ctx)).collect(Collectors.toList());
		return new RefinementType(params,baseType);
	}

}
