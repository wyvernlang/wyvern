package wyvern.stdlib.support;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * TODO: handle unsupported encoding exceptions
 */

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
    
    public ByteBuffer makeFromString(String s) throws UnsupportedEncodingException {
        byte[] ins = s.getBytes("UTF-16LE");
        ByteBuffer buf = ByteBuffer.allocate(ins.length);
        buf.put(ins);
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
    
    public String stringFromByteBuffer(Object buf) throws UnsupportedEncodingException {
        ByteBuffer buffer = (ByteBuffer) buf;
        return new String(buffer.array(), "UTF-16LE");
    }
    
    /**
     * Initializing for Reader
     * Leaves the limit the same and sets position to 0, reading data contained by the buffer
     */
    public void initializeForRead(Object b) {
        ByteBuffer buf = (ByteBuffer) b;
        buf.rewind();
    }
    
    /**
     * Initializing for Writer
     * Sets limit to capacity and position to 0, writing in essentially empty buffer
     */
    public void initializeForWrite(Object b) {
        ByteBuffer buf = (ByteBuffer) b;
        buf.clear();
    }
    
    //gets a byte
    public int getNext(Object b) {
        ByteBuffer buf = (ByteBuffer) b;
        if(buf.position() == buf.limit()) {
            return -1;
        }
        return buf.get();
    }
    
    //check defaults for this method
    //consistent with encoding for filesystem readUTF
    public String readUTF(Object b) throws IOException {
        ByteBuffer buf = (ByteBuffer) b;
        //assumes UTF-16 encoding
        //based on http://www.herongyang.com/Unicode/UTF-16-UTF-16LE-Encoding.html
        byte first = buf.get();
        byte second = buf.get();
        int block = (first << 8) + second;
        try {
            if ((block < 0xD800) || (block > 0xDFFF)) {
                //this indicates code point of decode character
                byte[] res = new byte[2];
                res[0] = first;
                res[1] = second;
                return new String(res, "UTF-16LE");
            } else {
                //first surrogate of surrogate pair, so read more
                byte[] res = new byte[4];
                res[0] = first;
                res[1] = second;
                res[2] = buf.get();
                res[3] = buf.get();
                return new String(res, "UTF-16LE");
            }
        } catch (UnsupportedEncodingException e) {
            return ""; //temporary
        }
    }
    
    public void writeUTF(Object buf, String s) throws UnsupportedEncodingException {
        ByteBuffer buffer = (ByteBuffer) buf;
        byte[] bytes = s.getBytes("UTF-16LE");
        buffer.put(bytes);
    }
    
    public void writeNext(Object b, int n) {
        ByteBuffer buf = (ByteBuffer) b;
        buf.putInt(n);
    }
    
    public void close() {
        return;
    }
}