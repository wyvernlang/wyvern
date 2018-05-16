import java.io.*;
import java.util.*;
import java.io.OutputStream;
import java.net.*;
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
            server.createContext("/", new MyRootHandler());
			server.createContext("/GET", new MyGetHandler());
			server.createContext("/POST", new MyPostHandler());
            server.setExecutor(null); 
            server.start();
            return server;
        } catch (IOException e) {
            return null; // FIXME
        }
    }

    static class MyRootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        if(t.getRequestMethod().equalsIgnoreCase("POST")){
            System.out.println("Doing POST");
            return;
        }		
		Headers h = t.getResponseHeaders();
            System.out.println("Client "+t.getRemoteAddress().toString() +"connected");
            String response = "<html><h1>Welcome to the Wyvern HTTP Server</h1></html>";
            h.add("Content-type", "text/html");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
	  static class MyGetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Headers h = t.getResponseHeaders();
             
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
            h.add("Content-type", "text/html");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
	  static class MyPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Headers requestHeaders = t.getRequestHeaders();
            Set<Map.Entry<String,List<String>>> entries = requestHeaders.entrySet();
            System.out.println("POST sent to Server: "+ t.getLocalAddress().toString());
            //process request
            int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));
			InputStream is = t.getRequestBody();
            String response = "";
            String query = convertIStoString(is);
			System.out.println(query);
			Map<String, String> params = splitQuery(query);
            String fname = params.get("fname");
 
            // Send Response
            response = "<html><h1>Hello " +fname +"!</h1></html>"; 
            Headers responseHeaders = t.getResponseHeaders();
			t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
            
            responseHeaders.add("Content-type", "text/html");
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
		
		public static String convertIStoString(InputStream is) throws IOException {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toString("UTF-8");
		}
		
		public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
			System.out.println("Q:"+query);
			Map<String, String> query_pairs = new LinkedHashMap<String, String>();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
			return query_pairs;
		}

    }

}
