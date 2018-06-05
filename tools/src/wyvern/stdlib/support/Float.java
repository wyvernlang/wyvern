package wyvern.stdlib.support;

public class Float {
    public static final Float utils = new Float();

    public float from(String s) {
        return java.lang.Float.parseFloat(s.trim());
    }
}
