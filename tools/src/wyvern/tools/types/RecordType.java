package wyvern.tools.types;

public interface RecordType extends Type {
	public Type getInnerType(String name);
}
