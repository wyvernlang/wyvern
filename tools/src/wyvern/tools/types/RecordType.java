package wyvern.tools.types;

import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.types.extensions.TypeType;

public interface RecordType extends Type {
	public TypeBinding getInnerType(String name);
	public TypeType getEquivType();
	public TaggedInfo getTaggedInfo();
}
