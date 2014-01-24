package wyvern.tools.prsr;

import org.junit.Test;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.std.*;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class CombinatorTests {
	private Parser<IntegerConstant> integer = new Computed<>(new Terminal(Token.Kind.Number, null), num -> new IntegerConstant(num.value));
	private Parser<String> string = new Computed<>(new Terminal(Token.Kind.String, null), str -> str.text);


	@Test
	public void simpleGrammar() {
		Parser<List<IntegerConstant>> emptyProd = (stream) -> new LinkedList<>();
		Parser<TupleObject> innerParser = new Computed<TupleObject, List<IntegerConstant>>(
				new Or<>(
						new Computed<>(
								new Concat<IntegerConstant, List<IntegerConstant>>(integer,
										new Rep<>(Concat.<Token, IntegerConstant>getRightConcat(new Terminal(null, ","), integer))),
								(pair) -> {
									List<IntegerConstant> nl = new LinkedList<>();
									nl.add(pair.first);
									nl.addAll(pair.second);
									return nl;
								}),
						emptyProd),
				list -> new TupleObject(list.toArray(new TypedAST[0])));
		Parser<TupleObject> tupleParser = Concat.
				getRightConcat(new Terminal(Token.Kind.LPAREN, null),
						Concat.<TupleObject, Token>getLefConcat(innerParser, new Terminal(Token.Kind.RPAREN, null)));

		Parser<TupleObject> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), tupleParser);

		TupleObject res = null;		try {
			res = lineParser.parse(new LexStream("test", new StringReader("(1,2,3)")));
		} catch (ParserException e) {
			e.printStackTrace();
		}
		int x = 2;
	}
}
