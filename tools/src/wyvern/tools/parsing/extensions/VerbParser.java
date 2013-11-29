package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.TreeWriter;

import java.util.LinkedList;

public class VerbParser implements LineParser {
	private class QuoteClosure implements TypedAST {
		private Application arg;
		private TypedAST inner;
		public QuoteClosure(TypedAST inner) {
			this.inner = inner;
		}
		@Override
		public Type getType() {
			return inner.getType();
		}

		@Override
		public Type typecheck(Environment env) {
			inner.typecheck(env);
			Fn fn = new Fn(new LinkedList<NameBinding>(), inner);
			fn.typecheck(env);
			arg = new Application(new Closure(fn, env), UnitVal.getInstance(inner.getLocation()), inner.getLocation());
			arg.typecheck(env);
			return Util.javaToWyvType(TypedAST.class);
		}

		@Override
		public Value evaluate(Environment env) {
			return Util.toWyvObj(arg);
		}

		@Override
		public LineParser getLineParser() {
			return null;
		}

		@Override
		public LineSequenceParser getLineSequenceParser() {
			return null;
		}

		@Override
		public FileLocation getLocation() {
			return inner.getLocation();
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {

		}
	}

    @Override
    public TypedAST parse(TypedAST first, CompilationContext ctx) {
        TypedAST exprAST = ParseUtils.parseExpr(ctx);
        return new QuoteClosure(exprAST);
    }
}
