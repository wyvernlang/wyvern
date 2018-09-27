package wyvern.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.tools.errors.ToolError;

public final class BytecodeCompiler {
    private BytecodeCompiler() { }

    /**
     * The bytecode compiler only supports 1 argument, which is the path to the Wyvern
     * file. If more arguments are supplied, it will exit with an error. Then,
     * the file is read in to memory in it's entirety, before being compiled in
     * an empty context. The resulting value is printed to the screen.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: wyvern <filename>");
            System.exit(1);
        }
        String filename = args[0];
        Path filepath = Paths.get(filename);
        if (!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
            System.exit(1);
        }
        try {
            String rootLoc;
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }
            File rootDir = new File(rootLoc);
            String wyvernPath = System.getenv("WYVERN_HOME");
            if (wyvernPath == null) {
                if (wyvernHome.get() != null) {
                    wyvernPath = wyvernHome.get();
                } else {
                    System.err.println("must set WYVERN_HOME environmental variable to wyvern project directory");
                    return;
                }
            }
            wyvernPath += "/stdlib/";
            // sanity check: is the wyvernPath a valid directory?
            if (!Files.isDirectory(Paths.get(wyvernPath))) {
                System.err.println("Error: WYVERN_HOME is not set to a valid Wyvern project directory");
                return;
            }
            final InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVASCRIPT, rootDir, new File(wyvernPath));
            Module m = state.getResolver().load("unknown", filepath.toFile(), true);

            BytecodeOuterClass.Bytecode wyb = state.getResolver().emitBytecode(m);

            // Good enough
            String outputFile = filename.replace(".wyv", ".wyb");

            wyb.writeTo(new FileOutputStream(outputFile));
        } catch (ToolError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    // used to set WYVERN_HOME when called programmatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}

