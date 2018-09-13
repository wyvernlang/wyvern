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
        return "" + c;
    }

    public String concatenate(String s1, String s2){
        return s1+""+s2;
    }
}
