package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.AbstractBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

import java.util.Optional;

public class TypeBinding extends AbstractBinding {
	private final Reference<Value> metadata;
	private final Reference<DeclSequence> keywordDecls;

	public TypeBinding(String name, Type type) {
		super(name, type);
		this.metadata = null;
		this.keywordDecls = null;
	}
	
	public TypeBinding(String name, Type type, DeclSequence kwdecls) {
		super(name, type);
		this.metadata = null;
		this.keywordDecls = new Reference<DeclSequence>(kwdecls);
	}
	
	public TypeBinding(String name, Type type, Reference<Value> metadata) {
		super(name, type);
		this.metadata = metadata;
		this.keywordDecls = null;
	}
	
	
	public TypeBinding(String name, Type type, Reference<Value> metadata, DeclSequence kwdecls) {
		super(name, type);
		this.metadata = metadata;
		this.keywordDecls = new Reference<DeclSequence>(kwdecls);
	}
	
	public Type getUse() {
		return getType(); 
	}
	
	public Optional<Reference<Value>> getMetadata() { return Optional.ofNullable(metadata); }
	
	public Optional<Reference<DeclSequence>> getKeywordDecsl() { 
		return Optional.ofNullable(keywordDecls); 
	}
}
