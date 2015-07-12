package wyvern.tools.typedAST.core.declarations;

import java.util.List;
import java.util.Map;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeInv;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public abstract class AbstractTypeDeclaration extends Declaration {
	protected void setupTags(String name, TypeBinding typeBinding, TaggedInfo taggedInfo) {
		if (taggedInfo != null) {
			this.taggedInfo = taggedInfo;
			this.taggedInfo.setTagName(name, this);
			this.taggedInfo.associateWithClassOrType(typeBinding);
		}
	}
	
	private TaggedInfo taggedInfo;
	
	protected void typecheckTags(Environment env) {
		taggedInfo.resolve(env, this);

		// System.out.println("CURRENT ti = " + taggedInfo);

		Type myTagType = taggedInfo.getTagType();

		if (taggedInfo.hasCaseOf()) {
			Type caseOfType = taggedInfo.getCaseOfTag();

			// System.out.println("caseOfType = " + caseOfType);
			// System.out.println("TaggedInfo Global Store Current State = " + TaggedInfo.getGlobalTagStore());

			//check the type is tagged
			if (!(caseOfType instanceof TypeInv)) { // If it is TypeInv - we won't know till runtime!
				TaggedInfo info = TaggedInfo.lookupTagByType(caseOfType);

				//System.out.println("Looked up: " + info);

				taggedInfo.setCaseOfTaggedInfo(info);

				if (info == null) {
					ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, this, caseOfType.toString());
				}

				//now check circular relationship has not been created
				if (taggedInfo.isCircular()) {
					ToolError.reportError(ErrorMessage.CIRCULAR_TAGGED_RELATION, this, taggedInfo.getTagName(), caseOfType.toString());
				}
			}
		}

		if (taggedInfo.hasComprises()) {
			List<Type> comprisesTags = taggedInfo.getComprisesTags();

			//first check that every comprises tag actually is a case-of of this
			for (Type s : comprisesTags) {
				TaggedInfo info = TaggedInfo.lookupTagByType(s); // FIXME:

				//check it exists
				if (info == null) {
					ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, this, s.toString());
				}

				//then check it is a case-of
				Type comprisesCaseOfName = info.getCaseOfTag();
				if (!myTagType.equals(comprisesCaseOfName)) {
					ToolError.reportError(ErrorMessage.COMPRISES_RELATION_NOT_RECIPROCATED, this);
				}
			}

			//now check every other case-of does not case-of this tag
			for (TaggedInfo info : TaggedInfo.getGlobalTagStoreList()) {
				Type othersType = info.getTagType();
				Type caseOf = info.getCaseOfTag();

				//if tag is ourselves, or one of our comprises, skip it
				if (othersType.equals(myTagType) || comprisesTags.contains(othersType)) {
					continue;
				}

				//now if the other tag 'case-of's is this, it is an error
				if (myTagType.equals(caseOf)) {
					ToolError.reportError(ErrorMessage.COMPRISES_EXCLUDES_TAG, this, myTagType.toString(), info.getTagType().toString());
				}
			}
		}
	}
	
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
