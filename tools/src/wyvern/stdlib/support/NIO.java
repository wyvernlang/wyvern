package wyvern.stdlib.support;

/* Imports */
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;

import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

/* Future -> CompletableFuture conversion as described in 
http://www.thedevpiece.com/converting-old-java-future-to-completablefuture/ */
final class CompletablePromiseContext {
    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    
    private CompletablePromiseContext() {
        //not called
    }
    
    public static void schedule(Runnable r) {
        SERVICE.schedule(r, 1, TimeUnit.MILLISECONDS);
    }
}

class CompletablePromise<V> extends CompletableFuture<V> {
    private Future<V> future;
    
    CompletablePromise(Future<V> future) {
        this.future = future;
        CompletablePromiseContext.schedule(this::tryToComplete);
    }
    
    private void tryToComplete() {
        if (future.isDone()) {
            try {
                complete(future.get());
            } catch (InterruptedException e) {
                completeExceptionally(e);
            } catch (ExecutionException e) {
                completeExceptionally(e.getCause());
            }
            return;
        }
        if (future.isCancelled()) {
            cancel(true);
            return;
        }
        //continue to loop
        CompletablePromiseContext.schedule(this::tryToComplete);
    }
}


public class NIO {
    public static final NIO nio = new NIO();
    public NIO() { }
    
    public AsynchronousServerSocketChannel makeServerSocketChannel(int port) throws IOException {
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        return serverSocket;
    }
    
    public AsynchronousSocketChannel makeSocketChannel() throws IOException {
        AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();
        //socket.connect(new InetSocketAddress(hostname, port));
        return socket;
    }
    
    public Future<Void> socketChannelConnect(Object sc, String hostname, int port) {
        AsynchronousSocketChannel chan = (AsynchronousSocketChannel) sc;
        return chan.connect(new InetSocketAddress(hostname, port));
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
    
    // works for any of the socket channels
    public void closeChannel(Object chan) throws IOException {
        Channel channel = (Channel) chan;
        channel.close();
    }
    
    /** Async UDP channel methods **/
    
    public DatagramChannel makeDatagramChannel(int port) throws IOException {
        DatagramChannel chan = DatagramChannel.open();
        chan.bind(new InetSocketAddress(port));
        chan.configureBlocking(false);
        return chan;
    }
    
    public SocketAddress receiveUDP(Object chan, Object buf) throws IOException {
        DatagramChannel channel = (DatagramChannel) chan;
        ByteBuffer buffer = (ByteBuffer) buf;
        return channel.receive(buffer);
    }
    
    public int sendUDP(Object chan, Object buf, Object addr) throws IOException {
        DatagramChannel channel = (DatagramChannel) chan;
        SocketAddress target = (SocketAddress) addr;
        ByteBuffer src = (ByteBuffer) buf;
        src.rewind();
        return channel.send(src, target);
    }
    
    public boolean isNull(Object obj) {
        return obj == null;
    }
    
    public SocketAddress makeSocketAddress(String hostname, int port) {
        return new InetSocketAddress(hostname, port);
    }
    
    public String addressToString(Object obj) {
        return ((SocketAddress) obj).toString();
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
    
    // applies wyvern function fn to wyvern value param
    public <T> Value wyvernApplication(T param, ObjectValue fn) {
        LinkedList<Value> params = new LinkedList<>();
        params.add((Value) param);
        return fn.invoke("apply", params).executeIfThunk();
    }
    
    // supplies wyvern callback to future value
    public <T> CompletableFuture<Value> applyCallback(Object obj, ObjectValue fn) {
        Future<T> f = (Future<T>) obj;
        CompletableFuture<T> completableFuture = new CompletablePromise<T>(f);
        LinkedList<Value> params = new LinkedList<>();
        return completableFuture.thenApply(res -> wyvernApplication(res, fn));
    }
    
    
}