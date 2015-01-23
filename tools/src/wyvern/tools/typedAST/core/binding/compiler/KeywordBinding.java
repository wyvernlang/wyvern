package wyvern.tools.typedAST.core.binding.compiler;

import java.util.Optional;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class KeywordBinding implements Binding{

	// TODO: Host by a type or by a name?

	private String hostTypeName;
	private Reference<Type> hostType;
	private final String keyword;
	private final Reference<Value> metadata;
	private final Type keywordType;
	
	public KeywordBinding(String hostType, KeywordDeclaration kwDecl) {
		this.hostTypeName = hostType;
		this.keyword = kwDecl.getName();
		this.metadata = kwDecl.getMetaObj();
		this.keywordType = kwDecl.getType();
		this.hostType = kwDecl.getHostType();
	}
	
	public KeywordBinding(String hostType, String keyword, Type keywordType, Reference<Value> metadata) {
		this.keyword = keyword;
		this.hostTypeName = hostType;
		this.metadata = metadata;
		this.keywordType = keywordType;
	}
	
	public Type setHostType(Environment env) {
		return env.lookupType(this.hostTypeName).getType();
	}
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public Type getHostType() {
		return this.hostType.get();
	}
	
	public String getHostTypeName() {
		return this.hostTypeName;
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
