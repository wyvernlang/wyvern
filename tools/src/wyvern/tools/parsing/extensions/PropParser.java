package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.PropDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class PropParser implements DeclParser {
	private PropParser() { }
	private static PropParser instance = new PropParser();
	public static PropParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		
		String varName = s.name;
		FileLocation line = s.getLocation();
		
		if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			String typeName = ParseUtils.parseSymbol(ctx).name;
			if (ParseUtils.checkFirst("?", ctx)) {
				// typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
				ParseUtils.parseSymbol("?", ctx); 
			}
			TypeBinding tb = ctx.second.lookupType(typeName);
			if (tb == null) {
				tb = new TypeBinding(typeName, Unit.getInstance()); // TODO: Implement proper Type for "type"!
			}
			return new PropDeclaration(varName, tb, line);
		} else {
			throw new RuntimeException("Error parsing prop expression : expected.");
		}
	}


	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		final String varName = s.name;
		FileLocation line = s.getLocation();
		TypeBinding tb;
		final Type type;
		if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			
			String typeName = ParseUtils.parseSymbol(ctx).name;
			if (ParseUtils.checkFirst("?", ctx)) {
				// typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
				ParseUtils.parseSymbol("?", ctx); 
			}
			tb = ctx.second.lookupType(typeName);
			if (tb == null) {
				tb = new TypeBinding(typeName, Unit.getInstance()); // TODO: Implement proper Type for "type"!
			}
			
		} else {
			throw new RuntimeException("Error parsing prop expression : expected.");
		}
		
		final TypeBinding finalBinding = tb;
		
		PropDeclaration parsed = new PropDeclaration(varName, tb, line);
		
		return new Pair<Environment, ContParser>(parsed.extend(Environment.getEmptyEnvironment()), new ContParser.EmptyWithAST(parsed));
		
	}
}