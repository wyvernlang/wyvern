package wyvern.stdlib.support;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public DataOutputStream getDataOutputStream(OutputStream out) {
        return new DataOutputStream(out);
    }

    public DataInputStream getDataInputStream(InputStream in) {
        return new DataInputStream(in);
    }
}
