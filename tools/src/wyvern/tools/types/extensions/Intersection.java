package wyvern.tools.types.extensions;

import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;
import java.util.List;

import static wyvern.tools.errors.ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH;
import static wyvern.tools.errors.ToolError.reportError;

public class Intersection implements Type, OperatableType, ApplyableType {
	private List<Type> types;
	public Intersection(List<Type> types) {
		this.types = types;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return subtype(other);
	}

	@Override
	public LineParser getParser() {
		return null;
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
		if (other instanceof Intersection) {
			if (((Intersection)other).types.size() != types.size())
				return false;
			for (int i = 0; i < types.size(); i++)
				if (!((Intersection)other).subtype(types.get(i)))
					return false;
			return true;
		}
		for (Type type : types)
			if (type.subtype(other))
				return true;
		return false;
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
		reportError(ACTUAL_FORMAL_TYPE_MISMATCH, application, application.getArgument().typecheck(env).toString(),
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
		reportError(ACTUAL_FORMAL_TYPE_MISMATCH, opExp, opExp.getArgument().typecheck(env).toString(),
                toString());
		return null; //Unreachable
	}
}
