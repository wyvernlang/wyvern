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
    
    public BufferedReader makeReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    public BufferedWriter makeWriter(Socket s) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    
    public boolean isNull(Object obj) {
        return obj == null;
    }
    
    /**
     * Reads a UTF-16 character using the given reader
     * TODO: handle unsupported encoding exceptions gracefully
     */
    public String readUTF(Object r) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = (BufferedReader) r;
        //assumes UTF-16 encoding
        //based on http://www.herongyang.com/Unicode/UTF-16-UTF-16LE-Encoding.html
        int first = reader.read();
        int second = reader.read();
        int block = (first << 8) + second;
        if ((block < 0xD800) || (block > 0xDFFF)) {
            //this indicates code point of decode character
            byte[] res = new byte[2];
            res[0] = (byte)first;
            res[1] = (byte)second;
            return new String(res, "UTF-16LE");
        } else {
            //first surrogate of surrogate pair, so read more
            byte[] res = new byte[4];
            res[0] = (byte)first;
            res[1] = (byte)second;
            res[2] = (byte)reader.read();
            res[3] = (byte)reader.read();
            return new String(res, "UTF-16LE");
        }
    }
    
    public void writeUTF(Object w, String s) throws IOException {
        BufferedWriter writer = (BufferedWriter) w;
        //since java chars support unicode characters, just straight to char[]
        writer.write(s.toCharArray());
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
