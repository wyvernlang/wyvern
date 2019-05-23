package wyvern.stdlib.support;

import java.math.BigInteger;
import java.util.Scanner;

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
    public void printFloat(Double f) {
        java.lang.System.out.print(f);
    }
    
    /** Stdin methods **/
    public Scanner initStdin() {
        return new Scanner(java.lang.System.in);
    }
    public String readLine(Object ob) {
        Scanner sc = (Scanner) ob;
        if (sc.hasNextLine()) {
            return sc.nextLine();
        } else {
            return null; //instead return an option
        }
    }
    public BigInteger read(Object ob) {
        Scanner sc = (Scanner) ob;
        if (sc.hasNextBigInteger()) {
            return sc.nextBigInteger();
        } else {
            return null;
        }
    }
    public void closeScanner(Object ob) {
        Scanner s = (Scanner) ob;
        s.close();
    }
    public boolean isNull(Object ob) {
        return ob == null;
    }
}
