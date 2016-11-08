package wyvern.stdlib.support;

public class StringHelper {
	public static StringHelper utils = new StringHelper();

	public boolean testEqual(String s1, String s2) {
		return s1.equals(s2);
	}

    public String ofInt(int x) {
        return Integer.toString(x);
    }
}
