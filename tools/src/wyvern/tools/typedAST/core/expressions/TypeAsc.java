package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.EvaluationEnvironment;

/**
 * Created by Ben Chung on 3/13/14.
 */
public class TypeAsc extends AbstractExpressionAST implements ExpressionAST {

	private final TypedAST exn;
	private Type should;

	public TypeAsc(TypedAST exn, Type should) {

		this.exn = exn;
		this.should = should;
	}

	@Override
	public Type getType() {
		return should;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> output = new HashMap<>();
		output.put("exn", exn);
		return output;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TypeAsc(newChildren.get("exn"), should);
	}

    @Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
