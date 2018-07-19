package wyvern.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import wyvern.target.corewyvernIL.expression.Value;

public final class ReplServer {
    private static HashMap<String, REPL> map = new HashMap<String, REPL>();;
    
    private ReplServer() {
    }
    
    public static void main(String[] args) throws Exception {
        // create a java server and tell it to listen on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("started server");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // reading the body of the request
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            br.close();
            isr.close();
            String response = "";
            if (t.getRequestMethod().equals("POST")) {
                Value v = null;
                System.out.println(buf.toString());
                if (map.containsKey(t.getRequestHeaders().get("id").get(0))) {
                    System.out.println("repl exist for this id");
                    REPL requestREPL = map.get(t.getRequestHeaders().get("id").get(0));
                    try {
                        v = requestREPL.interpretREPL(buf.toString());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("does not");
                    REPL newProgram = new REPL();
                    map.put(t.getRequestHeaders().get("id").get(0), newProgram);
                    try {
                        v = newProgram.interpretREPL(buf.toString());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                response = v.toString();
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
