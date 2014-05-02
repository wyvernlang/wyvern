package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class MetadataInnerBinding implements Binding {

	private final Reference<Environment> innerEnv;

	public MetadataInnerBinding() {
		innerEnv = new Reference<>(Environment.getEmptyEnvironment());
	}

	public MetadataInnerBinding(Environment metaEnv) {
		innerEnv = new Reference<>(metaEnv);
	}

	public MetadataInnerBinding(Reference<Environment> rEnv) {
		innerEnv = rEnv;
	}

	public MetadataInnerBinding from(Environment env) {
		Environment oldEnv = env.lookupBinding("metaVal", MetadataInnerBinding.class)
				.map(MetadataInnerBinding::getInnerEnv).orElse(Environment.getEmptyEnvironment());
		return new MetadataInnerBinding(new Reference<>(() -> oldEnv.extend(innerEnv.get())));
	}

	@Override
	public String getName() {
		return "metaEnv";
	}

	@Override
	public Type getType() {
		return Unit.getInstance();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
	public Environment getInnerEnv() {
		return innerEnv.get();
	}
}
