package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;

public class VerbParser implements LineParser {
	private class GreedyBinding extends TypeBinding {

		public GreedyBinding(String name, Type type) {
			super(name, type);
		}
	}

    @Override
    public TypedAST parse(TypedAST first, CompilationContext ctx) {
        TypedAST exprAST = ParseUtils.parseExpr(ctx);

        return Util.toWyvObj(exprAST);
    }
}
