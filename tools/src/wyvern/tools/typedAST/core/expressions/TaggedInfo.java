package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class encapsulates information about what tags a type is a case of and what comprises it.
 * 
 * @author Troy Shaw
 */
public class TaggedInfo {

	private static Map<String, TaggedInfo> globalTagStore = new HashMap<String, TaggedInfo>();
	private static List<TaggedInfo> globalTagStoreList = new ArrayList<TaggedInfo>();
	
	private String tagName;
	
	private String caseOf;
	private TaggedInfo caseOfTaggedInfo;
	
	private List<String> comprises;
	private List<TaggedInfo> comprisesTaggedInfos;
	
	
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
	 * Returns true of false if a circular hierarchical relation is detected.
	 * 
	 * @return
	 */
	public boolean isCircular() {
		TaggedInfo info = lookupTag(caseOf);
		String myName = tagName;
		
		while (info != null) {	
			if (info.tagName.equals(myName)) return true;
			
			info = lookupTag(info.caseOf);
		}
		
		return false;
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
	
	public void associateWithClass(String className) {
		tagName = className;
		globalTagStore.put(tagName, this);
		globalTagStoreList.add(this);
	}
	
	public static void clearGlobalTaggedInfos() {
		globalTagStore = new HashMap<String, TaggedInfo>();
		globalTagStoreList = new ArrayList<TaggedInfo>();
	}
	
	public static Map<String, TaggedInfo> getGlobalTagStore() {
		return globalTagStore;
	}
	
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
	
	public static TaggedInfo lookupTag(String name) {
		TaggedInfo info = globalTagStore.get(name);
		
		return info;
	}

	@Override
	public String toString() {
		return "TaggedInfo [tagName=" + tagName + ", caseOf=" + caseOf
				+ ", caseOfTaggedInfo=" + caseOfTaggedInfo + ", comprises="
				+ comprises + ", comprisesTaggedInfos=" + comprisesTaggedInfos
				+ "]";
	}
	
	
}
