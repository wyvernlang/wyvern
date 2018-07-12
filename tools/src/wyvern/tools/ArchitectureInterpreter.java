package wyvern.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.tools.arch.lexing.ArchLexer;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.parsing.coreparser.arch.ASTArchDesc;
import wyvern.tools.parsing.coreparser.arch.ArchParser;
import wyvern.tools.parsing.coreparser.arch.ArchParserConstants;
import wyvern.tools.parsing.coreparser.arch.DeclCheckVisitor;
import wyvern.tools.parsing.coreparser.arch.Node;

public class ArchitectureInterpreter {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: wyvarch <filename>");
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
      String wyvernPath = System.getenv("WYVERN_HOME");
      if (wyvernPath == null) {
        if (wyvernHome.get() != null) {
          wyvernPath = wyvernHome.get();
        } else {
          System.err.println(
              "must set WYVERN_HOME environmental variable to wyvern project directory");
          return;
        }
      }
      wyvernPath += "/stdlib/";
      // sanity check: is the wyvernPath a valid directory?
      if (!Files.isDirectory(Paths.get(wyvernPath))) {
        System.err.println(
            "Error: WYVERN_HOME is not set to a valid Wyvern project directory");
        return;
      }

      File f = new File(rootLoc + "/" + filepath.toString());
      BufferedReader source = new BufferedReader(new FileReader(f));
      ArchParser wp = new ArchParser(
          (TokenManager) new WyvernTokenManager<ArchLexer, ArchParserConstants>(
              source, "test", ArchLexer.class, ArchParserConstants.class));
      wp.fname = filename;
      Node start = wp.ArchDesc();
      DeclCheckVisitor visitor = new DeclCheckVisitor();
      visitor.visit((ASTArchDesc) start, null);
    } catch (ToolError e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

  // used to set WYVERN_HOME when called programmatically
  public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

  // used to set WYVERN_ROOT when called programmatically
  public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
