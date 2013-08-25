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
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.PartialDecl;
import wyvern.tools.typedAST.core.declarations.PartialDeclSequence;
import wyvern.tools.typedAST.core.expressions.TupleObject;
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
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.util.concurrent.atomic.AtomicReference;

// NB! See: http://en.cppreference.com/w/cpp/language/operator_precedence

public class BodyParser implements RawASTVisitor<Environment, TypedAST> {
    private DSLDummy dslToken;
    private Tuple tuple;
	private CompilationContext globalCtx;

	public BodyParser(CompilationContext globalCtx) {
		this.globalCtx = globalCtx;
	}

	public BodyParser() {
		globalCtx = null;
	}

	private interface Resolver {
        public void resolve();
        public Resolver getPrev();
    }

    private Resolver resolver = null;

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
	
	private TypedAST parseAtomicExpr(CompilationContext ctx) {
		ExpressionSequence node = ctx.getTokens();
		Environment env = ctx.getEnv();
		// TODO: should not be necessary, but a useful sanity check
		// FIXME: gets stuck on atomics like {$L $L} which were sometimes leftover by lexer from comments.
		if (node.children.size() == 0) {
			if (node instanceof Parenthesis) {
				ctx.setTokens(null);
				return UnitVal.getInstance(node.getLocation());
			} else
				throw new RuntimeException("cannot parse an empty list");
		}

        if (ParseUtils.checkFirst("~", ctx)) {
            ParseUtils.parseSymbol("~", ctx);
            dslToken = new DSLDummy(ctx.getExpected());
			ctx.setDSLToken(dslToken);
            return dslToken;
        }

		TypedAST first = node.getFirst().accept(new BodyParser(ctx), env);
		LineParser parser = first.getLineParser();
		ExpressionSequence rest = node.getRest();
		ctx.setTokens(rest);

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
	
	private TypedAST parseApplication(CompilationContext ctx) {
		TypedAST ast = parseAtomicExpr(ctx);
		
		while (ctx.getTokens() != null && (ctx.getTokens().getFirst() instanceof Parenthesis || ParseUtils.checkFirst(".",ctx))) {
			if (ParseUtils.checkFirst(".",ctx)) {
				ParseUtils.parseSymbol(".", ctx);
                Symbol sym = ParseUtils.parseSymbol(ctx);
                ast = new Invocation(ast, sym.name, null, sym.getLocation());
            } else {
                Type type = ast.typecheck(ctx.getEnv());
                if (type instanceof Arrow) {
                    ctx.setExpected(((Arrow) type).getArgument());
                } else {
					ctx.setExpected(null);
                }
				TypedAST argument = parseAtomicExpr(ctx);
				ast = new Application(ast, argument, argument.getLocation());
			}
		}

		return ast;
	}

	// TODO: refactor to reuse code between parseProduct and parseSum 
	private TypedAST parseProduct(CompilationContext ctx) {
		TypedAST ast = parseApplication(ctx);
		
		while (ctx.getTokens() != null && isProductOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			String operatorName = s.name;
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST argument = parseApplication(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseSum(CompilationContext ctx) {
		TypedAST ast = parseProduct(ctx);
		
		while (ctx.getTokens() != null && isSumOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			String operatorName = s.name;
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST argument = parseProduct(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}

	private TypedAST parseRelationalOps(CompilationContext ctx) {
		TypedAST ast = parseSum(ctx);
		
		while (ctx.getTokens() != null && isRelationalOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			String operatorName = s.name;
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST argument = parseSum(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseAnd(CompilationContext ctx) {
		TypedAST ast = parseRelationalOps(ctx);
		
		while (ctx.getTokens() != null && isAndOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			String operatorName = s.name;
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST argument = parseRelationalOps(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseOr(CompilationContext ctx) {
		TypedAST ast = parseAnd(ctx);
		
		while (ctx.getTokens() != null && isOrOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			String operatorName = s.name;
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST argument = parseAnd(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseEquals(CompilationContext ctx) {
		TypedAST ast = parseOr(ctx);
		while (ctx.getTokens() != null && isEqualsOperator(ctx.getTokens().getFirst())) {
			Symbol s = (Symbol) ctx.getTokens().getFirst();
			ctx.setTokens(ctx.getTokens().getRest());
			TypedAST value = parseOr(ctx);
			ast = new Assignment(ast, value, s.getLocation());
		}
		
		return ast;
	}

	private TypedAST parseTuple(CompilationContext ctx) {

		if (ctx.getExpected() != null && ctx.getExpected() instanceof Tuple) {
			Tuple tuple = ctx.getExpectedTuple();
			if (tuple == null)
				tuple = (Tuple) ctx.getExpected();
			ctx.setExpected(tuple.getFirst());
			ctx.setExpectedTuple(tuple.getRest());
		}

		TypedAST ast = parseEquals(ctx);

		while (ctx.getTokens() != null && ParseUtils.checkFirst(",", ctx)) {
			FileLocation commaLine = ParseUtils.parseSymbol(",",ctx).getLocation();
			ctx.setExpected(ctx.getExpectedTuple());
			TypedAST remaining = parseTuple(ctx);
			ast = new TupleObject(ast, remaining, commaLine);
		}
        tuple = null;
		return ast;
	}

    private TypedAST parseDSL(CompilationContext ctx) {
        TypedAST ast = parseTuple(ctx);

		DSLDummy dslToken = ctx.getDSLToken();
        if (dslToken != null && ctx.getTokens() != null) {
            dslToken.setDef(dslToken.getExpected().getParser().parse(ast, ctx));
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
		CompilationContext ctx = new CompilationContext(globalCtx, node, env);
		TypedAST result = parseDSL(ctx); // Start trying with the lowest precedence operator.
		if (ctx.getTokens() != null)
			reportError(UNEXPECTED_INPUT_WITH_ARGS, (ctx.getTokens().getFirst()!=null)? ctx.getTokens().getFirst().toString():null, ctx.getTokens());
		return result;
	}

	@Override
	public TypedAST visit(Parenthesis node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}

}