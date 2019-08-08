package wyvern.stdlib.support;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        buf.put(s.getBytes());
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
    
    public String stringFromByteBuffer(Object buf) {
        ByteBuffer buffer = (ByteBuffer) buf;
        return new String(buffer.array(), StandardCharsets.UTF_8);
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
    public String readUTF(Object b) {
        ByteBuffer buf = (ByteBuffer) b;
        //assumes UTF-8 encoding
        //num bytes to extract based on https://en.wikipedia.org/wiki/UTF-8
        byte first = buf.get();
        if ((first & 0x80) == 0) {
            //one byte encoding
            byte[] res = new byte[1];
            res[0] = first;
            return new String(res, StandardCharsets.UTF_8);
        } else if ((first & 0xE0) == 0xC0) {
            //two byte encoding
            byte[] res = new byte[2];
            res[0] = first;
            res[1] = buf.get();
            return new String(res, StandardCharsets.UTF_8);
        } else if ((first & 0xF0) == 0xE0) {
            //three byte encoding
            byte[] res = new byte[3];
            res[0] = first;
            res[1] = buf.get();
            res[2] = buf.get();
            return new String(res, StandardCharsets.UTF_8);
        } else if ((first & 0xF8) == 0xF0) {
            //four byte encoding
            byte[] res = new byte[4];
            res[0] = first;
            res[1] = buf.get();
            res[2] = buf.get();
            res[3] = buf.get();
            return new String(res, StandardCharsets.UTF_8);
        }
        return ""; //default
    }
    
    public void writeUTF(Object buf, String s) {
        ByteBuffer buffer = (ByteBuffer) buf;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
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