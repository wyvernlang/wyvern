package wyvern.tools.errors;

/*
 * An interface denoting an object that keeps track of its source location.
 * This functionality still needs to be implemented, but this interface is
 * here as a placeholder.
 */
public interface HasLocation {
	public int getLine();
	
	public static HasLocation UNKNOWN = new HasLocation() {
		public int getLine() { return -1; }
	};
}
