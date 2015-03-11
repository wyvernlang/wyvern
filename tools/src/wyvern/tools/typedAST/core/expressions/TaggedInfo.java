package wyvern.tools.typedAST.core.expressions;

import java.util.List;
import java.util.ArrayList;

import wyvern.tools.typedAST.core.binding.TagBinding;

/**
 * Class encapsulates information about what tags a type is a case of and what comprises it.
 * 
 * @author Troy Shaw
 */
public class TaggedInfo {

	private String tagName;
	private String caseOf;
	private List<String> comprises;
	
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
	public TaggedInfo(String caseOf) {
		this(caseOf, null);
	}
	
	/**
	 * Constructs a TaggedInfo with the given comprises tags.
	 * @param comprises
	 */
	public TaggedInfo(List<String> comprises) {
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
	public TaggedInfo(String caseOf, List<String> comprises) {		
		if (comprises == null) comprises = new ArrayList<String>();
		
		this.caseOf = caseOf;
		this.comprises = comprises;
	}
	
	/**
	 * Associate this TaggedInfo with the TagBinding tagset.
	 */
	public void associateTag() {
		TagBinding.associate(this);
	}
	
	/**
	 * Associates this name with the tag. Needed because we don't know tags name when this object is
	 * instantiated.
	 * 
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	/**
	 * Gets the tag's name.
	 * @return
	 */
	public String getTagName() {
		return tagName;
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
	public String getCaseOfTag() {
		return caseOf;
	}
	
	/**
	 * Returns a non-null list of what tags are comprised.
	 * A size of 0 indicates this doesn't have any comprise tags.
	 * 
	 * @return
	 */
	public List<String> getComprisesTags() {
		return comprises;
	}
}
