package wyvern.tools.typedAST.extensions;

import java.util.ArrayList;
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
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.EvaluationEnvironment;

public class SpliceBindExn extends AbstractExpressionAST implements BoundCode {
	private final TypedAST exn;
	private List<NameBinding> bindings;

	public SpliceBindExn(TypedAST exn, List<NameBinding> bindings) {
		this.exn = exn;
		this.bindings = bindings;
	}

	private Optional<Type> cached = Optional.empty();
	@Override
	public Type getType() {
		return DefDeclaration.getMethodType(bindings, cached.get());
	}

	@Override
	public List<NameBinding> getArgBindings() {
		return bindings;
	}

	@Override
	public TypedAST getBody() {
		return exn;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Environment outerEnv = env.lookupBinding("oev", TSLBlock.OuterTypecheckBinding.class)
			.map(oeb->oeb.getStore())
			.orElse(Environment.getEmptyEnvironment());

		List<NameBinding> newBindings = new ArrayList<>();
		for (NameBinding binding : bindings)
			newBindings.add(new NameBindingImpl(binding.getName(), TypeResolver.resolve(binding.getType(), env)));
		bindings = newBindings;


		Optional<Type> resType = expected.map(type -> ((Arrow) type).getResult());

		outerEnv = outerEnv.extend(bindings.stream().reduce(Environment.getEmptyEnvironment(), Environment::extend, (a,b)->b.extend(a)));
		Type exnType = exn.typecheck(outerEnv, resType);
		cached = Optional.of(exnType);
		return getType();
	}

	@Override
    @Deprecated
	public Value evaluate(EvaluationEnvironment env) {
		EvaluationEnvironment outerEnv = env.lookupBinding("oev", TSLBlock.OuterEnviromentBinding.class)
				.map(oeb->oeb.getStore())
				.orElse(EvaluationEnvironment.EMPTY);

		return new Closure(this, outerEnv);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> result = new HashMap<>(1);
		result.put("exn", exn);
		return result;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new SpliceBindExn(newChildren.get("exn"), bindings);
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
