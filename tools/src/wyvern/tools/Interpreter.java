package wyvern.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.util.TreeWriter;

public class Interpreter {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("usage: wyvern <filename>");
			System.exit(-1);
		}

		String filename = args[0];
		File file = new File(filename);
		if (!file.canRead()) {
			System.err.println("Cannot read file " + filename);
			System.exit(-1);
		}
		
		try {
			Reader reader = new FileReader(file);
			TypedAST res = (TypedAST) new Wyvern().parse(reader, filename);
			res.typecheck(Globals.getStandardEnv(), Optional.empty());
			res = new DSLTransformer().transform(res);
			Value finalV = res.evaluate(Globals.getStandardEnv());
			TreeWriter t = new TreeWriter();
			finalV.writeArgsToTree(t);
			System.out.println(t.getResult());
		} catch (IOException e) {
			System.err.println("Error reading file " + filename);
		} catch (CopperParserException e) {
			System.err.print("Parsing error: ");
			e.printStackTrace();
		}
	}

}
