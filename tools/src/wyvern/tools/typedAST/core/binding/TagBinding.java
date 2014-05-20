package wyvern.tools.typedAST.core.binding;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class TagBinding implements Binding {

	private String tagName;
	private String caseOf;
	private List<String> comprises = new ArrayList<String>();
	
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
