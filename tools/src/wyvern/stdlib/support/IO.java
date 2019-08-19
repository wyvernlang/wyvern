package wyvern.stdlib.support;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mkirwin on 7/18/17.
 */
public class IO {
    public static final IO io = new IO();
    public IO() { }

    /**
     * Make a new ServerSocket using a provided port
     * @param port
     * @return
     * @throws IOException
     */
    public ServerSocket makeServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    /**
     * Make a new Socket on the client side
     * @param hostname
     * @param port
     * @return
     * @throws IOException
     */
    public Socket makeSocket(String hostname, int port) throws IOException {
        return new Socket(hostname, port);
    }
    /*
    public DataOutputStream getDataOutputStream(OutputStream out) {
        return new DataOutputStream(out);
    }

    public DataInputStream getDataInputStream(InputStream in) {
        return new DataInputStream(in);
    }
    */
    
    /**
     * Added for Reader/Writer types in Wyvern
     */
    public BufferedReader makeReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    public BufferedWriter makeWriter(Socket s) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    
    public boolean isNull(Object obj) {
        return obj == null;
    }
    
    public DataInputStream makeBinaryReader(Socket s) throws IOException {
        return new DataInputStream(s.getInputStream());
    }
    
    public DataOutputStream makeBinaryWriter(Socket s) throws IOException {
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.flush();
        return dos;
    }
    
    /**
     * Methods for synchronous UDP operations
     * Created by jennyafish 7/31/19.
     */
    public DatagramChannel makeDatagramChannel(int port) throws IOException {
        DatagramChannel chan = DatagramChannel.open();
        chan.bind(new InetSocketAddress(port));
        return chan;
    }
    
    public int sendDatagram(DatagramChannel chan, Object buf, Object addr) throws IOException {
        SocketAddress address = (SocketAddress) addr;
        ByteBuffer buffer = (ByteBuffer) buf;
        buffer.rewind();
        return chan.send(buffer, address);
    }
    
    public SocketAddress receiveDatagram(DatagramChannel chan, Object buf) throws IOException {
        ByteBuffer buffer = (ByteBuffer) buf;
        buffer.rewind();
        return chan.receive(buffer);
    }
    
    public void closeChannel(DatagramChannel s) throws IOException {
        s.close();
    }
    
    public SocketAddress makeSocketAddress(String hostname, int port) {
        return new InetSocketAddress(hostname, port);
    }
}
