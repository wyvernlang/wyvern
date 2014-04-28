package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class MetadataInnerBinding implements Binding {

	private final Environment innerEnv;

	public MetadataInnerBinding() {
		innerEnv = Environment.getEmptyEnvironment();
	}

	public MetadataInnerBinding(Environment metaEnv) {
		innerEnv = metaEnv;
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
		return innerEnv;
	}
}
