package wyvern.stdlib.support;

import java.nio.ByteBuffer;

public class ByteBufferWrapper {

    public static final ByteBufferWrapper bb = new ByteBufferWrapper();
    public ByteBufferWrapper() { };

    /** ByteBuffer abstraction **/
    public ByteBuffer makeByteBuffer(int allocSize) {
        return ByteBuffer.allocate(allocSize);
    }
    
    public int byteBufferGet(Object buf, int index) {
        ByteBuffer b = (ByteBuffer) buf;
        return b.get(index);
    }
    
    //note that this puts an int
    public void byteBufferSet(Object buf, int index, int value) {
        ByteBuffer b = (ByteBuffer) buf;
        b.putInt(index, value);
    }
    
    public ByteBuffer makeFromString(String s) {
        ByteBuffer buf = ByteBuffer.allocate(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            buf.putChar(s.charAt(i));
        }
        return buf;
    }
    
    public int charGet(Object buf, int i) {
        ByteBuffer b = (ByteBuffer) buf;
        return (int) b.getChar(i);
    }
    
    public void charSet(Object buf, int i, int c) {
        ByteBuffer b = (ByteBuffer) buf;
        b.putChar(i, (char) c);
    }
    
}