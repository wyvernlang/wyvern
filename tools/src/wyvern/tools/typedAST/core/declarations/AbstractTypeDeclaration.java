package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;

public abstract class AbstractTypeDeclaration extends Declaration {
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
