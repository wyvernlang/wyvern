package wyvern.tools.typedAST.core.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class TagBinding implements Binding {

	private String tagName;
	private String caseOf;
	private List<String> comprises = new ArrayList<String>();
	
	/** The parent TagBinding if this has a case-of. May be null.  */
	private TagBinding caseOfParent;
	/** The list of direct sub-tags of this tag.  */
	public List<TagBinding> directSubtags = new ArrayList<TagBinding>();
	/** The list of comprises TagBindings. */
	public List<TagBinding> comprisesTags = new ArrayList<TagBinding>();
	
	//TODO: Remove this giant hack. Currently having a universal tag map because
	//type checking/ eval is not working properly
	public static Map<String, TagBinding> tagBindings = new HashMap<String, TagBinding>();
	
	
	public TagBinding(String tagName) {
		this.tagName = tagName;
	}
	
	public TagBinding(String tagName, String caseOf, List<String> comprises) {
		this.tagName = tagName;
		this.caseOf = caseOf;
		this.comprises.addAll(comprises);
	}
	
	public TagBinding(String tagName, TaggedInfo taggedInfo) {
		this.tagName = tagName;
		this.caseOf = taggedInfo.getCaseOfTag();
		this.comprises.addAll(taggedInfo.getComprisesTags());
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
}
