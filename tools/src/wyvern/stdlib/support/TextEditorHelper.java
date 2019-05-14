package wyvern.stdlib.support;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

public final class TextEditorHelper {
    private TextEditorHelper() { }

    public static final NativeUtils nativeUtils = new NativeUtils();
    public static class NativeUtils {
        public Object getNullValue() {
            return null;
        }
        public String getSystemProperty(String s) {
            return System.getProperty(s);
        }
        public void exitSystem() {
            System.exit(0);
        }
    }

    public static final NativeUIManager nativeUIManager = new NativeUIManager();
    public static class NativeUIManager {
        public void enableSettingLookAndFeel() {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                System.err.println("Error setting look and feel: " + e.getMessage());
                e.printStackTrace();
            }
        }
        public void setUIProperty(String element, String attribute, Color color) {
            UIManager.put(element + "." + attribute, color);
        }
        public void paintUIAttributeBlack(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.BLACK);
        }
        public void paintUIAttributeBlue(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.BLUE);
        }
        public void paintUIAttributeCyan(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.CYAN);
        }
        public void paintUIAttributeDarkGray(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.DARK_GRAY);
        }
        public void paintUIAttributeGray(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.GRAY);
        }
        public void paintUIAttributeGreen(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.GREEN);
        }
        public void paintUIAttributeLightGray(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.LIGHT_GRAY);
        }
        public void paintUIAttributeMagenta(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.MAGENTA);
        }
        public void paintUIAttributePink(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.PINK);
        }
        public void paintUIAttributeRed(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.RED);
        }
        public void paintUIAttributeWhite(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.WHITE);
        }
        public void paintUIAttributeYellow(String element, String attribute) {
            UIManager.put(element + "." + attribute, Color.YELLOW);
        }
        public void updateLookAndFeel(JFrame jFrame) {
            SwingUtilities.updateComponentTreeUI(jFrame);
        }
    }

    public static final NativeJTextArea nativeJTextArea = new NativeJTextArea();
    public static class NativeJTextArea {
        public JTextArea create(int rows, int columns) {
            return new JTextArea(rows, columns);
        }
    }

    public static final NativeJFileChooser nativeJFileChooser = new NativeJFileChooser();
    public static class NativeJFileChooser {
        public JFileChooser create(String currentDirectoryPath) {
            return new JFileChooser(currentDirectoryPath);
        }
        public int getApproveOption() {
            return JFileChooser.APPROVE_OPTION;
        }
    }

    public static final NativeJScrollPane nativeJScrollPane = new NativeJScrollPane();
    public static class NativeJScrollPane {
        public JScrollPane create(JTextArea view, int vsbPolicy, int hsbPolicy) {
            return new JScrollPane(view, vsbPolicy, hsbPolicy);
        }
        public int getHorizontalScrollbarAlwaysValue() {
            return JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS;
        }
        public int getVerticalScrollbarAlwaysValue() {
            return JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        }
    }

    public static final NativeJFrame nativeJFrame = new NativeJFrame();
    public static class NativeJFrame {
        public JFrame create() {
            return new JFrame();
        }
        public int getExitOnClose() {
            return JFrame.EXIT_ON_CLOSE;
        }
    }

    public static final NativeJMenuBar nativeJMenuBar = new NativeJMenuBar();
    public static class NativeJMenuBar {
        public JMenuBar create() {
            return new JMenuBar();
        }
    }

    public static final NativeJMenu nativeJMenu = new NativeJMenu();
    public static class NativeJMenu {
        public JMenu create(String s) {
            return new JMenu(s);
        }
    }

    public static final NativeJOptionPane nativeJOptionPane = new NativeJOptionPane();
    public static class NativeJOptionPane {
        public int showConfirmDialog(Object message, String title, int optionType) {
            return JOptionPane.showConfirmDialog(null, message, title, optionType);
        }
        public void showMessageDialog(Object message, String title, int messageType) {
            JOptionPane.showMessageDialog(null, message, title, messageType);
        }
        public int getYesNoOptionValue() {
            return JOptionPane.YES_NO_OPTION;
        }
        public int getYesOptionValue() {
            return JOptionPane.YES_OPTION;
        }
        public int getErrorMessageValue() {
            return JOptionPane.ERROR_MESSAGE;
        }
        public int getPlainMessageValue() {
            return JOptionPane.PLAIN_MESSAGE;
        }
    }

    public static final NativeActionCreator nativeActionCreator = new NativeActionCreator();
    public static class NativeActionCreator {
        public NativeAction createWithAction(String name, ObjectValue action) {
            return new NativeAction(name, action);
        }
        public NativeAction create(String name) {
            return new NativeAction(name);
        }
    }

    public static class NativeAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private ObjectValue action;

        public NativeAction(String name) {
            super(name);
        }

        public NativeAction(String name, ObjectValue action) {
            super(name);
            this.action = action;
        }

        public void setAction(ObjectValue action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (action != null) {
                action.invoke("apply", new LinkedList<Value>()).executeIfThunk();
            }
        }
    }

    public static final NativeKeyListenerCreator nativeKeyListenerCreator = new NativeKeyListenerCreator();
    public static class NativeKeyListenerCreator {
        public NativeKeyListener create(ObjectValue keyAction) {
            return new NativeKeyListener(keyAction);
        }
    }

    public static class NativeKeyListener extends KeyAdapter {
        private final ObjectValue keyAction;

        public NativeKeyListener(ObjectValue keyAction) {
            this.keyAction = keyAction;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (keyAction != null) {
                keyAction.invoke("apply", new LinkedList<Value>()).executeIfThunk();
            }
        }
    }
}
