package wyvern.stdlib.support;

public class Int {
    public static final Int utils = new Int();

    public int from(String s) {
        return Integer.parseInt(s.trim());
    }
}
