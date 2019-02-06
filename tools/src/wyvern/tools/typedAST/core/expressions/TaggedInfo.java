package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.tools.types.Type;

/**
 * Class encapsulates information about what tags a type is a case of and what comprises it.
 *
 * @author Troy Shaw
 * @author Alex and Ben fixed it all up after merge in GIT failed :-)
 */
public class TaggedInfo {

    private String tagName;
    private Type tagType;
    private Type caseOf;
    private List<Type> comprises;

    /**
     * Constructs an empty TaggedInfo.
     * Has no case of, and no comprises.
     */
    public TaggedInfo() {
    }

    public TaggedInfo(String name, Type type) {
        this.tagName = name;
        this.tagType = type;
    }

    /**
     * Constructs a TaggedInfo with the given caseOf, and no comprises.
     * @param caseOf the datatype that this tagged type is a subtype of
     */
    public TaggedInfo(Type caseOf) {
        this(caseOf, null);
    }

    /**
     * Constructs a TaggedInfo with the given comprises tags.
     * @param comprises the list of subtypes for a tagged type
     */
    public TaggedInfo(List<Type> comprises) {
        this(null, comprises);
    }

    /**
     * Constructs a TaggedInfo with the given caseOf and given comprises.
     *
     * comprises may be null if no list of comprises is specified.
     *
     * @param caseOf the datatype that this tagged type is a subtype of
     * @param comprises the list of subtypes for a tagged type
     */
    public TaggedInfo(Type caseOf, List<Type> comprises) {
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
     * Returns true if this TaggedInfo has a non-null comprises list.
     *
     * @return
     */
    public boolean hasComprises() {
        return comprises != null;
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
     * Returns a list of what tags are comprised.
     * null indicates this doesn't have any comprise tags.
     * an empty list indicates there are no cases
     *
     * @return
     */
    public List<Type> getComprisesTags() {
        return comprises;
    }

    public static void clearGlobalTaggedInfos() {
        // TODO Auto-generated method stub
    }
}