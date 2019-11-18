package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.generics.GenericArgument;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class TypeExtension extends AbstractTypeImpl implements Type {
    private Type base;
    private List<GenericArgument> genericArguments;

    public TypeExtension(Type base, List<GenericArgument> genericArguments, FileLocation loc) {
        super(loc);
        this.base = base;
        this.genericArguments = genericArguments;
    }

    public Type getBase() {
        return base;
    }
    
    public List<GenericArgument> getGenericArguments() {
        return genericArguments;
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

        return new RefinementType(
                this.genericArguments.stream()
                        .map(arg -> wyvern.target.corewyvernIL.generics.GenericArgument.fromHighLevel(ctx, this.getLocation(), arg))
                        .collect(Collectors.toList()),
                baseType,
                this
        );
    }
}