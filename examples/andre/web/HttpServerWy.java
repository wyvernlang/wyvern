import java.io.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerWy {

    public static final HttpServerWy serv = new HttpServerWy();

    public HttpServer startServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/test", new MyHandler());
            server.setExecutor(null); 
            server.start();
            return server;
        } catch (IOException e) {
            return null; // FIXME
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Headers h = t.getResponseHeaders();

            System.out.println("Doing stuff.");
             
            String response = "";
            try{
              File myFile = new File("index.html");
              BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(myFile)));
              String line = "";
              while((line = br.readLine()) != null){
                response += line;
              }
              br.close();
            }catch(IOException e){
              e.printStackTrace();
            }
            h.add("Content-type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
