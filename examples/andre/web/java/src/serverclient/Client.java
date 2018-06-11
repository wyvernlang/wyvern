package serverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static int PORT_NUM = 1254;
	
	public static void main(String[] args) {
		
		try {
			Socket soc = new Socket("localhost",PORT_NUM);
			System.out.println("Client started");
			System.out.println("Connected to Server: "+soc.getLocalPort());
			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
			
			String line = ".";
			
			while(!line.equals("")) 
				line = in.readLine();
			
			System.out.println(line);
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
