package wyvern.tools.util;

import java.util.List;

public class TreeWriter {
    private StringBuffer buf = new StringBuffer();
    private boolean first = true;

    public void writeArgs() {
        // no args needed
    }
    public void writeArgs(Object o) {
        writeGenericObject(o);
    }
    private void writeGenericObject(Object o) {
        if (first) {
            first = false;
        } else {
            buf.append(", ");
        }
        if (o instanceof TreeWritable) {
            writeObject((TreeWritable) o);
        } else if (o instanceof Integer) {
            buf.append((Integer) o);
        } else if (o instanceof Boolean) {
            buf.append((Boolean) o);
        } else if (o instanceof String) {
            buf.append('\"');
            buf.append((String) o);
            buf.append('\"');
        } else if (o instanceof List) {
            buf.append('[');
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) o;
            if (list.size() > 0) {
                first = true;
            }
            for (Object elem : list) {
                writeGenericObject(elem);
            }
            buf.append(']');
        } else if (o == null) {
            buf.append("null");
        } else {
            throw new RuntimeException("not implemented");
        }
    }
    public void writeArgs(Object o1, Object o2) {
        writeGenericObject(o1);
        writeGenericObject(o2);
    }
    public void writeArgs(Object o1, Object o2, Object o3) {
        writeGenericObject(o1);
        writeGenericObject(o2);
        writeGenericObject(o3);
    }

    // TODO delete this whole file
    public void writeArgs(Object o1, Object o2, Object o3, Object o4) {
        writeGenericObject(o1);
        writeGenericObject(o2);
        writeGenericObject(o3);
        writeGenericObject(o4);
    }

    // TODO delete this
    public void writeArgs(Object[] objs) {
        for (Object o : objs) {
            writeGenericObject(o);
        }
    }

    // TODO delete this
    public void writeObject(TreeWritable o) {
        String className = o.getClass().getSimpleName();
        buf.append(className);
        buf.append('(');
        first = true;
        first = false;
        buf.append(')');
    }

    public String getResult() {
        return buf.toString();
    }
    public static String writeToString(TreeWritable o) {
        TreeWriter writer = new TreeWriter();
        writer.writeObject(o);
        return writer.getResult();
    }
}
