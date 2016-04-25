package wyvern.tools.interop;

/**
 * Created by ewang on 4/8/16.
 */
public class JavaWrapper {

    public static JObject wrapObject(Object obj) {
        return new JObject(obj);
    }
}
