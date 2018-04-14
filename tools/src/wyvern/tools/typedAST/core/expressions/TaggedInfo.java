package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
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
        this.comprises = new ArrayList<Type>();
    }

    public TaggedInfo(String name, Type type) {
        this.comprises = new ArrayList<Type>();
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
     * comprises cannot be null or a NullPointerException is thrown.
     *
     * @param caseOf the datatype that this tagged type is a subtype of
     * @param comprises the list of subtypes for a tagged type
     */
    public TaggedInfo(Type caseOf, List<Type> comprises) {
        this.caseOf = caseOf;

        if (comprises != null) {
            this.comprises = comprises;
        } else {
            this.comprises = new ArrayList<Type>();
        }
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
    public Type getCaseOfTag() {
        return caseOf;
    }

    /**
     * Returns a non-null list of what tags are comprised.
     * A size of 0 indicates this doesn't have any comprise tags.
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