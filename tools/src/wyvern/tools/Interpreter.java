package wyvern.tools;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.Optional;

import wyvern.stdlib.Globals;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Interpreter {

	public static void main(String[] args) throws Exception {
		String parser = "" +
				"module LazyParser\n" +
				"import java:wyvern.tools.parsing.ParseBuffer\n" +
				"import java:wyvern.tools.typedAST.interfaces.TypedAST\n" +
				"import java:wyvern.tools.util.LangUtil\n" +
				"class Parser\n" +
				"	class def create():Parser = new\n" +
				"	def parse(buf:ParseBuffer):TypedAST\n" +
				"		val spliced = LangUtil.splice(buf)\n" +
				"		~\n" +
				"			new\n" +
				"				def get():Int = $spliced\n";
		String supplier = "\n\n" +
				"module LazyTSL\n" +
				"import wyv:parser\n" +
				"import java:wyvern.tools.parsing.ExtParser\n" +
				"import java:wyvern.tools.parsing.HasParser\n" +
				"type Lazy\n" +
				"	def get():Int\n" +
				"	metadata:HasParser = new\n" +
				"		def getParser():ExtParser = LazyParser.Parser.create()\n";
		String client = "\n"+
				"import wyv:supplier\n" +
				"val x = 4\n" +
				"val test:LazyTSL.Lazy = ~\n" +
				"	4 + x\n" +
				"test.get()\n";
		WyvernResolver.clearFiles();
		//WyvernResolver.addFile("parser", parser);
		//WyvernResolver.addFile("supplier", supplier);

		if (args.length < 1) {
			System.err.println("usage: wyvern <filename>");
			System.exit(-1);
		}
		String filename = args[0];
		Reader reader = new FileReader(filename);
		//CharBuffer buf = CharBuffer.allocate(64000);
		//System.out.println(reader.read(buf));
		//buf.rewind();
		//String contents = buf.toString();
		//System.out.println(contents);
		//System.out.println(client);
		//System.out.println(client.equals(contents));
		//reader = new StringReader(contents);
		//reader = new StringReader(client);	// backup test
		TypedAST res = (TypedAST)new Wyvern().parse(reader, filename);
		//System.out.println(res);
		//System.err.println("parsed");
		Type result = res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		//System.err.println("checked");
		res = new DSLTransformer().transform(res);
		//System.err.println("transformed");
		Value finalV = res.evaluate(Globals.getStandardEnv());
		TreeWriter t = new TreeWriter();
		finalV.writeArgsToTree(t);
		System.out.println(t.getResult());	
	}

}
