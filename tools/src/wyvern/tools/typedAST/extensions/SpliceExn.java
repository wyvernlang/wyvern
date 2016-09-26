package wyvern.tools.typedAST.extensions;

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
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class SpliceExn extends AbstractExpressionAST {
	private final TypedAST exn;

	public SpliceExn(TypedAST exn) {
		this.exn = exn;
	}

	private Optional<Type> cached = Optional.empty();
	@Override
	public Type getType() {
		return cached.get();
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Environment outerEnv = env.lookupBinding("oev", TSLBlock.OuterTypecheckBinding.class)
			.map(oeb->oeb.getStore())
			.orElse(Environment.getEmptyEnvironment());
		Type exnType = exn.typecheck(outerEnv, expected);
		cached = Optional.of(exnType);
		return exnType;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		EvaluationEnvironment outerEnv = env.lookupBinding("oev", TSLBlock.OuterEnviromentBinding.class)
				.map(oeb->oeb.getStore())
				.orElse(EvaluationEnvironment.EMPTY);
		return exn.evaluate(outerEnv);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> result = new HashMap<>(1);
		result.put("exn", exn);
		return result;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new SpliceExn(newChildren.get("exn"));
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
