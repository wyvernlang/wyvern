package wyvern.tools.parsing;

public interface RecordTypeParser extends ContParser {
	void parseTypes(EnvironmentResolver r);
	void parseInner(EnvironmentResolver r);
}
