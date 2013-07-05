package wyvern.tools.parsing;

import static wyvern.tools.errors.ErrorMessage.UNEXPECTED_INPUT_WITH_ARGS;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.IntLiteral;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.RawASTVisitor;
import wyvern.tools.rawAST.StringLiteral;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.rawAST.Unit;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.PartialDecl;
import wyvern.tools.typedAST.core.declarations.PartialDeclSequence;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.DSLDummy;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.Pair;

import java.util.concurrent.atomic.AtomicReference;

// NB! See: http://en.cppreference.com/w/cpp/language/operator_precedence

public class BodyParser implements RawASTVisitor<Environment, TypedAST> {
    private DSLDummy dslToken;
    private Tuple tuple;

    private BodyParser() { }
	private static BodyParser instance = new BodyParser();
	public static BodyParser getInstance() { return instance; }

    private interface Resolver {
        public void resolve();
        public Resolver getPrev();
    }

    private Resolver resolver = null;
    private Type expected = null;
    public void setExpected(Type expected) {
        this.expected = expected;
    }

	@Override
	public TypedAST visit(IntLiteral node, Environment env) {
		return new IntegerConstant(node.data);
	}

	@Override
	public TypedAST visit(StringLiteral node, Environment env) {
		return new StringConstant(node.data);
	}

	@Override
	public TypedAST visit(Symbol node, Environment env) {
		NameBinding binding = env.lookup(node.name);
		if (binding == null) {
			// return new Variable(new NameBindingImpl(node.name, null), node.getLine());
			reportError(ErrorMessage.VARIABLE_NOT_DECLARED, node.name, node);
		}
		
		return binding.getUse();
		// return new Variable(binding, node.getLine());
	}

	@Override
	public TypedAST visit(Unit node, Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * This is a typical entry point to the file being parsed, as well as for parsing indented sub-blocks.
	 */
	@Override
	public TypedAST visit(LineSequence node, Environment env) {
		// TODO: should not be necessary, but need sanity check somewhere!
		if (node.children.size() == 0) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);
		}

		TypedAST first = node.getFirst().accept(this, env);
		if (first instanceof EnvironmentExtender)
			env = ((EnvironmentExtender)first).extend(env);
		LineSequenceParser parser = first.getLineSequenceParser();
		LineSequence rest = node.getRest();
		
		if (rest == null) // only one statement in the block. 
		{
			if (first instanceof PartialDecl)
				first = ((PartialDecl) first).getAST(env);
			return first;
		}
		
		if (parser != null) {
			// if First is a Statement, get the statement continuation parser and use it to parse the rest
			return parser.parse(first, rest, env);
		} else {
			
			PartialDeclSequence pds = new PartialDeclSequence();
			final Sequence s = new Sequence();
			
			if (first instanceof PartialDecl) {
				env = pds.add((PartialDecl) first, env);
			} else {
				s.append(first);
			}
            final AtomicReference<PartialDeclSequence> pdsRef = new AtomicReference<>(pds);
            final AtomicReference<Environment> envRef = new AtomicReference<>(env);
            resolver = new Resolver() {
                Resolver saved = resolver;
                @Override
                public void resolve() {
                    PartialDeclSequence pds = pdsRef.get();
                    if (!pds.isResolved() && !pds.isEmpty()) {
                        Pair<TypedAST, Environment> pair = pds.resolve(envRef.get());
                        pdsRef.set(new PartialDeclSequence());
                        s.append(pair.first);
                    }
                }

                @Override
                public Resolver getPrev() {
                    return saved;
            }
        };

        while (rest != null) {
            first = rest.getFirst().accept(this, envRef.get());
            if (first instanceof PartialDecl) {
                envRef.set(pdsRef.get().add((PartialDecl) first, envRef.get()));
                rest = rest.getRest();
                continue;
            } else if (!pdsRef.get().isResolved() && !pdsRef.get().isEmpty()) {
                resolver.resolve();
            }

            if (first instanceof EnvironmentExtender)
                envRef.set(((EnvironmentExtender) first).extend(envRef.get()));
				
				// Make sure that we allow val's etc to parse their bodies/continuations properly!
				LineSequenceParser bodyParser = first.getLineSequenceParser();
				if (bodyParser == null || rest.getRest() == null) {
					s.append(first);
				} else {
					// Has to eat the rest of the LineSequence!!!
					s.append(bodyParser.parse(first, rest.getRest(), envRef.get()));
					return s;
				}
				
				rest = rest.getRest();
			}

			if (resolver != null) {
                resolver.resolve();
                resolver = resolver.getPrev();
            }
			
			return s;
		}
	}

	@Override
	public TypedAST visit(Line node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}
	
	private TypedAST parseAtomicExpr(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		ExpressionSequence node = ctx.first;
		Environment env = ctx.second;
		// TODO: should not be necessary, but a useful sanity check
		// FIXME: gets stuck on atomics like {$L $L} which were sometimes leftover by lexer from comments.
		if (node.children.size() == 0) {
			if (node instanceof Parenthesis) {
				ctx.first = null;
				return UnitVal.getInstance(node.getLocation());
			} else
				throw new RuntimeException("cannot parse an empty list");
		}

        if (ParseUtils.checkFirst("~", ctx)) {
            ParseUtils.parseSymbol("~", ctx);
            dslToken = new DSLDummy();
            return dslToken;
        }

		TypedAST first = node.getFirst().accept(this, env);
		LineParser parser = first.getLineParser();
		ExpressionSequence rest = node.getRest();
		ctx.first = rest;

		if (parser == null) {
            if (resolver != null)
                resolver.resolve();
			return first;
        }
		
		if (parser instanceof DeclParser) {
			return new PartialDecl(((DeclParser) parser).parseDeferred(first, ctx));
		}
        if (resolver != null)
            resolver.resolve();
		// if first is a special form, get the expression continuation parser and use it to parse the rest
		return parser.parse(first, ctx);
	}
	
	private TypedAST parseApplication(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseAtomicExpr(ctx, expected);
		
		while (ctx.first != null && (ctx.first.getFirst() instanceof Parenthesis || ParseUtils.checkFirst(".",ctx))) {
			if (ParseUtils.checkFirst(".",ctx)) {
				ParseUtils.parseSymbol(".", ctx);
                Symbol sym = ParseUtils.parseSymbol(ctx);
                ast = new Invocation(ast, sym.name, null, sym.getLocation());
            } else {
                Type type = ast.typecheck(ctx.second);
                if (type instanceof Arrow) {
                    expected = ((Arrow) type).getArgument();
                } else {
                    expected = null;
                }
				TypedAST argument = parseAtomicExpr(ctx, expected);
				ast = new Application(ast, argument, argument.getLocation());
			}
		}

		return ast;
	}

	// TODO: refactor to reuse code between parseProduct and parseSum 
	private TypedAST parseProduct(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseApplication(ctx, expected);
		
		while (ctx.first != null && isProductOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseApplication(ctx, expected);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseSum(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseProduct(ctx, expected);
		
		while (ctx.first != null && isSumOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseProduct(ctx, expected);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}

	private TypedAST parseRelationalOps(Pair<ExpressionSequence, Environment> ctx, Type expected) {
		TypedAST ast = parseSum(ctx, expected);
		
		while (ctx.first != null && isRelationalOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseSum(ctx, expected);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseAnd(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseRelationalOps(ctx, expected);
		
		while (ctx.first != null && isAndOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseRelationalOps(ctx, expected);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseOr(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseAnd(ctx, expected);
		
		while (ctx.first != null && isOrOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseAnd(ctx, expected);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseEquals(Pair<ExpressionSequence,Environment> ctx, Type expected) {
		TypedAST ast = parseOr(ctx, expected);
		while (ctx.first != null && isEqualsOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();			
			ctx.first = ctx.first.getRest();
			TypedAST value = parseOr(ctx, expected);
			ast = new Assignment(ast, value, s.getLocation());
		}
		
		return ast;
	}

	private TypedAST parseTuple(Pair<ExpressionSequence, Environment> ctx, Type expected) {
		TypedAST ast = parseEquals(ctx, expected);

		while (ctx.first != null && ParseUtils.checkFirst(",", ctx)) {
			FileLocation commaLine = ParseUtils.parseSymbol(",",ctx).getLocation();
            if (expected != null && expected instanceof Tuple) {
                if (tuple == null)
                    tuple = (Tuple) expected;
                expected = tuple.getFirst();
                tuple = tuple.getRest();
            }
			TypedAST remaining = parseTuple(ctx, expected);
			ast = new TupleObject(ast, remaining, commaLine);
		}
        tuple = null;
		return ast;
	}

    private TypedAST parseDSL(Pair<ExpressionSequence, Environment> ctx, Type expected) {
        TypedAST ast = parseTuple(ctx, expected);

        if (this.dslToken != null && ctx.first != null) {
            dslToken.setDef(expected.getParser().parse(ast, ctx));
        }

        return ast;
    }

	private boolean isProductOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("*") || operatorName.equals("/");
	}

	private boolean isSumOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("+") || operatorName.equals("-");
	}
	
	private boolean isRelationalOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals(">") || operatorName.equals("<") || operatorName.equals("!=")
			|| operatorName.equals(">=") || operatorName.equals("<=") || operatorName.equals("==")	
			|| operatorName.equals("!=");
	}
	
	private boolean isAndOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("&&");
	}
	
	private boolean isOrOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("||");
	}
	
	private boolean isEqualsOperator(RawAST opNode) {
		if (!(opNode instanceof Symbol))
			return false;
		String opName = ((Symbol)opNode).name;
		
		return opName.equals("=");
	}

	public TypedAST visit(ExpressionSequence node, Environment env) {
		Pair<ExpressionSequence,Environment> ctx = new Pair<ExpressionSequence,Environment>(node, env); 
		TypedAST result = parseDSL(ctx, expected); // Start trying with the lowest precedence operator.
		if (ctx.first != null)
			reportError(UNEXPECTED_INPUT_WITH_ARGS, (ctx.first.getFirst()!=null)?ctx.first.getFirst().toString():null, ctx.first);
		return result;
	}

	@Override
	public TypedAST visit(Parenthesis node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}

}