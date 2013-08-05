package wyvern.tools.parsing;

public interface RecordTypeParser extends ContParser {
	void parseTypes(EnvironmentResolver r);
	void parseInner(EnvironmentResolver r);

	public static abstract class RecordTypeParserBase implements RecordTypeParser {
		protected abstract void doParseTypes(EnvironmentResolver r);
		private boolean typesParsed = false;
		public void parseTypes(EnvironmentResolver r) {
			if (typesParsed)
				return;
			typesParsed = true;
			doParseTypes(r);
		}

		protected abstract void doParseInner(EnvironmentResolver r);
		private boolean innerParsed = false;
		public void parseInner(EnvironmentResolver r) {
			if (innerParsed)
				return;
			innerParsed = true;
			doParseInner(r);
		}
	}
}
