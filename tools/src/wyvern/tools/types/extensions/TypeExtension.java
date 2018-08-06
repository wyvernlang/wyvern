package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.generics.GenericArgument;
import wyvern.tools.generics.GenericKind;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class TypeExtension extends AbstractTypeImpl implements Type {
    private Type base;
    private List<Type> typeArguments;
    private List<EffectSet> effectArguments;

    public TypeExtension(Type base, List<GenericArgument> genericArguments, FileLocation loc) {
        super(loc);
        this.base = base;
        this.typeArguments =
                genericArguments.stream()
                        .filter(a -> a.getKind() == GenericKind.TYPE)
                        .map(GenericArgument::getType)
                        .collect(Collectors.toList());
        this.effectArguments =
                genericArguments.stream()
                        .filter(a -> a.getKind() == GenericKind.EFFECT)
                        .map(a -> EffectSet.parseEffects("", a.getEffect(), false, loc))
                        .collect(Collectors.toList());
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
                this.typeArguments.stream().map(ta -> ta.getILType(ctx)).collect(Collectors.toList()),
                this.effectArguments,
                baseType,
                this
        );
    }

}
