package wyvern.tools.util;

public final class FileLocation {
	public final String filename;
	public final int line;
	public final int character;
	public FileLocation(String filename, int line, int charP) {
		this.filename = filename;
		this.line = line;
		this.character = charP;
	}
	
	public String toString() {
		return filename+":"+line+","+character;
	}
}
