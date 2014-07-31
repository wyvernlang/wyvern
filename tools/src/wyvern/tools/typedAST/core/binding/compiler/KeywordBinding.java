package wyvern.tools.typedAST.core.binding.compiler;

import java.util.Optional;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class KeywordBinding implements Binding{

	// TODO: Host by a type or by a name?
	private final Type hostType;
	private final String keyword;
	private final Reference<Value> metadata;
	private final Type keywordType;
	
	public KeywordBinding(Type hostType, KeywordDeclaration kwDecl) {
		this.hostType = hostType;
		this.keyword = kwDecl.getName();
		this.metadata = kwDecl.getMetaObj();
		this.keywordType = kwDecl.getType();
	}
	
	public KeywordBinding(Type hostType, String keyword, Type keywordType, Reference<Value> metadata) {
		this.keyword = keyword;
		this.hostType = hostType;
		this.metadata = metadata;
		this.keywordType = keywordType;
	}
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public Type getHostType() {
		return this.hostType;
	}
	
	public Optional<Reference<Value>> getMetadata() { 
		return Optional.ofNullable(metadata); 
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public String getName() {
		return this.getKeyword();
	}

	@Override
	public Type getType() {
		return this.keywordType;
	}

	@Override
	public String toString() {
		return "[WAAAAAA]" + "{" + this.keyword + "["  + this.getHostType() + "]}";
	}
	
}
