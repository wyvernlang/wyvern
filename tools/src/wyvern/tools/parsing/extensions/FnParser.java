package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "fn x : T => e"
 * 
 * Could specify as:   "fn" symbol ":" type "=>" exp
 */

public class FnParser implements LineParser {
	private FnParser() { }
	private static FnParser instance = new FnParser();
	public static FnParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String varName = ParseUtils.parseSymbol(ctx).name;
		ParseUtils.parseSymbol(":", ctx);
		Type type = ParseUtils.parseType(ctx);
		parseSymbol("=>", ctx);
		NameBinding binding = new NameBindingImpl(varName, type);
		ctx.second = ctx.second.extend(binding);
		List<NameBinding> bindings = new ArrayList<NameBinding>();
		bindings.add(binding);
		TypedAST exp = ParseUtils.parseExpr(ctx);
				
		return new Fn(bindings, exp);
	}
}
