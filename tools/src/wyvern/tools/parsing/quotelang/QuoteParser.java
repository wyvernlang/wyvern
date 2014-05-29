package wyvern.tools.parsing.quotelang;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.extensions.ToastExpression;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTTransformer;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Unit;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.unit;

import java.io.IOException;

public class QuoteParser implements ExtParser {
	private static class ToastExecutor extends AbstractASTTransformer {
		private final Environment evalEnv;

		public ToastExecutor(Environment evalEnv) {
			this.evalEnv = evalEnv;
		}

		@Override
		public TypedAST transform(TypedAST input) {
			if (input instanceof ToastExpression) {
				Value result = input.evaluate(evalEnv);
				TypedAST iExn = Util.toJavaClass((Obj)result, TypedAST.class);
				TypedAST equivExn = Util.toJavaClass((Obj)iExn, TypedAST.class);
				return equivExn;
			}
			return super.defaultTransformation(input);
		}
	}

	@Override
	public TypedAST parse(ParseBuffer input) throws IOException, CopperParserException {
		TypedAST quoted = (TypedAST) new WyvernQuote().parse(input.getSrcString()+"\n");
		return new Application(new ExternalFunction(arrow(unit, Util.javaToWyvType(TypedAST.class)), (env, arg) -> {
			TypedAST adapted = new ToastExecutor(env).transform(quoted);
			return Util.toWyvObj(adapted);
		}), UnitVal.getInstance(FileLocation.UNKNOWN), FileLocation.UNKNOWN);
	}
}
