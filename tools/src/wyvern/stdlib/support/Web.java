package wyvern.stdlib.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import wyvern.target.corewyvernIL.expression.ObjectValue;

/**
 * Web provided utilities to the 'web' Wyvern module, found at '/wyvern/lib/wyvern/util/web/'
 * Right now, only a static file server is implemented.
 */
public class Web {

    // Currently, Wyvern can only import static objects
    public static final Web utils = new Web();

    /** listenAndServer serves static web content.
     * @param port: the port on which to listen
     * @param root: the root directory of the filesystem. All requests will directed relative to this root.
     */
    public ObjectValue listenAndServe(int port, String root) {

        // It's possible that the port could be in use and we could fail to connect.
        try {

            HttpServer http = HttpServer.create();
            http.bind(
                    new InetSocketAddress(port), 0
                    );

            // All look up requests are handled by the FileHandler, which looks up
            // the file associated with the URI and
            // writes its contents into the response output stream
            http.createContext("/", new FileHandler(root));
            http.setExecutor(null); // creates a default executor
            http.start();

            // Catch any failures to connect
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * combine is a utility method that concatenates a base directory with an child path.
     */
    private static Path combine(String root, String file) {
        return FileSystems.getDefault().getPath(root, file);
    }

    /**
     * fetchFile reads the whole file into memory and returns its String representation
     */
    private static Optional<String> fetchFile(Path filename) {

        // Handle the special case where looking up a directory really means
        // looking for index.html in that directory
        if (filename.toFile().isDirectory()) {
            filename = combine(filename.toString(), "index.html");
        }

        try {

            BufferedReader r = Files.newBufferedReader(filename, StandardCharsets.UTF_8);
            StringBuilder str = new StringBuilder();
            String ln = null;
            // Calling readLine consumes the newline charater, so it must be added back in manually
            // @author Robbie: There must be a more elegant way to stream bufferedReader input to a PrintStream output.
            while ((ln = r.readLine()) != null) {
                str.append(ln);
                str.append("\n");
            }

            return Optional.of(str.toString());

            // Perhaps the file cannot be found
            // Catch those outcomes, and return an empty optional, indicating a 404 status
        } catch (NoSuchFileException e) {
        } catch (IOException e) {
        }

        return Optional.empty();
    }

    /**
     * FileHandler is the HTTP request handler that simply reads the contents of the file
     * into memory and rewrites it out as a response, erroring if the file does not exist.
     */
    private class FileHandler implements HttpHandler {

        private String root;

        /**
         * @param root: The base directory of the fileserver
         */
        FileHandler(String root) {
            this.root = root;
        }

        /**
         * This method is called every time a request comes in.
         * handle tries to read the file into memory, 404s if it doesn't exist, and
         * writes it out to the response otherwise
         */
        public void handle(HttpExchange t) throws IOException {

            OutputStream os = t.getResponseBody();
            String path = t.getRequestURI().getPath();
            Optional<String> response = fetchFile(combine(this.root, path));

            String content = "";
            int status = 200;

            if (response.isPresent()) {
                content = response.get();
            } else {
                content = "404 File not found";
                status = 404;
            }

            t.sendResponseHeaders(status, content.getBytes().length);
            os.write(content.getBytes());
            os.close();
        }
    }
}
