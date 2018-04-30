package wyvern.stdlib.support;

import java.math.BigInteger;

public class Stdio {
    public static final Stdio stdio = new Stdio();

    //TODO: use stderr instead of stdout, allow redirection to a log file
    public static final Stdio debug = new Stdio();

    public void print(String text) {
        java.lang.System.out.print(text);
    }
    public void println() {
        java.lang.System.out.println();
    }
    public void flush() {
        java.lang.System.out.flush();
    }
    public void printInt(BigInteger n) {
        java.lang.System.out.print(n);
    }
    public void printBoolean(boolean b) {
        java.lang.System.out.print(b);
    }
}
