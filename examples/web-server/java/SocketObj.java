import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketObj {
    public static final SocketObj skt = new SocketObj();
    public SocketObj() { }

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

    public OutputStream getOutputStream(Socket soc) throws IOException  {
        return soc.getOutputStream();
    }

    public InputStream getInputStream(Socket soc) throws IOException {
        return soc.getInputStream();
    }
	
	public PrintWriter makePrintWriter(OutputStreamWriter osw) throws IOException {
		return new PrintWriter(osw);
	}
	
	public OutputStreamWriter makeOutputStreamWriter(Socket soc) throws IOException {
		return new OutputStreamWriter(soc.getOutputStream());
	}
	
}
