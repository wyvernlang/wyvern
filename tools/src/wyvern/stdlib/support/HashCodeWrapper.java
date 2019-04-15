package wyvern.stdlib.support;

public class HashCodeWrapper {

    public static final HashCodeWrapper hshc = new HashCodeWrapper();

    public HashCodeWrapper() {

    }

    public int hashCode(Object o) {
        return o.hashCode();
    }
}