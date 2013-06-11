package wyvern.DSL.deploy.parsers.architecture;

import wyvern.DSL.deploy.typedAST.architecture.Endpoint;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class EndpointParser implements DeclParser {

	private final ArchitectureParser arch;

	public EndpointParser(ArchitectureParser architectureParser) {
		arch = architectureParser;
	}

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		String name = ParseUtils.parseSymbol(ctx).name;
		Endpoint endpoint = new Endpoint(name);
		arch.addEndpoint(endpoint);
		return endpoint;
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		Declaration elem = (Declaration) parse(first, ctx);
		return new Pair<Environment, ContParser>(elem.extend(Environment.getEmptyEnvironment()), new ContParser.EmptyWithAST(elem));
	}
}
