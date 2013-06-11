package wyvern.DSL.deploy.parsers.architecture.connections.properties;

import wyvern.DSL.deploy.parsers.architecture.connections.ConnectionPropertyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class ViaParser extends ConnectionPropertyParser {
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
