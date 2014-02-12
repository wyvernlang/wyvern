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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result + filename.hashCode();
		result = prime*result + line;
		result = prime*result + character;
		return result;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof FileLocation &&
				((FileLocation) other).filename.equals(filename) &&
				((FileLocation) other).character == this.character &&
				((FileLocation) other).line == this.line;
	}
	
	public final static FileLocation UNKNOWN = new FileLocation("Unknown",-1,-1);
}
