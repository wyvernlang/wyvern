package wyvern.tools.errors;

/*
 * An interface denoting an object that keeps track of its source location.
 * This functionality still needs to be implemented, but this interface is
 * here as a placeholder.
 */
public interface HasLocation {
    FileLocation getLocation();

    HasLocation UNKNOWN = new HasLocation() {
        public FileLocation getLocation() {
            return new FileLocation("Unknown", -1, -1);
        }
    };
}
