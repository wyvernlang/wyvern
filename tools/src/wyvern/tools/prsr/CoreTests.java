package wyvern.tools.prsr;

import org.junit.Assert;
import org.junit.Test;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.io.StringReader;
import java.util.Arrays;

/**
 * Created by Ben Chung on 1/20/14.
 */
public class CoreTests {
	ParserStage add = (str, self, next) -> {
		TypedAST lhs = next.apply(str);
		String symbol = str.peek().text;
		if (!(symbol.equals("+") || symbol.equals("-")))
			return lhs;
		Token loc = str.next();
		TypedAST rhs = self.parse(str, self, next);
		return new Invocation(lhs, symbol, rhs, loc.location);
	};
	ParserStage mult = (str, self, next) -> {
		TypedAST lhs = next.apply(str);
		String symbol = str.peek().text;
		if (!(symbol.equals("*") || symbol.equals("/")))
			return lhs;
		Token loc = str.next();
		TypedAST rhs = self.parse(str, self, next);
		return new Invocation(lhs, symbol, rhs, loc.location);
	};
	ParserStage atom = (str, self, next) -> {
		if (str.peek().kind == Token.Kind.Number){
			return new IntegerConstant(str.next().value);
		}
		if (str.peek().kind == Token.Kind.LPAREN) {
			Token start = str.next();
			TypedAST eval = next.back(str, add);
			if (str.next().kind != Token.Kind.RPAREN)
				ToolError.reportError(ErrorMessage.MISMATCHED_PARENTHESES, start);
			return eval;
		}
		return next.apply(str);
	};
	ParserStage neg = (str, self, handle) -> {
		Token next = str.next();
		if (!next.text.equals("-"))
			ToolError.reportError(ErrorMessage.MISMATCHED_PARENTHESES, next);
		return new Invocation(new IntegerConstant(0), "-", handle.back(str, atom), next.location);
	};

	@Test
	public void calc1() {
		ILexStream src = new LexStream("test", new StringReader("2+2*3"));
		src.next();
		TypedAST result = new RecursiveDescentParser(Arrays.asList(add, mult, atom, neg)).parse(src);
		Assert.assertEquals("Invocation(IntegerConstant(2), \"+\", Invocation(IntegerConstant(2), \"*\", IntegerConstant(3)))", result.toString());
	}

	@Test
	public void calc2() {
		ILexStream src = new LexStream("test", new StringReader("2+(2+5)*3"));
		src.next();
		TypedAST result = new RecursiveDescentParser(Arrays.asList(add, mult, atom, neg)).parse(src);
		Assert.assertEquals("Invocation(IntegerConstant(2), \"+\", Invocation(Invocation(IntegerConstant(2), \"+\", IntegerConstant(5)), \"*\", IntegerConstant(3)))", result.toString());
	}

	@Test
	public void calc3() {
		ILexStream src = new LexStream("test", new StringReader("2+(2+5)* -3"));
		src.next();
		TypedAST result = new RecursiveDescentParser(Arrays.asList(add, mult, atom, neg)).parse(src);
		Assert.assertEquals("Invocation(IntegerConstant(2), \"+\", Invocation(Invocation(IntegerConstant(2), \"+\", IntegerConstant(5)), \"*\", Invocation(IntegerConstant(0), \"-\", IntegerConstant(3))))", result.toString());
	}
}
