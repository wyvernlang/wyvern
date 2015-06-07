package wyvern.tools.typedAST.core.binding.evaluation;

import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class HackForArtifactTaggedInfoBinding implements EvaluationBinding {
    private final String name;
    private TaggedInfo associated;

    public HackForArtifactTaggedInfoBinding(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setTaggedInfo(TaggedInfo newInfo) {
        this.associated = newInfo;
    }

    public TaggedInfo getTaggedInfo() {
        return associated;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {

    }
}
