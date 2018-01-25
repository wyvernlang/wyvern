package wyvern.tools.errors;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public final class FileLocation {
    private final String filename;
    private final int line;
    private final int character;

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

    public String getFilename() {
        return filename;
    }

    public int getLine() {
        return line;
    }

    public int getCharacter() {
        return character;
    }

    public String toString() {
        return "file " + filename + " on line " + line + " column " + character;
    }

    public static final FileLocation UNKNOWN = new FileLocation("Unknown", -1, -1);
}
