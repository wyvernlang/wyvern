package wyvern.tools.types.extensions;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class TypeInv implements Type {
	Type innerType;
	String invName;

	public Type getInnerType() { return innerType; }
	public String getInvName() { return invName; }

	public String toString() {
		return "TypeInv with innerType = " + innerType + " invName = " + invName;
	}

	public TypeInv(Type innerType, String invName) {
		this.innerType = innerType;
		this.invName = invName;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean subtype(Type other) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean isSimple() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Map<String, Type> getChildren() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Optional<TypeBinding> getResolvedBinding() {
		return Optional.empty();
	}

	@Override
	public void setResolvedBinding(TypeBinding binding) {
		throw new RuntimeException("Cannot resolve to a type invocation");
	}

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		throw new RuntimeException("Cannot resolve to a type invocation");
	}

    private Expression resolvePath() {
        if (innerType instanceof TypeInv) {
            return new FieldGet(((TypeInv) innerType).resolvePath(), invName, getLocation());
        } else if (innerType instanceof Variable) {
            return new wyvern.target.corewyvernIL.expression.Variable(((Variable) innerType).getName());
        }
        throw new RuntimeException("Unreachable");
    }

    @Override
    @Deprecated
    public ValueType generateILType() {
        return new NominalType((Path)resolvePath(), invName);
    }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FileLocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
}
