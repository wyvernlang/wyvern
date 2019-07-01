package wyvern.target.oir;

public final class ForwardNative {
    private ForwardNative() { }
    public static native long getFieldAddress(String className, long objectAddress, long fieldPos);
    public static native int getObjectClassID(long objectAddress);
}
