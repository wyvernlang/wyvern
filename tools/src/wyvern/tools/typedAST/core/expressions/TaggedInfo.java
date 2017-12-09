package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.AbstractTypeDeclaration;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
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


	// Note that caseOf and comprises only use Type when parsing, they should all be replaced with appropriate
	// TaggedInfo during runtime or for type checking of tags to work.
	private Type caseOf;

	private List<Type> comprises;

	public TaggedInfo getCaseOfTaggedInfo() {
		return caseOfTaggedInfo;
	}

	public void setCaseOfTaggedInfo(TaggedInfo caseOfTaggedInfo) {
		this.caseOfTaggedInfo = caseOfTaggedInfo;
	}

	// The only thing that matters is TaggedInfo address and caseOf/comprises relation below.
	private TaggedInfo caseOfTaggedInfo;
	private List<TaggedInfo> comprisesTaggedInfos;



	/**
	 * Constructs an empty TaggedInfo.
	 * Has no case of, and no comprises.
	 */
	public TaggedInfo() {
		this.comprises = new ArrayList<Type>();
	}

	public TaggedInfo(String name, Type type) {
		this.comprises = new ArrayList<Type>();

		this.tagName = name;
		this.tagType = type;
	}

	/**
	 * Constructs a TaggedInfo with the given caseOf, and no comprises.
	 * @param caseOf
	 */
	public TaggedInfo(Type caseOf) {
		this(caseOf, null);
	}

	public TaggedInfo(TaggedInfo caseOfTaggedInfo, List<TaggedInfo> comprisesTaggedInfos) {
		this.caseOfTaggedInfo = caseOfTaggedInfo;
		this.comprisesTaggedInfos = comprisesTaggedInfos;
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
		this.caseOf = caseOf;

		if (comprises != null) {
			this.comprises = comprises;
		} else {
			this.comprises = new ArrayList<Type>();
		}
	}

	/**
	 * Associates this name with the tag. Needed because we don't know tags name when this object is
	 * instantiated.
	 *
	 * @param tagName
	 */
	public void setTagName(String tagName, AbstractTypeDeclaration td) {
		this.tagName = tagName;

		// One of these will be null:
		this.td = td;
	}

	AbstractTypeDeclaration td;

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

			if (i.tagType.equals(t)) return i;
		}
		return null;
	}

}