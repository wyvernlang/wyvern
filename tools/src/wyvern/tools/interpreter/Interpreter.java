package wyvern.tools.interpreter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.types.Environment;

public class Interpreter {
	public static void main(String args[]) {
		if (args.length != 1) {
			System.err.println("usage: wyvern file.wyv");
			return;
		}
		
		try {
			Reader reader = new FileReader(args[0]);
			
			Value resultValue = new Interpreter().interpret(reader);
			
			System.out.println(resultValue);
			
			if (resultValue instanceof IntegerConstant) {
				System.exit(((IntegerConstant)resultValue).getValue());
			}

		} catch (FileNotFoundException e) {
			System.err.println("Error: cannot open file " + args[0]);
			System.exit(-1);
		}
	}

	public Value interpret(Reader reader) {
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		
		typedAST.typecheck(env);
		
		return typedAST.evaluate(env);		
	}
}
