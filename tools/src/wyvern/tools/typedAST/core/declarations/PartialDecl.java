package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.ContParser.EnvironmentResolver;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;


public class PartialDecl implements TypedAST {

	private Pair<Environment, ContParser> pair;

	public PartialDecl(Pair<Environment, ContParser> pair) {
		this.pair = pair;
	}
	
	public Environment extend(Environment env) {
		return env.extend(pair.first);
	}
	
	public TypedAST getAST(final Environment env) {
		return pair.second.parse(new EnvironmentResolver() {

			@Override
			public Environment getEnv(TypedAST elem) {
				return env;
			}
			
		});
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileLocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type typecheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value evaluate(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineParser getLineParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		// TODO Auto-generated method stub
		return null;
	}
}