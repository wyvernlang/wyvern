package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.AbstractBinding;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

import java.util.Optional;

public class TypeBinding extends AbstractBinding {
	private final Reference<Value> metadata;

	public TypeBinding(String name, Type type) {
		super(name, type);
		this.metadata = null;
	}
	
	public TypeBinding(String name, Type type, Reference<Value> metadata) {
		super(name, type);
		this.metadata = metadata;
	}
	
	public Type getUse() {
		return getType(); 
	}
	
	public Optional<Reference<Value>> getMetadata() { return Optional.ofNullable(metadata); }
}
