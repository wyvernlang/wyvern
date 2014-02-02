package wyvern.tools.prsr;

import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.std.*;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.TypeAsc;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import java.io.File;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class CombinatorTests {
	private Parser<IntegerConstant> integer = new Computed<>(new Terminal(Token.Kind.Number, null), num -> new IntegerConstant(num.value));
	private Parser<String> string = new Computed<>(new Terminal(Token.Kind.String, null), str -> str.text);
	private <T> Parser<T> indent(Parser<T> parser) {
		return new Terminal(Token.Kind.INDENT).toggleBreak()
					.concatRight(parser)
					.concatLeft(new Terminal(Token.Kind.DEDENT));
	}

	private <T> List<T> cons(T f, List<T> last) {
		LinkedList<T> out = new LinkedList<>();
		out.add(f);
		out.addAll(last);
		return out;
	}


	@Test
	public void simpleTuple() {
		Parser<List<TypedAST>> emptyProd = AbstractParser.getParser((stream) -> new LinkedList<>());
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
						Concat.<TupleObject, Token>getLeftConcat(innerParser, new Terminal(Token.Kind.RPAREN, null))));

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
		Parser<List<TypedAST>> emptyProd = AbstractParser.getParser((stream) -> new LinkedList<>());
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
						Concat.<TupleObject, Token>getLeftConcat(innerParser, new Terminal(Token.Kind.RPAREN, null))));

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
				new Computed<>(new Concat<Token, IntegerConstant>(new Terminal(null, "-"),integer),
						pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				new Computed<>(integer, t->t));
		factorRef.set(new Or<>(num, parens));

		parensRef.set(Concat.getRightConcat(new Terminal(null,"("), Concat.getLeftConcat(exp, new Terminal(null, ")"))));

		Parser<TypedAST> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), exp);
		TypedAST result = null;
		try {
			result = lineParser.parse(new LexStream("test", new StringReader("1+2*4*(6+ - 7)")));
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals("Invocation(IntegerConstant(1), \"+\", Invocation(IntegerConstant(2), \"*\", Invocation(IntegerConstant(4), \"*\", Invocation(IntegerConstant(6), \"+\", Invocation(IntegerConstant(0), \"-\", IntegerConstant(7))))))",result.toString());
	}

	@Test
	public void wExpr() {
		Supplier<Reference<Parser<TypedAST>>> supp = Reference::new;
		Reference<Parser<TypedAST>> expRef = supp.get(), termRef = supp.get(), factorRef = supp.get(),
				parensRef = supp.get(), varRef = supp.get(), tupleRef = supp.get(),
				invRef = supp.get();
		Function<Reference<Parser<TypedAST>>, Parser<TypedAST>> tform = Ref::new;
		Parser<TypedAST> exp = tform.apply(expRef), term = tform.apply(termRef),
				factor = tform.apply(factorRef), parens = tform.apply(parensRef),
				var = tform.apply(varRef), tuple = tform.apply(tupleRef),
				inv = tform.apply(invRef);

		Function<Pair<TypedAST,List<TypedAST>>,List<TypedAST>> concat = (pair) -> {
			List<TypedAST> nl = new LinkedList<>();
			nl.add(pair.first);
			nl.addAll(pair.second);
			return nl;
		};

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

		termRef.set(new Computed<>(new Concat<>(inv, new Opt<>(new Concat<>(mulOp, term))), toInv));

		Reference<Parser<Function<TypedAST, TypedAST>>> invPRef = new Reference<>();
		Parser<Function<TypedAST, TypedAST>> invP = new Ref<>(invPRef);

		invRef.set(new Computed<TypedAST, Pair<TypedAST, Function<TypedAST, TypedAST>>>(
				new Concat<TypedAST, Function<TypedAST, TypedAST>>(factor, invP),
				(tp)->tp.second.apply(tp.first)));

		invPRef.set(
				new Computed<Function<TypedAST, TypedAST>,Function<TypedAST, TypedAST>>(
						new Opt<Function<TypedAST, TypedAST>>(new Or<Function<TypedAST, TypedAST>>(new Computed<Function<TypedAST, TypedAST>, Pair<Token, Function<TypedAST, TypedAST>>>(
								new Concat<>(Concat.getRightConcat(new Terminal(null, "."), new Terminal(Token.Kind.Identifier, null)), invP),
								pair -> {
									if (pair != null)
										return (nt) -> pair.second.apply(new Invocation(nt, pair.first.text, null, pair.first.location));
									else
										return nt -> nt;
								}),
								new Computed<Function<TypedAST, TypedAST>, Pair<Pair<TypedAST, List<TypedAST>>, Function<TypedAST, TypedAST>>>((new Concat<>(Concat.<Token, Pair<TypedAST, List<TypedAST>>>getRightConcat(new Terminal(null,"("),
										Concat.getLeftConcat(new Opt<>(new Concat<>(inv, new Rep<>(
												Concat.getRightConcat(new Terminal(null, ","), inv)))), new Terminal(null, ")"))), invP)), pair -> {
									if (pair == null)
										return nt -> nt;
									List<TypedAST> comb;
									if (pair.first != null)
										comb = concat.apply(pair.first);
									else
										comb = new LinkedList<>();
									Pair<TypedAST, List<TypedAST>> ipair = pair.first;

									Function<TypedAST, TypedAST> constructor = (pair.second!=null)?pair.second:tast->tast;

									if (comb.size() == 0) {
										return tast -> constructor.apply(new Application(tast, UnitVal.getInstance(FileLocation.UNKNOWN), tast.getLocation()));
									} else if (comb.size() == 1) {
										return tast -> constructor.apply(new Application(tast, ipair.first, ipair.first.getLocation()));
									} else {
										return tast -> constructor.apply(new Application(tast, new TupleObject(comb.toArray(new TypedAST[comb.size()])), ipair.first.getLocation()));
									}
								}))), fn -> (fn == null)? tast -> tast : fn));


		Parser<TypedAST> num = new Or<>(
				new Computed<>(new Concat<Token, IntegerConstant>(new Terminal(null, "-"),integer),
						pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				new Computed<>(integer, t->t));
		factorRef.set(new Or<>(num, parens, var, tuple));

		varRef.set(new Computed<>(new Terminal(Token.Kind.Identifier,null), token -> new Variable(token.text, token.location)));

		parensRef.set(Concat.getRightConcat(new Terminal(null,"("), Concat.getLeftConcat(exp, new Terminal(null, ")"))));



		tupleRef.set(new Computed<>(Concat.getRightConcat(new Terminal(null, "("), Concat.
				<Pair<TypedAST, List<TypedAST>>, Token>getLeftConcat(
						new Concat<>(term, new Rep<>(
								Concat.getRightConcat(new Terminal(null, ","), exp)
						)
						), new Terminal(null, ")")
				)), pair -> new TupleObject(concat.apply(pair).toArray(new TypedAST[0]))));

		Parser<TypedAST> lineParser = Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null), exp);
		TypedAST result = null;
		try {
			result = lineParser.parse(new LexStream("test", new StringReader("1+2*(a.c.d,b.c()+2)*(hello + - 7)")));
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals("Invocation(IntegerConstant(1), \"+\", Invocation(IntegerConstant(2), \"*\", Invocation(TupleObject(Invocation(Invocation(Variable(\"a\"), \"c\", null), \"d\", null), Invocation(Application(Invocation(Variable(\"b\"), \"c\", null), UnitVal()), \"+\", IntegerConstant(2))), \"*\", Invocation(Variable(\"hello\"), \"+\", Invocation(IntegerConstant(0), \"-\", IntegerConstant(7))))))",result.toString());
	}

	@Test
	public void wDecl() {
		Supplier<Reference<Parser<TypedAST>>> supp = Reference::new;
		Reference<Parser<TypedAST>> expRef = supp.get(), termRef = supp.get(), factorRef = supp.get(),
				parensRef = supp.get(), varRef = supp.get(), tupleRef = supp.get(),
				invRef = supp.get();
		Function<Reference<Parser<TypedAST>>, Parser<TypedAST>> tform = Ref::new;
		Parser<TypedAST> exp = tform.apply(expRef), term = tform.apply(termRef),
				factor = tform.apply(factorRef), parens = tform.apply(parensRef),
				var = tform.apply(varRef), tuple = tform.apply(tupleRef),
				inv = tform.apply(invRef);

		Function<Pair<TypedAST,List<TypedAST>>,List<TypedAST>> concat = (pair) -> {
			List<TypedAST> nl = new LinkedList<>();
			nl.add(pair.first);
			nl.addAll(pair.second);
			return nl;
		};

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

		termRef.set(new Computed<>(new Concat<>(inv, new Opt<>(new Concat<>(mulOp, term))), toInv));

		Reference<Parser<Function<TypedAST, TypedAST>>> invPRef = new Reference<>();
		Parser<Function<TypedAST, TypedAST>> invP = new Ref<>(invPRef);


		invRef.set(factor.concat(invP).compute(tp -> tp.second.apply(tp.first)));

		invPRef.set(
				new Computed<Function<TypedAST, TypedAST>,Function<TypedAST, TypedAST>>(
						new Opt<Function<TypedAST, TypedAST>>(new Or<Function<TypedAST, TypedAST>>(
								new Terminal(null, ".").concatRight(new Terminal(Token.Kind.Identifier, null)).concat(invP).<Function<TypedAST,TypedAST>>compute(
										pair -> {
											if (pair != null)
												return (nt) -> pair.second.apply(new Invocation(nt, pair.first.text, null, pair.first.location));
											else
												return nt -> nt;
										}),
								new Terminal(null,"(").concatRight(new Opt<Pair<TypedAST, List<TypedAST>>>(
										inv.<List<TypedAST>>concat(new Rep<>(new Terminal(null, ",").concatRight(inv))))).concatLeft(new Terminal(null, ")"))
										.concat(invP).<Function<TypedAST,TypedAST>>compute(pair -> {
									if (pair == null)
										return nt -> nt;
									List<TypedAST> comb;
									if (pair.first != null)
										comb = concat.apply(pair.first);
									else
										comb = new LinkedList<>();
									Pair<TypedAST, List<TypedAST>> ipair = pair.first;

									Function<TypedAST, TypedAST> constructor = (pair.second!=null)?pair.second:tast->tast;

									if (comb.size() == 0) {
										return tast -> constructor.apply(new Application(tast, UnitVal.getInstance(FileLocation.UNKNOWN), tast.getLocation()));
									} else if (comb.size() == 1) {
										return tast -> constructor.apply(new Application(tast, ipair.first, ipair.first.getLocation()));
									} else {
										return tast -> constructor.apply(new Application(tast, new TupleObject(comb.toArray(new TypedAST[comb.size()])), ipair.first.getLocation()));
									}
								}))), fn -> (fn == null)? tast -> tast : fn));




		Parser<TypedAST> num = new Or<>(
				new Terminal(null, "-").concat(integer).<TypedAST>compute(
						pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				integer.<TypedAST>compute(t->t));

		Parser<TypedAST> str = string.compute(stri -> new StringConstant(stri));
		factorRef.set(new Or<>(num, str, parens, var, tuple));

		varRef.set(new Terminal(Token.Kind.Identifier, null).compute(token -> new Variable(token.text, token.location)));

		parensRef.set(new Terminal("(").concatRight(exp).concatLeft(new Terminal(")")));

		tupleRef.set(new Terminal("(")
				.concatRight(exp.concat(new Terminal(",").concatRight(exp).rep()))
				.concatLeft(new Terminal(")"))
				.<TypedAST>compute(
						pair -> new TupleObject(concat.apply(pair).toArray(new TypedAST[0]))));



		Reference<Parser<BiFunction<Environment, Type,Type>>> typePParserRef = new Reference<>();

		Parser<BiFunction<Environment, Type,Type>> typePParser = new Ref<>(typePParserRef);
		typePParserRef.set(new Terminal(".").concatRight(new Terminal(Token.Kind.Identifier)).concat(typePParser).opt().compute(
				pair -> {
					if (pair == null)
						return (env, otype) -> otype;
					if (pair.second == null) {
						return (env, otype) -> ((OperatableType) otype).checkOperator(new Invocation(null, pair.first.text, null, FileLocation.UNKNOWN), env);
					}
					return (env, otype) -> pair.second.apply(env, ((OperatableType) otype).checkOperator(new Invocation(null, pair.first.text, null, FileLocation.UNKNOWN), env));
				}
		));
		Parser<TypeAsc> type = new Terminal(Token.Kind.Identifier).concat(typePParser).compute(pair -> env -> {
			Type typei = env.lookupType(pair.first.text).getUse();
			if (pair.second != null) {
				return pair.second.apply(env, typei);
			} else {
				return typei;
			}
		});


		Parser<TypedAST> valParser = new Terminal(null, "val")
				.concatRight(new Terminal(Token.Kind.Identifier, null))
				.concat(new Terminal(null, ":").concatRight(type).opt())
				.concatLeft(new Terminal(null, "="))
				.concat(exp.opt())
				.<TypedAST>compute(
						pairs -> new ValDeclaration(pairs.first.first.text, pairs.first.second, pairs.second, pairs.first.first.location));

		Parser<Pair<Token, TypeAsc>> arg = new Terminal(Token.Kind.Identifier).concatLeft(new Terminal(":")).concat(type);
		Parser<List<Pair<Token, TypeAsc>>> args = (new Terminal("(")
				.concatRight(arg.concat(new Terminal(",").concatRight(arg).rep()).opt()).concatLeft(new Terminal(")"))).compute(pair -> {
			if (pair == null) {
				return new LinkedList<>();
			} else {
				LinkedList<Pair<Token,TypeAsc>> output = new LinkedList<>();
				output.add(pair.first);
				output.addAll(pair.second);
				return output;
			}
		});

		Reference<Parser<TypedAST>> lineParserRef = new Reference<>(), defLineParserRef = new Reference<>();
		Parser<TypedAST> lineParser = new Ref<>(lineParserRef), defLineParser = new Ref<>(defLineParserRef);


		Parser<TypedAST> defParser = new Terminal("def")
				.concatRight(new Terminal(Token.Kind.Identifier))
				.concat(args)
				.concat(new Terminal(null, ":").concatRight(type))
				.concat(new Or<TypedAST>(new Terminal("=").concatRight(exp), indent(defLineParser)))
				.compute(pairs ->
						new DefDeclaration(pairs.first.first.first.text, pairs.first.second,
								pairs.first.first.second
										.stream().map(pair -> new Pair<String, TypeAsc>(pair.first.text, pair.second))
										.collect(Collectors.<Pair<String, TypeAsc>>toList()),
								pairs.second, false, pairs.first.first.first.location));

		Parser<TypedAST> ds = new Or<>(defParser).concatLeft(new Terminal(Token.Kind.NEWLINE).opt()).rep1().compute(decls -> new DeclSequence(decls.stream().map(decl -> (TypedAST) decl).iterator()));


		Parser<TypedAST> tlp = new Or<>(valParser, ds, exp).toggleBreak();

		defLineParserRef.set((tlp.concat(new Terminal(Token.Kind.NEWLINE).concatRight(tlp).rep()).compute(pair -> (TypedAST)new Sequence(cons(pair.first, pair.second)))).opt());

		lineParserRef.set(new Computed<TypedAST, List<TypedAST>>(new Rep<>(Concat.getRightConcat(new Terminal(Token.Kind.NEWLINE, null).opt(), tlp)), lines -> {
			if (lines.size() == 0) {
				return UnitVal.getInstance(FileLocation.UNKNOWN);
			} else if (lines.size() == 1) {
				return lines.get(0);
			} else {
				return new Sequence(lines);
			}
		}));


		TypedAST result = null;
		try {
			result = lineParser.parse(new LexStream("test", new StringReader("val t : Int = 100\n" +
					"def x(t : Int) : Int = y(t-1)\n" +
					"def y(i:Int) : Int\n" +
					"\ti*x(i)\n" +
					"1+2\n" +
					"3*4\n" +
					"y()")));
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
		Assert.assertEquals("[ValDeclaration(\"t\", IntegerConstant(100)), [DefDeclaration(\"x\", Invocation(IntegerConstant(2), \"+\", Variable(\"t\"))), DefDeclaration(\"y\", Sequence())], Invocation(IntegerConstant(1), \"+\", IntegerConstant(2)), Invocation(IntegerConstant(3), \"*\", IntegerConstant(4)), Application(Variable(\"y\"), UnitVal())]",result.toString());

		result.typecheck(Globals.getStandardEnv());
		TypedAST res = result.evaluate(Globals.getStandardEnv());
	}

}
