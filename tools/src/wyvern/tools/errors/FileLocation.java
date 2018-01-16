package wyvern.tools.errors;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public final class FileLocation {
	public final String filename;
	public final int line;
	public final int character;
	public FileLocation(String filename, int line, int charP) {
		this.filename = filename;
		this.line = line;
		this.character = charP;
	}

	public FileLocation(InputPosition copperState) {
		filename = copperState.getFileName();
		line = copperState.getLine();
		character = copperState.getColumn();
	}
	
	public String toString() {
		return "file " + filename + " on line " + line + " column " + character;
	}
	
	public final static FileLocation UNKNOWN = new FileLocation("Unknown",-1,-1);
}
