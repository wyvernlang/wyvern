package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.TypeDeclaration;
import wyvern.tools.typedAST.extensions.TypeInstance;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class ValParser implements LineParser {
	private ValParser() { }
	private static ValParser instance = new ValParser();
	public static ValParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String varName = ParseUtils.parseSymbol(ctx).name;
		
		if (ParseUtils.checkFirst("=", ctx)) {
			parseSymbol("=", ctx);
			TypedAST exp = ParseUtils.parseExpr(ctx);
			return new ValDeclaration(varName, exp, ctx.second);		
		} else if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			String typeName = ParseUtils.parseSymbol(ctx).name; // This can contain several type parameters! E.g. T Link?
			if (ctx.first != null) { // Not EOL? Hence, another type name coming?
				typeName = typeName + " " +
							ParseUtils.parseSymbol(ctx).name; // FIXME: Just hack for now until parameterised types done.
				if (ParseUtils.checkFirst("?", ctx)) {
					typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
					ParseUtils.parseSymbol("?", ctx); 
				}
			}
			TypeBinding tb = new TypeBinding(typeName, Unit.getInstance()); // TODO: Implement proper Type for "type"!
			return new ValDeclaration(varName, new TypeInstance(tb), ctx.second);
		} else {
			throw new RuntimeException("Error parsing val expression, either : or = expected.");
		}
	}
}