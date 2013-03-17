package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.TypeInstance;
import wyvern.tools.typedAST.extensions.declarations.VarDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class VarParser implements LineParser {
	private VarParser() { }
	private static VarParser instance = new VarParser();
	public static VarParser getInstance() { return instance; }
	
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String varName = ParseUtils.parseSymbol(ctx).name;
		
		if (ParseUtils.checkFirst("=", ctx)) {
			parseSymbol("=", ctx);
			TypedAST exp = ParseUtils.parseExpr(ctx);
			return new VarDeclaration(varName, exp, ctx.second);		
		} else if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			String typeName = ParseUtils.parseSymbol(ctx).name; // FIXME: This can contain several type parameters! E.g. T Link?

			// if (ctx.first != null) { // Not EOL? Hence, another type name coming?
			//	typeName = // typeName + " " + FIXME: Ignoring type params!
			//			   ParseUtils.parseSymbol(ctx).name; // FIXME: Just hack for now until parameterised types done.
			// }
			if (ParseUtils.checkFirst("?", ctx)) {
				// typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
				// FIXME: ? ignored
				ParseUtils.parseSymbol("?", ctx); 
			}
			// System.out.println("Creating type binding for type: " + typeName);
			// System.out.println("Have I seen " + typeName + "? " + ctx.second.lookupType(typeName).getType().toString());
			
			TypeBinding tb = ctx.second.lookupType(typeName);
			if (tb == null) {
				tb = new TypeBinding(typeName, Unit.getInstance()); // TODO: Implement proper Type for "type"!
			}
			return new VarDeclaration(varName, new TypeInstance(tb), ctx.second);
		} else {
			throw new RuntimeException("Error parsing val expression, either : or = expected.");
		}
	}
}