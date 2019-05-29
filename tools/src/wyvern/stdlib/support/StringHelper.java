package wyvern.stdlib.support;

import java.math.BigInteger;

public class StringHelper {
    public static final StringHelper utils = new StringHelper();

    public boolean testEqual(String s1, String s2) {
        return s1.equals(s2);
    }

    public String ofInt(BigInteger x) {
        return x.toString();
    }

    public String ofFloat(double d) {
        return Double.toString(d);
    }

    public String ofFormattedFloat(String format, double d) {
        return String.format(format, d);
    }

    public String ofCharacter(char c) {
        return String.valueOf(c);
    }

    public String ofASCII(int i) {
        return Character.toString((char) i);
    }

    public String valueOf(int i) {
        return String.valueOf(i);
    }
}
