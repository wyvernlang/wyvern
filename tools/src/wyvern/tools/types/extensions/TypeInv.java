package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;
import java.util.Map;

public class TypeInv implements Type {
	Type innerType;
	String invName;

	public String toString() {
		return "TypeInv with innerType = " + innerType + " invName = " + invName;
	}
	
	public TypeInv(Type innerType, String invName) {
		this.innerType = innerType;
		this.invName = invName;
	}

	public Type resolve(Environment env) {
		// System.out.println("Inside TypeInv innerType = " + innerType + " and its class is " + innerType.getClass());
		
		if (this.innerType instanceof UnresolvedType) {
			UnresolvedType ut = (UnresolvedType) this.innerType;
			Type t = ut.resolve(env);
			
			// System.out.println("GOT: " + t);
		}
		TypeBinding fetched = ((RecordType) innerType).getInnerType(invName);

		// There can be some problems if there exists keyword but not metadata
		if (fetched.getMetadata().isPresent() && fetched.getMetadata().get().get() != null)
			return new MetadataWrapper(fetched.getUse(), fetched.getMetadata().get(), fetched.getKeywordDecls().get());
		else if (fetched.getKeywordDecls().isPresent() && fetched.getKeywordDecls().get() != null)
			return new MetadataWrapper(fetched.getUse(), fetched.getMetadata().get(), fetched.getKeywordDecls().get());
		else return fetched.getUse();
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean subtype(Type other) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean isSimple() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Map<String, Type> getChildren() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
