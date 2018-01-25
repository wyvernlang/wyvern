package wyvern.tools.util;

public abstract class AbstractTreeWritable implements TreeWritable {
    @Override
    public String toString() {
        return TreeWriter.writeToString(this);
    }
}
