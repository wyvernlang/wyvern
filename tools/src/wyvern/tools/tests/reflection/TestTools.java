package wyvern.tools.tests.reflection;

/**
 * Created by ewang on 3/17/16.
 */
public class TestTools {

    public int assertIntEquals(int x, int y) {
        assert(x == y);
        return 0;
    }

    public int assertStringEquals(String x, String y) {
        assert(x.equals(y));
        return 0;
    }

    public int add(int x, int y) {
        return x + y;
    }
}
