package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.MetadataWrapper;
import wyvern.tools.types.extensions.TypeInv;

/**
 * Class encapsulates information about what tags a type is a case of and what comprises it.
 * 
 * @author Troy Shaw
 * @author Alex and Ben fixed it all up after merge in GIT failed :-)
 */
public class TaggedInfo {

	// private static Map<TypeBinding, TaggedInfo> globalTagStore = new HashMap<TypeBinding, TaggedInfo>();
	/*private*/ static List<TaggedInfo> globalTagStoreList = new ArrayList<TaggedInfo>();
	
	private String tagName;
	private Type tagType;
	
	private Type caseOf;
	private TaggedInfo caseOfTaggedInfo;
	
	private List<Type> comprises;
	private List<TaggedInfo> comprisesTaggedInfos;
	
	
	public static void resolveAll(Environment env, HasLocation hl) {
		for (TaggedInfo ti : globalTagStoreList) {
			ti.resolve(env, hl);
		}
	}
	
	
	public void resolve(Environment env, HasLocation hl) {
		if (this.tagType instanceof UnresolvedType) {
			this.tagType = ((UnresolvedType) this.tagType).resolve(env);
			if (this.tagType instanceof MetadataWrapper) {
				// System.out.println("Caught one (tagType)!");
				this.tagType = ((MetadataWrapper) this.tagType).getInner();
			}
		}
		
		if (this.tagType instanceof TypeInv) {
			// System.out.println("FOUND TypeInv tagType!" + tagType + " which could be " + ((TypeInv) this.tagType).resolve(env));
			// this.tagType = ((TypeInv) this.tagType).resolve(env);
		}
		
		
		if (this.caseOf != null && this.caseOf instanceof UnresolvedType) {
			String name = ((UnresolvedType) this.caseOf).getName();
			if (env.lookup(name) == null && env.lookupType(name) == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, hl, name);
			}
			
			this.caseOf = ((UnresolvedType) this.caseOf).resolve(env);
			if (this.caseOf instanceof MetadataWrapper) {
				// System.out.println("Caught one (caseOf)!");
				this.caseOf = ((MetadataWrapper) this.caseOf).getInner();
			}
		}

		if (this.caseOf != null && this.caseOf instanceof TypeInv) {
			// System.out.println("FOUND caseOf!" + caseOf);
			((TypeInv) this.caseOf).resolve(env);
			// System.out.println("MADE caseOf!" + caseOf);
		}

		if (caseOfTaggedInfo != null) caseOfTaggedInfo.resolve(env, hl);
		
		List<Type> resolvedComprises = new ArrayList<Type>(comprises.size());
		for (Type t : comprises) {
			if (t instanceof UnresolvedType) {
				Type tt = ((UnresolvedType) t).resolve(env);
				if (tt instanceof MetadataWrapper) {
					// System.out.println("Caught one (tt)!");
					tt = ((MetadataWrapper) tt).getInner();
				}
				resolvedComprises.add(tt);
			} else {
				// if (t instanceof ClassType)
				//	System.out.println(((ClassType) t).getName());
				resolvedComprises.add(t);
			}
			/*
			if (t instanceof TypeInv) {
				// System.out.println("FOUND comprises one!" + t);
				Type tt = ((TypeInv) t).resolve(env);
				resolvedComprises.add(tt);
			}
			*/
		}
		this.comprises = resolvedComprises;
		if (comprisesTaggedInfos != null) {
			for (TaggedInfo ti : comprisesTaggedInfos) {
				ti.resolve(env, hl);
			}
		}
	}
	
	
	/**
	 * Constructs an empty TaggedInfo. 
	 * Has no case of, and no comprises. 
	 */
	public TaggedInfo() {
		this(null, null);
	}
	
	/**
	 * Constructs a TaggedInfo with the given caseOf, and no comprises.
	 * @param caseOf
	 */
	public TaggedInfo(Type caseOf) {
		this(caseOf, null);
	}
	
	/**
	 * Constructs a TaggedInfo with the given comprises tags.
	 * @param comprises
	 */
	public TaggedInfo(List<Type> comprises) {
		this(null, comprises);
	}
	
	/**
	 * Constructs a TaggedInfo with the given caseOf and given comprises.
	 * 
	 * comprises cannot be null or a NullPointerException is thrown.
	 * 
	 * @param caseOf
	 * @param comprises
	 */
	public TaggedInfo(Type caseOf, List<Type> comprises) {		
		if (comprises == null) comprises = new ArrayList<Type>();
		
		this.caseOf = caseOf;
		this.comprises = comprises;
	}
	
	/**
	 * Associates this name with the tag. Needed because we don't know tags name when this object is
	 * instantiated.
	 * 
	 * @param tagName
	 */
	public void setTagName(String tagName, TypeDeclaration td, ClassDeclaration cd) {
		this.tagName = tagName;
		
		// One of these will be null:
		this.td = td;
		this.cd = cd;
	}
	
	TypeDeclaration td;
	ClassDeclaration cd;
	
	/**
	 * Gets the tag's name.
	 * @return
	 */
	public String getTagName() {
		return tagName;
	}
	
	public Type getTagType() {
		return tagType;
	}
	
	/**
	 * Returns true if this TaggedInfo has a case of.
	 * @return
	 */
	public boolean hasCaseOf() {
		return caseOf != null;
	}
	
	/**
	 * Returns true if this TaggedInfo has at least 1 comprises tag.
	 * 
	 * @return
	 */
	public boolean hasComprises() {
		return !comprises.isEmpty();
	}
	
	/**
	 * Returns the tag this TaggedInfo is a case of.
	 * Null indicates there is no case of tag.
	 * 
	 * @return
	 */
	public Type getCaseOfTag() {
		return caseOf;
	}
	
	/**
	 * Returns true of false if a circular hierarchical relation is detected.
	 * 
	 * @return
	 */
	public boolean isCircular() {
		// FIXME: Should be using type bindings, not strings.
		
		TaggedInfo info = lookupTagByType(caseOf);
		String myName = tagName;
		
		while (info != null) {	
			if (info.tagName.equals(myName)) return true;
			
			info = lookupTagByType(info.caseOf);
		}
		
		return false;
	}
	
	/**
	 * Returns a non-null list of what tags are comprised.
	 * A size of 0 indicates this doesn't have any comprise tags.
	 * 
	 * @return
	 */
	public List<Type> getComprisesTags() {
		return comprises;
	}
	
	public void associateWithClassOrType(TypeBinding t) {
		this.tagType = t.getType();
		
		//globalTagStore.put(t, this);
		globalTagStoreList.add(this);
	}
	
	public static void clearGlobalTaggedInfos() {
		//globalTagStore = new HashMap<TypeBinding, TaggedInfo>();
		globalTagStoreList = new ArrayList<TaggedInfo>();
	}
	
	// public static Map<TypeBinding, TaggedInfo> getGlobalTagStore() {
	//	return globalTagStore;
	// }
	
	/**
	 * Returns the global tag store, as a list.
	 * 
	 * This has the same contents as the Map<String, TaggedInfo> map, just
	 * without them being mapped by the tag name.
	 * 
	 * @return
	 */
	public static List<TaggedInfo> getGlobalTagStoreList() {
		return globalTagStoreList;
	}
	
	// public static TaggedInfo lookupTag(TypeBinding t) {
	//	TaggedInfo info = globalTagStore.get(t);
	//	
	//	return info;
	//}
	
	public static TaggedInfo lookupTagByType(Type t) {
		if (t == null) { return null; }
		
		// System.out.println("Looking for " + t + " inside:");
		// System.out.println(globalTagStoreList);
		
		for (TaggedInfo i : globalTagStoreList) {
			if (t instanceof ClassType && i.tagType instanceof ClassType) {
				ClassType ct = (ClassType) t;
				ClassType cti = (ClassType) i.tagType;
				
				// System.out.println(cti.getName());
				// System.out.println(ct.getName());
			
				if (ct.getName() != null && ct.getName().equals(i.getTagName())) return i; // FIXME:
			}
			
			if (i.tagType.equals(t)) return i;
		}
		return null;
	}

	public static void dumpall(Environment env) {
		System.out.println("DUMP OF TAGS:");
		for (TaggedInfo ti : TaggedInfo.globalTagStoreList) {
			System.out.println("Tag: " + ti);
			System.out.println("Tag.td = " + ti.td);
			if (ti.td!=null) {
				System.out.println(ti.td.getType());
			}
			System.out.println("Tag.cd = " + ti.cd);
			if (ti.cd!=null) {
				System.out.println(ti.cd.getType());
			}
		}
		System.out.println("END OF DUMP.");
	}

	@Override
	public String toString() {
		return "TaggedInfo [tagName=" + tagName + ", tagType=" + tagType + ", caseOf=" + caseOf
				+ ", caseOfTaggedInfo=" + caseOfTaggedInfo + ", comprises="
				+ comprises + ", comprisesTaggedInfos=" + comprisesTaggedInfos
				+ "]";
	}

}