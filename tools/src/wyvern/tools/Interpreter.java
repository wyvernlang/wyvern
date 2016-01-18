package wyvern.tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class Interpreter {


    /**
     * The interpreter only supports 1 argument, which is the path to the Wyvern file.
     * If more arguments are supplied, it will exit with an error.
     * Then, the file is read in to memory in it's entirety, before being executed in an empty context. The resulting value is printed to the screen.
     */
	public static void main(String[] args) {
		
        if(args.length != 1) {
			System.err.println("usage: wyvern <filename>");
			System.exit(1);
        }

        String filename = args[0];
        Path filepath = Paths.get(filename);
        if(!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
			System.exit(1);
        }

        String source = TestUtil.readFile(filepath.toAbsolutePath().toString());
        try {
            ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
    		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
            Expression program = ast.generateIL(genCtx, null);
            TypeContext ctx = TypeContext.empty();
            program.typeCheck(ctx);
            Value v = program.interpret(EvalContext.empty());
            System.out.println(v.toString());
        } catch(ParseException e) {
            System.err.println(e.toString());
        } catch(ToolError e) {
            System.err.println(e.getTypecheckingErrorMessage().toString());
        }
    }
}
