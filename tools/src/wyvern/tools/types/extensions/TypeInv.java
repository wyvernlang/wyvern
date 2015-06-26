package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.types.Environment;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

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

	public Type resolve(Environment env) {
		// System.out.println("Inside TypeInv innerType = " + innerType + " and its class is " + innerType.getClass());

		if (this.innerType instanceof UnresolvedType) {
			UnresolvedType ut = (UnresolvedType) this.innerType;
			Type t = ut.resolve(env);

			// System.out.println("GOT: " + t);

			// System.out.println("OUT: " + t);

			innerType = t; // FIXME: FIXME: FIXME:

			// return t; // FIXME:
		}

		if (innerType instanceof RecordType) {
			// System.out.println("innerTYpe = " + innerType);

			TypeBinding fetched = ((RecordType) innerType).getInnerType(invName);

			// System.out.println("fetched = " + fetched);

			if (fetched.getMetadata().isPresent() && fetched.getMetadata().get().get() != null){
				return fetched.getUse().cloneWithBinding(fetched);
			}
			else return fetched.getUse();
		}

		// System.out.println("Note: returning plain innertype in TypeInv!");
		return this.innerType; // FIXME:
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
            return new FieldGet(((TypeInv) innerType).resolvePath(), invName);
        } else if (innerType instanceof Variable) {
            return new wyvern.target.corewyvernIL.expression.Variable(((Variable) innerType).getName());
        }
        throw new RuntimeException("Unreachable");
    }

    @Override
    public ValueType generateILType() {
        return new NominalType((Path)resolvePath(), invName);
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
