package wyvern.tools.errors;

public final class FileLocation {
	public final String filename;
	public final int line;
	public final int character;
	public final int globalChar;
	public FileLocation(String filename, int line, int charP) {
		this.filename = filename;
		this.line = line;
		this.character = charP;
		this.globalChar = 0;
	}

	public FileLocation(String filename, int line, int charP, int gchar) {
		this.filename = filename;
		this.line = line;
		this.character = charP;
		this.globalChar = gchar;
	}

	public String toString() {
		return filename+":"+line+","+character;
	}
	
	public final static FileLocation UNKNOWN = new FileLocation("Unknown",-1,-1);
}
