package wyvern.tools;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

public class Interpreter {
	/**
	 * The interpreter only supports 1 argument, which is the path to the Wyvern
	 * file. If more arguments are supplied, it will exit with an error. Then,
	 * the file is read in to memory in it's entirety, before being executed in
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
			File rootDir = new File(System.getProperty("user.dir"));
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
			final InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA, rootDir, new File(wyvernPath));
			Module m = state.getResolver().load("unknown", filepath.toFile());
			IExpr program = m.getExpression();
			program = state.getResolver().wrap(program, m.getDependencies());
			
			/*ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(filepath.toFile());
			GenContext genCtx = Globals.getGenContext(state);
			Expression program = ast.generateIL(genCtx, null);*/
			TypeContext ctx = Globals.getStandardTypeContext();
			program.typeCheck(ctx);
			Value v = program.interpret(Globals.getStandardEvalContext());
		/*} catch (ParseException e) {
			System.err.println("Parse error: " + e.getMessage());*/
		} catch (ToolError e) {
			System.err.println(e.getMessage());
		}
	}

	// used to set WYVERN_HOME when called programatically
	public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();
}
