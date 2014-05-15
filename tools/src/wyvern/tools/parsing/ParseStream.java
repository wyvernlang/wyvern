package wyvern.tools.parsing;

public class ParseStream {
	String source;

	ParseStream(String src) {
		this.source = src;
	}

	public String getSource() { return source; }
	public ParseStream substring(int start, int end) {
		return new ParseStream(source.substring(start, end));
	}
}
