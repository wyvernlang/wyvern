package wyvern.stdlib.support;

import java.nio.file.NoSuchFileException;
import java.util.Optional;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import wyvern.target.corewyvernIL.expression.ObjectValue;

public class Web {

    public static Web utils = new Web();

    public ObjectValue listenAndServe(int port, String root) {
        try {
             HttpServer http = HttpServer.create();
            http.bind(
                new InetSocketAddress(port), 0
            );

            http.createContext("/", new FileHandler(root));
            http.setExecutor(null); // creates a default executor
            http.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Path combine(String root, String file) {
        return FileSystems.getDefault().getPath(root, file);
    }

    /**
     * fetchFile reads the whole file into memory and returns its String representation
     */
    private static Optional<String> fetchFile(Path filename) {

        System.out.println("Filename is : " +  filename.toString());
        System.out.println("IsDirectory() is : " +  filename.toFile().isDirectory());

        if (filename.toFile().isDirectory()) {
            System.out.println("Is directory!");
            filename = combine(filename.toString(), "index.html");
        }

        try {

            BufferedReader r = Files.newBufferedReader(filename, StandardCharsets.UTF_8);

            StringBuilder str = new StringBuilder();
            String ln = null;
            while((ln = r.readLine()) != null) {
                str.append(ln);
                str.append("\n");
            }

            return Optional.of(str.toString()); 
        } catch(NoSuchFileException e) {
        } catch(IOException e) {
        }
        
        return Optional.empty();
    }

    private class FileHandler implements HttpHandler {
        
        String root;

        FileHandler(String root) {
            this.root = root;
        }

        public void handle(HttpExchange t) throws IOException {

            OutputStream os = t.getResponseBody();
            String path = t.getRequestURI().getPath();
            Optional<String> response = fetchFile(combine(this.root, path));

            String content = "";
            int status = 200;

            if(response.isPresent()) {
                content = response.get();
            } else {
                content = "404 File not found";
                status = 404;
            }
 
            System.out.println("Printing file " + combine(this.root, path));
            System.out.println("Response Length:" + content.getBytes().length);
            
            t.sendResponseHeaders(status, content.getBytes().length);
            os.write(content.getBytes());
            os.close();
            

        }
    }

}
