package wyvern.target.oir;

public final class DelegateNative {
    private DelegateNative() { }
    public static native long getFieldAddress(String className, long objectAddress, long fieldPos);
    public static native int getObjectClassID(long objectAddress);
}
