package wyvern.stdlib.support;

public class Sys {
    public static final Sys utils = new Sys();

    public void assertion(String description, boolean condition) {
        if (!condition) {
            throw new WyvernAssertion(description);
        }
    }
}
