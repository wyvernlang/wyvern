package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.AbstractBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

import java.util.Optional;

public class TypeBinding extends AbstractBinding {
	private final TypedAST metadata;

	public TypeBinding(String name, Type type) {
		super(name, type);
		this.metadata = null;
	}
	public TypeBinding(String name, Type type, TypedAST metadata) {
		super(name, type);
		this.metadata = metadata;
	}

	public Type getUse() {
		return getType(); 
	}
	public Optional<TypedAST> getMetadata() { return Optional.ofNullable(metadata); }
}
