package wyvern.tools.typedAST.core.binding.compiler;

import wyvern.tools.imports.ImportResolver;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ImportResolverBinding implements Binding {

	private final String name;
	private final ImportResolver bound;

	public ImportResolverBinding(String name, ImportResolver bound) {
		this.name = name;
		this.bound = bound;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return null;
	}

	public ImportResolver getBound() {
		return bound;
	}
}
