package serverclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static int PORT_NUM = 1254;

	public static void sendHTMLFile(PrintWriter out, String filename) {

		File file = new File(filename);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String htmlContent = "";
			String line = "";
			while((line = br.readLine()) != null) {
				htmlContent+= line;
			}
			br.close();
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type: text/html");
			out.println("Server: Bot");
			out.println("");
			out.println(htmlContent);
			out.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			ServerSocket serv = new ServerSocket(PORT_NUM);
			System.out.println("Server started, now listening on port: "+PORT_NUM);
			System.out.println("Waiting for connection..");

			Socket soc = serv.accept();
			System.out.println(soc.getInetAddress()+ " connected");

			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			
			sendHTMLFile(out, "bmi.html");

			String request, response;
			while ((request = in.readLine()) != null) {
			  response = processRequest(request);
			  out.println(response);
			  if ("Done".equals(request)) {
			    break;
			  }
			}
	        
	        out.close();
	        in.close();
	        soc.close();

		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	private static String processRequest(String request) {
		System.out.println(request);
		
		return "Not Done";
	}


}
