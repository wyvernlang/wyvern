package wyvern.stdlib.support;

public class Float {
    public static final Float utils = new Float();

    public double from(String s) {
        return java.lang.Double.parseDouble(s.trim());
    }
    
    public double ofInt(int i) {
        return (double) i;
    }
}
