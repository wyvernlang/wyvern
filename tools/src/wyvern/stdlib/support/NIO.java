package wyvern.stdlib.support;

/* Imports */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class NIO {
    public static final NIO nio = new NIO();
    public NIO() { }
    
    public AsynchronousServerSocketChannel makeServerSocketChannel(int port) throws IOException {
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        return serverSocket;
    }
    
    public AsynchronousSocketChannel makeSocketChannel(String hostname, int port) throws IOException {
        AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();
        socket.bind(new InetSocketAddress(hostname, port));
        return socket;
    }
    
    public Future<AsynchronousSocketChannel> serverAccept(Object server) {
        AsynchronousServerSocketChannel s = (AsynchronousServerSocketChannel) server;
        return s.accept();
    }
    
    /* initializes a buffer to pass to Wyvern */
    public ByteBuffer initBuffer(int capacity) {
        return ByteBuffer.allocate(capacity);
    }
    
    /** Async TCP socket methods **/
    
    /* asynchronously reads from socket channel (chan) into byte buffer (b) */
    public Future<Integer> socketRead(Object chan, Object b) {
        AsynchronousSocketChannel socket = (AsynchronousSocketChannel) chan;
        ByteBuffer buf = (ByteBuffer) b;
        Future<Integer> status = socket.read(buf);
        return status;
    }
    
    public Future<Integer> socketWrite(Object chan, Object b) {
        AsynchronousSocketChannel socket = (AsynchronousSocketChannel) chan;
        ByteBuffer buf = (ByteBuffer) b;
        Future<Integer> status = socket.write(buf);
        return status;
    }
    
    public void socketClose(Object chan) throws IOException {
        AsynchronousSocketChannel socket = (AsynchronousSocketChannel) chan;
        socket.close();
    }
    
    /** Future wrapper **/
    public boolean futureIsCancelled(Object obj) {
        Future f = (Future) obj;
        return f.isCancelled();
    }
    
    public boolean futureIsDone(Object obj) {
        Future f = (Future) obj;
        return f.isDone();
    }
    
    public <T> T futureGet(Object obj) {
        Future<T> f = (Future<T>) obj;
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException ex) {
            return null; //might need to change later
        }
    }
    
    public boolean futureCancel(Object obj) {
        Future f = (Future) obj;
        return f.cancel(true);
    }
    
    public <T> Future<T> futureNew(T value) {
        return CompletableFuture.completedFuture(value);
    }

    /** ByteBuffer abstraction **/
    public ByteBuffer makeByteBuffer(int allocSize) {
        return ByteBuffer.allocate(allocSize);
    }
    
    public int byteBufferGet(Object buf, int index) {
        ByteBuffer b = (ByteBuffer) buf;
        return b.get(index);
    }
    
    public void byteBufferSet(Object buf, int index, byte value) {
        ByteBuffer b = (ByteBuffer) buf;
        b.put(index, value);
    }
    
}