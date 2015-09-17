package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH;
import static wyvern.tools.errors.ToolError.reportError;

public class Intersection implements Type, OperatableType, ApplyableType {
	private List<Type> types;
	public Intersection(List<Type> types) {
		this.types = types;
	}

	public int getOptions() {
		return types.size();
	}
	public List<Type> getTypes() {
		return types;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (other instanceof Intersection) {
			if (((Intersection)other).types.size() > types.size())
				return false;
			for (int i = 0; i < types.size(); i++)
				if (!((Intersection)other).subtype(types.get(i), subtypes))
					return false;
			return true;
		}
		for (Type type : types) {

			SubtypeRelation sr = new SubtypeRelation(type, other);
			if (subtypes.contains(sr)) {
				continue;
			}
			subtypes.add(sr);
			if (type.subtype(other, subtypes))
				return true;
			subtypes.remove(sr);
		}
		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	/**One-way only, or
	 * T |- e : A_1  T |- e : A_2
	 * --------------------------
	 *     T |- e : A_1 ^ A_2
	 *
	 * To do this properly, we also need
	 *
	 *     T |- e : A_1 ^ A_2
	 * --------------------------
	 * T |- e : A_1  T |- e : A_2
	 *
	 * We don't have this yet, though.
	 *
	 */
	@Override
	public boolean subtype(Type other) {
		return subtype(other, new HashSet<SubtypeRelation>());
	}

	@Override
	public Type checkApplication(Application application, Environment env) {
		for (Type type : types)
			if (type instanceof ApplyableType)
				try {
					return ((ApplyableType) type).checkApplication(application, env);
				} catch (ToolError e) {
					continue;
				}
		reportError(ACTUAL_FORMAL_TYPE_MISMATCH, application, application.getArgument().typecheck(env, Optional.empty()).toString(),
                toString());
		return null; //Unreachable
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		for (Type type : types)
			if (type instanceof OperatableType)
				try {
					return ((OperatableType) type).checkOperator(opExp, env);
				} catch (ToolError e) {
					continue;
				}
		reportError(ACTUAL_FORMAL_TYPE_MISMATCH, opExp, opExp.getArgument().typecheck(env, Optional.empty()).toString(),
                toString());
		return null; //Unreachable
	}
	@Override
	public Map<String, Type> getChildren() {
		HashMap<String, Type> map = new HashMap<>();
		for (int i = 0; i < types.size(); i++) {
			map.put(i +"", types.get(i));
		}
		return map;
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		List<Type> result = new ArrayList<>(types.size());
		for (int i = 0; i < types.size(); i++) {
			result.add(newChildren.get(i + ""));
		}
		return new Intersection(result);
	}


	private Optional<TypeBinding> binding = Optional.empty();

	@Override
	public Optional<TypeBinding> getResolvedBinding() {
		return binding;
	}

	@Override
	public void setResolvedBinding(TypeBinding binding) {
		this.binding = Optional.of(binding);
	}

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		Type cloned = cloneWithChildren(getChildren());
		cloned.setResolvedBinding(binding);
		return cloned;
	}

    @Override
    public ValueType generateILType() {
        throw new WyvernException("Invalid interop type, cannot generate IL", FileLocation.UNKNOWN);
    }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
