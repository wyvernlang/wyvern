package wyvern.tools.parsing.extensions;

import java.util.LinkedList;
import java.util.List;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ObjectType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "class x [indent] decls"
 */

public class ClassParser implements LineParser {
	private ClassParser() { }
	private static ClassParser instance = new ClassParser();
	public static ClassParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		if (ParseUtils.checkFirst("meth", ctx)) {
			ParseUtils.parseSymbol(ctx);
			return MethParser.getInstance().parse(first, ctx, Unit.getInstance());
		}
		
		String clsName = ParseUtils.parseSymbol(ctx).name;
		
		String implementsName = "";
		if (ParseUtils.checkFirst("implements", ctx)) {
			ParseUtils.parseSymbol("implements", ctx);
			implementsName = ParseUtils.parseSymbol(ctx).name;
		}

		TypedAST declAST = null;
		if (ctx.first == null) {
			// Empty body in the class declaration is OK.
		} else {
			// Process body.
			LineSequence lines = ParseUtils.extractLines(ctx);
			if (ctx.first != null)
				throw new RuntimeException("parse error");
		
			declAST = lines.accept(CoreParser.getInstance(), ctx.second);
			if (!(declAST instanceof Declaration))
				throw new RuntimeException("parse error");
		}

		return new ClassDeclaration(clsName, implementsName, (Declaration) declAST);
	}
}
