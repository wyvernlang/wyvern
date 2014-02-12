package wyvern.tools.prsr;

import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.std.*;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.DSLInf.DSLDummy;
import wyvern.tools.typedAST.extensions.DSLInf.UnparsedDSL;
import wyvern.tools.typedAST.extensions.TypeAsc;
import wyvern.tools.typedAST.extensions.UnparsedType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
		Parser<TypedAST> values = new Or<TypedAST>(
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

		Function<Pair<TypedAST, Optional<Pair<Token, TypedAST>>>, TypedAST> toInv = pair ->
				pair.second
						.<TypedAST>map(eval -> new Invocation(pair.first, eval.first.text, eval.second, pair.first.getLocation()))
						.orElse(pair.first);

		expRef.set(term.concat(addOp.concat(exp).opt()).compute(toInv));
		termRef.set(factor.concat(mulOp.concat(term).opt()).compute(toInv));

		Parser<TypedAST> num = new Or<TypedAST>(
				new Computed<>(new Concat<Token, IntegerConstant>(new Terminal(null, "-"),integer),
						pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				integer.compute(t->(TypedAST)t));
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


		Function<Pair<TypedAST, Optional<Pair<Token, TypedAST>>>, TypedAST> toInv = pair ->
				pair.second
						.<TypedAST>map(eval -> new Invocation(pair.first, eval.first.text, eval.second, pair.first.getLocation()))
						.orElse(pair.first);
		expRef.set(term.concat(addOp.concat(exp).opt()).compute(toInv));
		termRef.set(inv.concat(mulOp.concat(term).opt()).compute(toInv));

		Reference<Parser<Function<TypedAST, TypedAST>>> invPRef = new Reference<>();
		Parser<Function<TypedAST, TypedAST>> invP = new Ref<>(invPRef);

		invRef.set(new Computed<TypedAST, Pair<TypedAST, Function<TypedAST, TypedAST>>>(
				new Concat<TypedAST, Function<TypedAST, TypedAST>>(factor, invP),
				(tp)->tp.second.apply(tp.first)));

		invPRef.set(
				new Or<>(
						new Terminal(null, ".").concatRight(new Terminal(Token.Kind.Identifier, null)).concat(invP).<Function<TypedAST,TypedAST>>compute(
								pair -> {
									if (pair != null)
										return (nt) -> pair.second.apply(new Invocation(nt, pair.first.text, null, pair.first.location));
									else
										return nt -> nt;
								}),
						new Terminal(null,"(").concatRight(inv.<List<TypedAST>>concat(new Rep<>(new Terminal(null, ",").concatRight(inv))).opt()).concatLeft(new Terminal(null, ")"))
								.concat(invP).<Function<TypedAST,TypedAST>>compute(pair -> {
							if (pair == null)
								return nt -> nt;
							List<TypedAST> comb = pair.first.<List<TypedAST>>map(concat::apply).orElse(new LinkedList<>());

							Function<TypedAST, TypedAST> constructor = (pair.second!=null)?pair.second:tast->tast;

							return tast -> constructor.apply(new Application(tast, TupleObject.fromList(comb, tast.getLocation()), tast.getLocation()));
						})).opt().compute(opt -> opt.orElse(el -> el)));


		Parser<TypedAST> num = new Or<TypedAST>(
				new Concat<Token, IntegerConstant>(new Terminal(null, "-"),integer).compute(
						pair->new Invocation(new IntegerConstant(0), "-", pair.second, pair.first.location)),
				integer.compute(t->(TypedAST)t));
		factorRef.set(new Or<>(num, parens, var, tuple).toggleBreak());

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

		Function<Pair<TypedAST, Optional<Pair<Token, TypedAST>>>, TypedAST> toInv = pair ->
			pair.second
					.<TypedAST>map(eval -> new Invocation(pair.first, eval.first.text, eval.second, pair.first.getLocation()))
					.orElse(pair.first);
		expRef.set(term.concat(addOp.concat(exp).opt()).compute(toInv));
		termRef.set(inv.concat(mulOp.concat(term).opt()).compute(toInv));

		Reference<Parser<Function<TypedAST, TypedAST>>> invPRef = new Reference<>();
		Parser<Function<TypedAST, TypedAST>> invP = new Ref<>(invPRef);


		invRef.set(factor.concat(invP).compute(tp -> tp.second.apply(tp.first)));

		invPRef.set(
						new Or<>(
								new Terminal(null, ".").concatRight(new Terminal(Token.Kind.Identifier, null)).concat(invP).<Function<TypedAST,TypedAST>>compute(
										pair -> {
											if (pair != null)
												return (nt) -> pair.second.apply(new Invocation(nt, pair.first.text, null, pair.first.location));
											else
												return nt -> nt;
										}),
								new Terminal(null,"(").concatRight(inv.<List<TypedAST>>concat(new Rep<>(new Terminal(null, ",").concatRight(inv))).opt()).concatLeft(new Terminal(null, ")"))
										.concat(invP).<Function<TypedAST,TypedAST>>compute(pair -> {
									if (pair == null)
										return nt -> nt;
									List<TypedAST> comb = pair.first.<List<TypedAST>>map(concat::apply).orElse(new LinkedList<>());

									Function<TypedAST, TypedAST> constructor = (pair.second!=null)?pair.second:tast->tast;

									return tast -> constructor.apply(new Application(tast, TupleObject.fromList(comb, tast.getLocation()), tast.getLocation()));
								})).opt().compute(opt -> opt.orElse(el -> el)));




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
				pair ->
						pair.map(ipair -> (BiFunction<Environment, Type, Type>) (env, otype) ->
								ipair.second.apply(env, ((OperatableType) otype).checkOperator(new Invocation(null, ipair.first.text, null, FileLocation.UNKNOWN), env)))
								.orElse((env, otype) -> otype)
		));
		Parser<UnparsedType> type = new Terminal(Token.Kind.Identifier).concat(typePParser).compute(pair -> env -> {
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
						pairs -> new ValDeclaration(pairs.first.first.text, pairs.first.second.orElse(null), pairs.second.orElse(null), pairs.first.first.location));

		Parser<Pair<Token, UnparsedType>> arg = new Terminal(Token.Kind.Identifier).concatLeft(new Terminal(":")).concat(type);

		Parser<List<Pair<Token, UnparsedType>>> args = (new Terminal("(")
				.concatRight(arg.concat(new Terminal(",").concatRight(arg).rep()).opt()).concatLeft(new Terminal(")"))).compute(pair -> pair.map(ipair -> {
					LinkedList<Pair<Token,UnparsedType>> output = new LinkedList<>();
					output.add(ipair.first);
					output.addAll(ipair.second);
					return output;
				}).orElse(new LinkedList<>()));

		Reference<Parser<TypedAST>> lineParserRef = new Reference<>();
		Reference<Parser<Optional<TypedAST>>> defLineParserRef = new Reference<>();
		Parser<TypedAST> lineParser = new Ref<>(lineParserRef);
		Parser<Optional<TypedAST>> defLineParser = new Ref<>(defLineParserRef);


		Parser<TypedAST> defParser = new Terminal("def")
				.concatRight(new Terminal(Token.Kind.Identifier))
				.concat(args)
				.concat(new Terminal(null, ":").concatRight(type))
				.concat(new Or<TypedAST>(new Terminal("=").concatRight(exp), indent(defLineParser).compute(opt->opt.orElse(null))))
				.compute(pairs ->
						new DefDeclaration(pairs.first.first.first.text, pairs.first.second,
								pairs.first.first.second
										.stream().map(pair -> new Pair<String, UnparsedType>(pair.first.text, pair.second))
										.collect(Collectors.<Pair<String, UnparsedType>>toList()),
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
					"def x(t : Int) : Int = 2 + t\n" +
					"def y() : Int\n" +
					"\tx(4)*3\n" +
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

	@Test
	public void testDSLGrammar() {
		Parser<Token> ID = new Terminal(Token.Kind.Identifier);

		Reference<Parser<BiFunction<Environment, Type,Type>>> typePParserRef = new Reference<>();

		Parser<BiFunction<Environment, Type,Type>> typePParser = new Ref<>(typePParserRef);

		typePParserRef.set(new Terminal(".").concatRight(new Terminal(Token.Kind.Identifier)).concat(typePParser).opt().compute(
				pair ->
						pair.map(ipair -> (BiFunction<Environment, Type, Type>) (env, otype) ->
								ipair.second.apply(env, ((OperatableType) otype).checkOperator(new Invocation(null, ipair.first.text, null, FileLocation.UNKNOWN), env)))
								.orElse((env, otype) -> otype)
		));
		Parser<UnparsedType> type = new Terminal(Token.Kind.Identifier).concat(typePParser).compute(pair -> env -> {
			Type typei = env.lookupType(pair.first.text).getUse();
			if (pair.second != null) {
				return pair.second.apply(env, typei);
			} else {
				return typei;
			}
		});


		/*
		exn = ID exn'
		|	 ( e, e ) exn'

	   exn' = ( al ) exn'
		|      . ID exn'
		|	  : type exn'
*/

		Reference<Parser<TypedAST>> exref = new Reference<>();
		Reference<Parser<Function<TypedAST, TypedAST>>> expref = new Reference<>();
		Parser<TypedAST> Ex = new Ref<>(exref);
		Parser<Function<TypedAST, TypedAST>> Exp = new Ref<>(expref);

		Parser<List<TypedAST>> args = (new Terminal("(")
				.concatRight(Ex.concat(new Terminal(",").concatRight(Ex).rep()).opt()).concatLeft(new Terminal(")"))).compute(pair -> pair.map(ipair->cons(ipair.first, ipair.second)).orElse(new LinkedList<TypedAST>()));

		Parser<TypedAST> ebar = (new Or<>(
				new Terminal("(").concatRight(Ex).concatLeft(new Terminal(","))
						.concat(Ex).concatLeft(new Terminal(")")).concat(Exp)
						.<TypedAST>compute(pair -> pair.second.apply(new TupleObject(pair.first.first, pair.first.second))),
				new Terminal(Token.Kind.Identifier).concat(Exp).compute(pair -> pair.second.apply(new Variable(pair.first.text,pair.first.location)))
		));


		expref.set(
				new Or<Function<TypedAST, TypedAST>>(
						args.concat(Exp).compute(pair -> ast -> pair.second.apply(new Application(ast, new TupleObject(pair.first), ast.getLocation()))),
						new Terminal(".").concatRight(ID).concat(Exp).compute(pair -> ast -> new Invocation(ast, pair.first.text, null, pair.first.location)),
						new Terminal(":").concatRight(type).concat(Exp).compute(pair -> ast -> pair.second.apply(new TypeAsc(ast, pair.first)))
				).opt().compute(fn -> fn.orElse(inp -> inp))
		);

	/*
	e[fwd] = fwd e'[fwd]
		|	 ( exn, e[fwd] ) e'[fwd]
		|	 ( e[fwd], e ) e'[fwd]
		|	 e ( al[fwd] ) e'[fwd]

    e'[fwd] = epsilon
		|	  . ID e'[fwd]
		|	  ( al[fwd] ) e'[fwd]
		| 	  : type e'[fwd]
		 */
		Function<Parser<TypedAST>, Parser<TypedAST>> grammar = (fwd) -> {
			Reference<Parser<TypedAST>> eref = new Reference<>();
			Parser<TypedAST> E = new Ref<>(eref);
			Reference<Parser<Function<TypedAST, TypedAST>>> epref = new Reference<>();
			Parser<Function<TypedAST, TypedAST>> Ep = new Ref<>(epref);

			Reference<Parser<LinkedList<TypedAST>>> alneref = new Reference<>();
			Parser<LinkedList<TypedAST>> alne = new Ref<>(alneref);

			alneref.set( new Or<LinkedList<TypedAST>>(
					E.compute(ast -> new LinkedList<>(Arrays.asList(ast))),
					E.concatLeft(new Terminal(",")).concat(alne).compute(pair -> {
						pair.second.addFirst(pair.first);
						return pair.second;
					}))
			);
			Parser<LinkedList<TypedAST>> ali = alne.opt().compute(llst->llst.orElse(new LinkedList<TypedAST>()));

			alneref.set(new Or<LinkedList<TypedAST>>( E.compute(ast-> new LinkedList<TypedAST>()), E.concatLeft(new Terminal(",")).concat(alne).compute(pair -> {
				pair.second.addFirst(pair.first);
				return pair.second;
			})));

			Parser<LinkedList<TypedAST>> alf = new Terminal("(").concatRight(ali).concatLeft(new Terminal(")"));

			eref.set(
					new Or<TypedAST>(
						fwd.concat(Ep).compute(pair->pair.second.apply(pair.first)),
							new Terminal("(").concatRight(Ex).concatLeft(new Terminal(",")).concat(E).concatLeft(new Terminal(")")).concat(Ep)
									.compute(pair -> pair.second.apply(new TupleObject(pair.first.first, pair.first.second))),
							new Terminal("(").concatRight(E).concatLeft(new Terminal(",")).concat(Ex).concatLeft(new Terminal(")")).concat(Ep)
									.compute(pair -> pair.second.apply(new TupleObject(pair.first.first, pair.first.second))),
						Ex.concat(alf).concat(Ep).compute(pair -> pair.second.apply(new Application(pair.first.first, new TupleObject(pair.first.second), pair.first.first.getLocation())))
					)
			);

			epref.set(new Or<Function<TypedAST, TypedAST>>(
					new Terminal(".").concatRight(ID).concat(Ep).compute(pair -> ast -> pair.second.apply(new Invocation(ast, pair.first.text, null, pair.first.location))),
					alf.concat(Ep).compute(pair -> ast -> pair.second.apply(new Application(ast, TupleObject.fromList(pair.first, FileLocation.UNKNOWN), FileLocation.UNKNOWN))),
					new Terminal(":").concatRight(type).concat(Ep).compute(pair -> ast -> pair.second.apply(new TypeAsc(ast, pair.first)))
			).opt().compute(fn -> fn.orElse(inp -> inp)));

			return E;
		};

		exref.set(ebar);

		Parser<List<Token>> dslp = AbstractParser.<List<Token>>getParserOpt(stream -> {
			if (stream.peek().kind != Token.Kind.INDENT)
				return Optional.empty();
			stream.next();

			LinkedList<Token> chars = new LinkedList<>();
			while (stream.peek() != null && stream.peek().kind != Token.Kind.EOF && stream.peek().kind != Token.Kind.DEDENT)
				chars.add(stream.next());

			if (stream.peek().kind != Token.Kind.DEDENT && stream.peek().kind != Token.Kind.EOF)
				return Optional.empty();
			stream.next();

			return Optional.of(chars);
		});

		Parser<TypedAST> e = (
				grammar.apply(new Terminal("~").toggleBreak().compute(tok -> new DSLDummy(null))).concat(dslp).compute(pair->(TypedAST)new UnparsedDSL(pair.first, pair.second))
				);

		Parser<TypedAST> val = new Terminal("val").concatRight(new Terminal(Token.Kind.Identifier)).concatLeft(new Terminal(":")).concat(type).concatLeft(new Terminal("=")).concat(e)
				.compute(pairs -> new ValDeclaration(pairs.first.first.text, pairs.first.second, pairs.second, pairs.first.first.location));

		Parser<TypedAST> lines = new Terminal(Token.Kind.NEWLINE).concatRight(val).rep().compute(Sequence::fromList);


		TypedAST result = null;
		try {
			result = lines.parse(new LexStream("test", new StringReader("val t:Int = (~,2) \n" +
					"	term = 1")));
		} catch (ParserException ex) {
			throw new RuntimeException(ex);
		}
	}

}
