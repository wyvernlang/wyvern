package wyvern.targets.JavaScript;

import static wyvern.tools.types.TypeUtils.arrow;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import wyvern.DSL.html.Html;
import wyvern.stdlib.Globals;
import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.targets.JavaScript.types.JSObjectType;
import wyvern.targets.JavaScript.visitors.JSCodegenVisitor;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;

public class JSWyvCompiler {
	private static TypedAST doCompile(Reader reader, String filename, Environment ienv) {
		RawAST parsedResult = Phase1Parser.parse(filename, reader);
		Environment env = Globals.getStandardEnv();
		env = env.extend(new ValueBinding("require", new JSFunction(arrow(Str.getInstance(),JSObjectType.getInstance()),"require")));
		env = env.extend(new TypeBinding("JSObject", JSObjectType.getInstance()));
		env = env.extend(ienv);
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String ifile = args[0], ofile = args[1];
		TypedAST ast = doCompile(new FileReader(ifile),ifile,Html.extend(Environment.getEmptyEnvironment()));
		
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)ast).accept(visitor);
		PrintWriter writer = new PrintWriter(ofile);
		writer.print(visitor.getCode());
		writer.close();
	}

}
