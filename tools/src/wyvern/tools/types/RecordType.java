package wyvern.tools.types;

import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;

public interface RecordType extends Type {
	public TypeBinding getInnerType(String name);
}
