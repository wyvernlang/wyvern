package wyvern.tools.types;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.types.extensions.MetadataWrapper;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UnresolvedType implements Type {
	private String typeName;

	public UnresolvedType(String typeName) {
		this.typeName = typeName;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(typeName);
	}
	
	public Type resolve(Environment env) {
		
		if (env.lookup(this.typeName) != null) {
			NameBinding n = env.lookup(this.typeName);
		}
		
		if (env.lookupType(typeName) == null) {
			if (env.lookup(this.typeName) != null) {
				// Perhaps its first class?
				NameBinding n = env.lookup(this.typeName);
				Type t = n.getType();
				return t;
			}
			throw new RuntimeException("Cannot find "+typeName +" in environment "+env);
		}
		
		// There can be some problems if there exists keyword but not metadata
		TypeBinding typeBinding = env.lookupType(typeName);
		
		boolean metadataCond = typeBinding.getMetadata().isPresent() && typeBinding.getMetadata().get().get() != null;
		boolean keywordsCond = typeBinding.getKeywordDecls().isPresent() && typeBinding.getKeywordDecls().get().get() != null;
		
		if (metadataCond && !keywordsCond)
			return new MetadataWrapper(typeBinding.getUse(), typeBinding.getMetadata().get(), null);
		else if (keywordsCond && !metadataCond) {
			return new MetadataWrapper(typeBinding.getUse(), null, typeBinding.getKeywordDecls().get());
		} else if (keywordsCond && metadataCond) {
			return new MetadataWrapper(typeBinding.getUse(), typeBinding.getMetadata().get(), typeBinding.getKeywordDecls().get());
		} else
			return typeBinding.getUse();
	}
	
	@Override
	public String toString() {
		return "UNRESOLVED: " + typeName;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
				HasLocation.UNKNOWN, this.toString(), other.toString());
		return false; // Unreachable.
	}
	
	@Override
	public boolean subtype(Type other) {
		return this.subtype(other, new HashSet<SubtypeRelation>());
	}

    @Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		throw new RuntimeException("Cannot specify a ref type");
	}

	public String getName() {
		return typeName;
	}
}