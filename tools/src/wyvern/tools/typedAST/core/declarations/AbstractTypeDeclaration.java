package wyvern.tools.typedAST.core.declarations;

import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeInv;

public abstract class AbstractTypeDeclaration extends Declaration {
	protected void setupTags(String name, TypeBinding typeBinding, TaggedInfo taggedInfo) {
		if (taggedInfo != null) {
			this.taggedInfo = taggedInfo;
			this.taggedInfo.setTagName(name, this);
			this.taggedInfo.associateWithClassOrType(typeBinding);
		}
	}
	
	private TaggedInfo taggedInfo;
	
	/**
	 * Returns the tag information associated with this type declaration.
	 * If this class isn't tagged this information will be null.
	 *
	 * @return the tag info
	 */
	public TaggedInfo getTaggedInfo() {
		return taggedInfo;
	}
	
	/**
	 * Returns if this class is tagged or not.
	 *
	 * @return true if tagged, false otherwise
	 */
	public boolean isTagged() {
		return taggedInfo != null;
	}
}
