package wyvern.tools;

// TODO make this an explicit import
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.GenContext;

public class Interpreter {

	public static void main(String[] args) {
		
        System.out.println("Running the interpreter.");
        if(args.length != 1) {
			System.err.println("usage: wyvern <filename>");
			System.exit(1);
        }

        String filename = args[0];
        Path filepath = Paths.get(filename);
        if(!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
			System.exit(-1);
        }

        // TODO include the try/catch block for the parse error.
        String source = TestUtil.readFile(filepath.toAbsolutePath().toString());
        try {
            ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
            Expression program = ast.generateIL(GenContext.empty());
            TypeContext ctx = TypeContext.empty();
            program.typeCheck(ctx);
            Value v = program.interpret(EvalContext.empty());
            System.out.println(v.toString());
        } catch(ParseException e) {
            System.err.println("Failed to parse file.");
            e.printStackTrace();
        }
    }
}
