package wyvern.tools.typedAST.core.binding.compiler;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;

public class MetadataInnerBinding implements Binding {

	private final Reference<Environment> tcEnv;
	private final Reference<EvaluationEnvironment> innerEnv;

	public static final MetadataInnerBinding EMPTY = new MetadataInnerBinding(EvaluationEnvironment.EMPTY, Environment.getEmptyEnvironment());

	public MetadataInnerBinding() {
		innerEnv = new Reference<>(EvaluationEnvironment.EMPTY);
		tcEnv = new Reference<>(Environment.getEmptyEnvironment());
	}

	public MetadataInnerBinding(EvaluationEnvironment evEnv, Environment tcEnv) {
		innerEnv = new Reference<>(evEnv);
		this.tcEnv = new Reference<>(tcEnv);
	}

	public MetadataInnerBinding(Reference<EvaluationEnvironment> eEnv, Reference<Environment> rEnv) {
		innerEnv = eEnv;
		tcEnv = rEnv;
	}

	public MetadataInnerBinding from(Environment env) {
		MetadataInnerBinding old = env.lookupBinding("metaVal", MetadataInnerBinding.class).orElseGet(() -> MetadataInnerBinding.EMPTY);
		return new MetadataInnerBinding(
				old.innerEnv.map(e -> innerEnv.get().extend(e)),
				old.tcEnv.map(e -> tcEnv.get().extend(e)));
	}

	@Override
	public String getName() {
		return "metaEnv";
	}

	@Override
	public Type getType() {
		return new Unit();
	}

	public Environment getInnerEnv() {
		return tcEnv.get();
	}

	public EvaluationEnvironment getInnerEvalEnv() {
		return innerEnv.get();
	}
}
