package wyvern.tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.tools.Interpreter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ReplServer {
	
	    public static void main(String[] args) throws Exception {
	    	//  create a java server and tell it to listen on port 8000
	        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/", new MyHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
	        System.out.println("started server");
	    }

	    static class MyHandler implements HttpHandler {
	        @Override
	        public void handle(HttpExchange t) throws IOException {
	        	//  reading the body of the request
	        	
	        	InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
	        	BufferedReader br = new BufferedReader(isr);
	        	int b;
	        	StringBuilder buf = new StringBuilder(512);
	        	while ((b = br.read()) != -1) {
	        	    buf.append((char) b);
	        	}
	        	br.close();
	        	isr.close();
	        	
	        	String response = "";
	        	//  saving body of post request into file to use in interpreter
	        	if (t.getRequestMethod().equals("POST")){
//	        		PrintWriter out = new PrintWriter("code.wyv");
//	        		out.println(buf.toString());
//	        		System.out.println(buf.toString());
//	        		out.close();
	        		
	        		System.out.println("PROGRAM START");
	        		System.out.println(buf.toString());
	        		System.out.println("PROGRAM END");
	        		
	        		Value v = Interpreter.runFile(buf.toString()); // "C:\\Users\\dinht\\git\\wyvern489\\tools\\code.wyv");
	        		response = v.toString();
	        	}
	        	
	            
	            t.sendResponseHeaders(200, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
	        }
	    }

}
