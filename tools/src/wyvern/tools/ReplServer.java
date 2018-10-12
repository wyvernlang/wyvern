package wyvern.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.tools.parsing.coreparser.ParseException;

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
        System.out.println("started server: " + server.getAddress());
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
            if (t.getRequestMethod().equals("GET")) {
                System.out.println("got here");
            }
            if (t.getRequestMethod().equals("POST")) {
                System.out.println("got here");
                if (t.getRequestHeaders().get("operation").get(0).equals("closing")) {
                    System.out.println("deleting program for id: " + t.getRequestHeaders().get("id").get(0));
                    map.remove(t.getRequestHeaders().get("id").get(0));
                } else if (t.getRequestHeaders().get("operation").get(0).equals("interpretREPL")) {
                    String v = null;

                    System.out.println("Running code: " + buf.toString());
                    System.out.println("For id: " + t.getRequestHeaders().get("id").get(0));
                    if (map.containsKey(t.getRequestHeaders().get("id").get(0))) {
                        REPL requestREPL = map.get(t.getRequestHeaders().get("id").get(0));
                        try {
                            v = requestREPL.interpretREPL(buf.toString());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        REPL newProgram = new REPL();
                        map.put(t.getRequestHeaders().get("id").get(0), newProgram);
                        try {
                            v = newProgram.interpretREPL(buf.toString());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (v != null) {
                        response = v;
                    } else {
                        response = " ";
                    }
                } else if (t.getRequestHeaders().get("operation").get(0).equals("interpretModule")) {
                    String[] moduleData = buf.toString().split("module def");
                    String resourceType = moduleData[0];
                    String module = "module def" + moduleData[1];
                    if (map.containsKey(t.getRequestHeaders().get("id").get(0))) {
                        REPL requestREPL = map.get(t.getRequestHeaders().get("id").get(0));
                        Value v1 = null;
                        Value v2 = null;
                        try {
                            v1 = requestREPL.updateCode(resourceType);
                            v2 = requestREPL.interpretModule(module);
                            response =  v1.toString() + "\n" + v2.toString();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        REPL newProgram = new REPL();
                        map.put(t.getRequestHeaders().get("id").get(0), newProgram);
                        Value v1 = null;
                        Value v2 = null;
                        try {
                            v1 = newProgram.updateCode(resourceType);
                            v2 = newProgram.interpretModule(module);
                            response = v1.toString() + "\n" + v2.toString();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (t.getRequestHeaders().get("operation").get(0).equals("saveModule")) {
                    String[] moduleData = buf.toString().split("=:=");
                    String moduleName = moduleData[0];
                    String moduleCode = moduleData[1];
                    
                    File file = new File("../tools/src/wyvern/tools/tests/modules/replModules/" + moduleName + ".wyv");
                    OutputStream fileWriter = new FileOutputStream(file);
                    fileWriter.write(moduleCode.getBytes());
                    fileWriter.flush();
                    fileWriter.close();
                    file.createNewFile();
                } else if (t.getRequestHeaders().get("operation").get(0).equals("loadModule")) {
                    String moduleName = buf.toString();
                    File file = new File("../tools/src/wyvern/tools/tests/modules/replModules/" + moduleName + ".wyv");
                    
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                   
                    String st;
                    while ((st = fileReader.readLine()) != null) {
                        response = response + st + "\n";
                    }
                } else if (t.getRequestHeaders().get("operation").get(0).equals("loadAllModule")) {
                    try {
                       
                        File folder = new File("../tools/src/wyvern/tools/tests/modules/replModules");
                        File[] listOfFiles = folder.listFiles();

                        for (int i = 0; i < listOfFiles.length; i++) {
                            if (listOfFiles[i].getName().endsWith(".wyv")) {
                                response = response + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4) + " ";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
                t.getResponseHeaders().add("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept, Authorization, id, operation");
                t.sendResponseHeaders(204, -1);
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
