package wyvern.tools.typedAST.core.expressions;

import java.util.List;
import java.util.ArrayList;

/**
 * Class encapsulates information about what tags a type is a case of and what comprises it.
 * 
 * @author Troy Shaw
 */
public class TaggedInfo {

	private String caseOf;
	private List<String> comprises;
	
	/**
	 * Constructs an empty TaggedInfo. 
	 * Has no case of, and no comprises. 
	 */
	public TaggedInfo() {
		comprises = new ArrayList<String>();
	}
	
	/**
	 * Constructs a TaggedInfo with the given caseOf, and no comprises.
	 * @param caseOf
	 */
	public TaggedInfo(String caseOf) {
		this(caseOf, new ArrayList<String>());
	}
	
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
		if (comprises == null) throw new NullPointerException("comprises cannot be null");
		
		this.caseOf = caseOf;
		this.comprises = comprises;
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
