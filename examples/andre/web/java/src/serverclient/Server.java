package serverclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

	public static int PORT_NUM = 1254;
	public static int POST_HEADER_LENGTH = 556;


	public static void main(String[] args) {

		try {
			ServerSocket serv = new ServerSocket(PORT_NUM);
			System.out.println("Server started, now listening on port: "+PORT_NUM);
			System.out.println("Waiting for connection..");

			for(;;) {
				Socket soc = serv.accept();


				BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));
				sendHTMLFile(out, "bmi.html");
				String str = ".";
				String method = "";
				String all = "";
				str = ".";
				method = "";

				while (!str.equals("")) {
					str = in.readLine();
					method +=  str;
					if(method.contains("POST")) {
						break;
					}
				}


				if(method.startsWith("POST")) {	
					System.out.println("POST data received!");
					String line = "";
					while((line = in.readLine()) != null) {
						if(!in.ready()) break;
						method += line;
						//System.out.println(line);
					}
				}

				if(method.startsWith("POST")) {
					ArrayList<String> params = new ArrayList<String>(processPOST(method));
					bmiCalc(out, params.get(0), params.get(1), params.get(2));
					System.out.println("Response sent");
				
				}


				out.close();
				in.close();
				soc.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}



	}
	
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
			out.println("");
			out.println(htmlContent);
			out.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static List<String> processPOST(String method) {

		String request = method.substring(POST_HEADER_LENGTH);
		//System.out.println(request);
		List<String> params = Arrays.asList( request.replaceAll("^.*?m\"", "").split("------.*?(m\"|$)"));
		return params;
	}

	private static void bmiCalc(PrintWriter out, String ageParam, String heightParam, String weightParam) {
		System.out.println("Starting BMI Calculation");
		int age = Integer.parseInt(ageParam);
		double height = (double) Integer.parseInt(heightParam)/100.0;
		int weight = Integer.parseInt(weightParam);
		double bmi = weight/ Math.pow(height,2);
		String result = getBmi(bmi);
		saveFile(genBmiContent(age, bmi, result));
		String content = genBmiHTMLContent(age,bmi,result);

		out.println(content);
		out.flush();



	}

	private static void saveFile(String genBmiContent) {
		FileWriter fstream;
		try {
			fstream = new FileWriter(System.currentTimeMillis() + "out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(genBmiContent);
			out.flush();
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static String genBmiContent(int age, double bmi, String result) {
		String content = "";
		content+= "BMI Results\n";
		content+="You are "+ +age +" years old, and your BMI is: "+ bmi+ " - meaning you are" + result+" for your height!\n";
		return content;
	}


	private static String genBmiHTMLContent(int age, double bmi, String result) {

		return "<html><h1>BMI Results</h1><h3>You are "+age +" years old, and your BMI is: "+ bmi+" - meaning you are "+ result+ " for your height! </h3><html>";
	}

	private static String getBmi(double bmi) {

		if(bmi < 18.5) {
			return "UNDERWEIGHT";
		}else if(bmi >= 18.5 && bmi < 25) {
			return "NORMAL";
		}else if(bmi >= 25 && bmi < 30) {
			return "OVERWEIGHT";
		}else {
			return "OBESE";
		}
	}


}
