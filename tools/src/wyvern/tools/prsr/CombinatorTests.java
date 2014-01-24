package wyvern.tools.prsr;

import org.junit.Assert;
import org.junit.Test;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.std.*;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class CombinatorTests {
	private Parser<IntegerConstant> integer = new Computed<>(new Terminal(Token.Kind.Number, null), num -> new IntegerConstant(num.value));
	private Parser<String> string = new Computed<>(new Terminal(Token.Kind.String, null), str -> str.text);


	@Test
	public void simpleTuple() {
		Parser<List<TypedAST>> emptyProd = (stream) -> new LinkedList<>();
		Function<Pair<TypedAST,List<TypedAST>>,List<TypedAST>> concat = (pair) -> {
			List<TypedAST> nl = new LinkedList<>();
			nl.add(pair.first);
			nl.addAll(pair.second);
			return nl;
		};
		Reference<Parser<TupleObject>> tupleParser = new Reference<>();
		Parser<TypedAST> values = new Or<>(
				new Computed<TypedAST, TupleObject>(new Ref<>(tupleParser), tu->tu),
				new Computed<>(integer, t->t));
		Parser<TupleObject> innerParser = new Computed<TupleObject, List<TypedAST>>(
				new Or<>(new Computed<>(
						new Concat<TypedAST, List<TypedAST>>(values,
								new Rep<>(Concat.<Token, TypedAST>getRightConcat(new Terminal(null, ","), values))),
						concat),
						emptyProd),
				list -> new TupleObject(list.toArray(new TypedAST[0])));
		tupleParser.set(Concat.
				getRightConcat(new Terminal(Token.Kind.LPAREN, null),
						Concat.<TupleObject, Token>getLefConcat(innerParser, new Terminal(Token.Kind.RPAREN, null))));

		Parser<TupleObject> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), new Ref<TupleObject>(tupleParser));

		TupleObject res = null;
		try {
			res = lineParser.parse(new LexStream("test", new StringReader("(1,2,3)")));
		} catch (ParserException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("TupleObject(IntegerConstant(1), IntegerConstant(2), IntegerConstant(3))",res.toString());
	}

	@Test
	public void simpleTuple2() {
		Parser<List<TypedAST>> emptyProd = (stream) -> new LinkedList<>();
		Function<Pair<TypedAST,List<TypedAST>>,List<TypedAST>> concat = (pair) -> {
			List<TypedAST> nl = new LinkedList<>();
			nl.add(pair.first);
			nl.addAll(pair.second);
			return nl;
		};
		Reference<Parser<TupleObject>> tupleParser = new Reference<>();
		Parser<TypedAST> values = new Or<>(
				new Computed<>(new Ref<TupleObject>(tupleParser), tu-> tu),
				new Computed<TypedAST, IntegerConstant>(integer, t->t));
		Parser<TupleObject> innerParser = new Computed<TupleObject, List<TypedAST>>(
				new Or<>(
						new Computed<>(
							new Concat<TypedAST, List<TypedAST>>(values,
								new Rep<>(Concat.<Token, TypedAST>getRightConcat(new Terminal(null, ","), values))),
							concat),
						emptyProd),
				list -> new TupleObject(list.toArray(new TypedAST[0])));
		tupleParser.set(Concat.
				getRightConcat(new Terminal(Token.Kind.LPAREN, null),
						Concat.<TupleObject, Token>getLefConcat(innerParser, new Terminal(Token.Kind.RPAREN, null))));

		Parser<TupleObject> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), new Ref<TupleObject>(tupleParser));

		TupleObject res = null;
		try {
			res = lineParser.parse(new LexStream("test", new StringReader("((1,(7,85,9),3,(4,5,6)),2,3)")));
		} catch (ParserException e) {
			e.printStackTrace();
		}
		Assert.assertEquals("TupleObject(TupleObject(IntegerConstant(1), TupleObject(IntegerConstant(7), IntegerConstant(85), IntegerConstant(9)), IntegerConstant(3), TupleObject(IntegerConstant(4), IntegerConstant(5), IntegerConstant(6))), IntegerConstant(2), IntegerConstant(3))",res.toString());
	}

	@Test
	public void calculator() {
		Supplier<Reference<Parser<TypedAST>>> supp = Reference::new;
		Reference<Parser<TypedAST>> expRef = supp.get(), termRef = supp.get(), factorRef = supp.get(), parensRef = supp.get();
		Function<Reference<Parser<TypedAST>>, Parser<TypedAST>> tform = Ref::new;
		Parser<TypedAST> exp = tform.apply(expRef), term = tform.apply(termRef), factor = tform.apply(factorRef), parens = tform.apply(parensRef);

		Parser<Token> addOp = new Or<>(new Terminal(null, "+"), new Terminal(null, "-"));
		Parser<Token> mulOp = new Or<>(new Terminal(null, "*"), new Terminal(null, "/"));

		Function<Pair<TypedAST, Pair<Token, TypedAST>>, TypedAST> toInv = pair -> {
			if (pair.second != null) {
				return new Invocation(pair.first, pair.second.first.text, pair.second.second, pair.first.getLocation());
			} else {
				return pair.first;
			}
		};
		expRef.set(new Computed<>(new Concat<>(term, new Opt<>(new Concat<>(addOp, exp))), toInv));

		termRef.set(new Computed<>(new Concat<>(factor, new Opt<>(new Concat<>(mulOp, term))), toInv));
		Parser<TypedAST> num = new Or<>(
				new Computed<>(new Concat<>(new Terminal(null, "-"),integer), pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				new Computed<>(integer, t->t));
		factorRef.set(new Or<>(num, parens));

		parensRef.set(Concat.getRightConcat(new Terminal(null,"("), Concat.getLefConcat(exp, new Terminal(null, ")"))));

		Parser<TypedAST> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), exp);
		TypedAST result = null;
		try {
			result = lineParser.parse(new LexStream("test", new StringReader("1+2*4*(6+ - 7)")));
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals("Invocation(IntegerConstant(1), \"+\", Invocation(IntegerConstant(2), \"*\", Invocation(IntegerConstant(4), \"*\", Invocation(IntegerConstant(6), \"+\", Invocation(IntegerConstant(0), \"-\", IntegerConstant(7))))))",result.toString());
	}
}
