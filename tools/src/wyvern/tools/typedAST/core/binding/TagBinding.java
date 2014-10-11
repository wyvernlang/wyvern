package wyvern.tools.typedAST.core.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.TreeWriter;

public class TagBinding implements Binding {

	private String tagName;
	private String caseOf;
	private List<String> comprises = new ArrayList<String>();
	
	
	/** The parent TagBinding if this has a case-of. May be null.  */
	private TagBinding caseOfParent;
	
	/** The list of direct sub-tags of this tag.  */
	private List<TagBinding> directSubtags = new ArrayList<TagBinding>();
	
	/** The list of comprises TagBindings. */
	private List<TagBinding> comprisesTags = new ArrayList<TagBinding>();
	
	
	//A bit of a hack, because environment is not working
	private static Map<String, TagBinding> tagBindings = new HashMap<String, TagBinding>();
	private static Map<String, TaggedInfo> tagInfos = new HashMap<String, TaggedInfo>();
	
	public static void resetGlobalData() {
		tagBindings = new HashMap<String, TagBinding>();
		tagInfos = new HashMap<String, TaggedInfo>();
	}
	
	/**
	 * Internal constructor to instantiate a TagBinding.
	 * 
	 * @param tagName
	 * @param taggedInfo
	 */
	private TagBinding(String tagName, TaggedInfo taggedInfo) {
		this.tagName = tagName;
		this.caseOf = taggedInfo.getCaseOfTag();
		this.comprises.addAll(taggedInfo.getComprisesTags());
	}
	
	private TagBinding(String tagName, TaggedInfo taggedInfo, NameBinding nameBinding, TypeBinding typeBinding) {		
		this.tagName = tagName;
		this.caseOf = taggedInfo.getCaseOfTag();
		this.comprises.addAll(taggedInfo.getComprisesTags());
	}
	
	/**
	 * Gets the TagBinding, creating it if necessary.
	 * 
	 * @param tagName
	 * @return
	 */
	private static TagBinding getOrCreate(String tagName) {
		if (tagBindings.containsKey(tagName)) {
			return tagBindings.get(tagName);
		}
		
		//If not present, create it, associate it, then return it
		TagBinding binding = new TagBinding(tagName, tagInfos.get(tagName));
		tagBindings.put(tagName, binding);
		
		return binding;
	}
	
	private static TagBinding get(String tagName) {
		if (!tagBindings.containsKey(tagName)) {
			//throw new RuntimeException("Attempted to get non-existant tag: " + tagName);
			return null;
		}
		
		return tagBindings.get(tagName);
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// Empty, what goes here?
	}

	/**
	 * Sets the case-of parent for this TagBinding.
	 * @param caseOfParent
	 */
	public void setCaseOfParent(TagBinding caseOfParent) {
		this.caseOfParent = caseOfParent;
	}
	
	public TagBinding getCaseOfParent() {
		return caseOfParent;
	}
	
	public boolean hasCaseOfParent() {
		return caseOfParent != null;
	}
	
	public List<TagBinding> getComprisesOf() {
		return comprisesTags;
	}
	
	public boolean hasAnyComprises() {
		return comprisesTags.size() != 0;
	}
	
	public List<TagBinding> getDirectSubtags() {
		return directSubtags;
	}
	
	/**
	 * Adds a TagBinding as a direct child/ sub-tag of this TagBinding.
	 * @param tagBinding
	 */
	public void addCaseOfDirectChild(TagBinding tagBinding) {
		
	}
	
	@Override
	public String getName() {
		return tagName;
	}

	@Override
	public Type getType() {
		//TODO: what should this type be
		return null;
	}
	@Override
	public String toString() {
		return "{tag: " + tagName + ", case-of: " + caseOf + ", comprises: " + comprises + "}";
	}
	
	public static void associate(TaggedInfo taggedInfo) {
		tagInfos.put(taggedInfo.getTagName(), taggedInfo);
	}
}
