package wyvern.stdlib.support;
import java.awt.Color;

import javax.swing.UIManager;

public final class JavaHelper {
    private JavaHelper() { }

    public static final NativeUIManager nativeUIManager = new NativeUIManager();
    public static class NativeUIManager {
        public void paintUIAttributeBlack(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.BLACK);
        }
    }
}
