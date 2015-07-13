package wyvern.target.oir;

public class DelegateNative {
	public static native long getFieldAddress (long objectAddress, long fieldPos);
	public static native int getObjectClassID (long objectAddress);
}
