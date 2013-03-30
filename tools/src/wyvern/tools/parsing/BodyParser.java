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
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.Assignment;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.Sequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.extensions.TupleObject;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.typedAST.extensions.values.IntegerConstant;
import wyvern.tools.typedAST.extensions.values.StringConstant;
import wyvern.tools.typedAST.extensions.values.UnitVal;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

// NB! See: http://en.cppreference.com/w/cpp/language/operator_precedence

public class BodyParser implements RawASTVisitor<Environment, TypedAST> {
	private BodyParser() { }
	private static BodyParser instance = new BodyParser();
	public static BodyParser getInstance() { return instance; }

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
		if (first instanceof Declaration)
			env = ((Declaration)first).extend(env);
		LineSequenceParser parser = first.getLineSequenceParser();
		LineSequence rest = node.getRest();
		
		if (rest == null) // only one statement in the block.
			return first;
		
		if (parser != null) {
			// if First is a Statement, get the statement continuation parser and use it to parse the rest
			return parser.parse(first, rest, env);
		} else {
			Sequence s = new Sequence(first);
			
			while (rest != null) {
				first = rest.getFirst().accept(this, env);
				
				if (first instanceof Declaration)
					env = ((Declaration)first).extend(env);
				
				// Make sure that we allow val's etc to parse their bodies/continuations properly!
				LineSequenceParser bodyParser = first.getLineSequenceParser();
				if (bodyParser == null || rest.getRest() == null) {
					s.append(first);
				} else {
					// Has to eat the rest of the LineSequence!!!
					s.append(bodyParser.parse(first, rest.getRest(), env));
					return s;
				}
				
				rest = rest.getRest();
			}
			
			return s;
		}
	}

	@Override
	public TypedAST visit(Line node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}
	
	private TypedAST parseAtomicExpr(Pair<ExpressionSequence,Environment> ctx) {
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

		TypedAST first = node.getFirst().accept(this, env);
		LineParser parser = first.getLineParser();
		ExpressionSequence rest = node.getRest();
		ctx.first = rest;
		
		if (ParseUtils.checkFirst(",", ctx)) {
			FileLocation commaLine = ParseUtils.parseSymbol(",",ctx).getLocation();
			TypedAST remaining = parseAtomicExpr(ctx);
			first = new TupleObject(first, remaining, commaLine); // FIXME: should be: remaining.getLine());
		}
		
		if (parser == null)
			return first;
		
		// if first is a special form, get the expression continuation parser and use it to parse the rest
		return parser.parse(first, ctx);
	}
	
	private TypedAST parseApplication(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseAtomicExpr(ctx);
		
		while (ctx.first != null && (ctx.first.getFirst() instanceof Parenthesis || ParseUtils.checkFirst(".",ctx))) {
			if (ParseUtils.checkFirst(".",ctx)) {
				ParseUtils.parseSymbol(".", ctx);
				Symbol sym = ParseUtils.parseSymbol(ctx);
				ast = new Invocation(ast, sym.name, null, sym.getLocation());
			} else {
				TypedAST argument = parseAtomicExpr(ctx);
				ast = new Application(ast, argument, argument.getLocation());
			}
		}

		return ast;
	}

	// TODO: refactor to reuse code between parseProduct and parseSum 
	private TypedAST parseProduct(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseApplication(ctx);
		
		while (ctx.first != null && isProductOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseApplication(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseSum(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseProduct(ctx);
		
		while (ctx.first != null && isSumOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseProduct(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}

	private TypedAST parseRelationalOps(Pair<ExpressionSequence, Environment> ctx) {
		TypedAST ast = parseSum(ctx);
		
		while (ctx.first != null && isRelationalOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseSum(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseAnd(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseRelationalOps(ctx);
		
		while (ctx.first != null && isAndOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseRelationalOps(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseOr(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseAnd(ctx);
		
		while (ctx.first != null && isOrOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();
			String operatorName = s.name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseAnd(ctx);
			ast = new Invocation(ast, operatorName, argument, s.getLocation());
		}
		
		return ast;
	}
	
	private TypedAST parseEquals(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseOr(ctx);
		while (ctx.first != null && isEqualsOperator(ctx.first.getFirst())) {
			Symbol s = (Symbol) ctx.first.getFirst();			
			ctx.first = ctx.first.getRest();
			TypedAST value = parseOr(ctx);
			ast = new Assignment(ast, value, s.getLocation());
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
		TypedAST result = parseEquals(ctx); // Start trying with the lowest precedence operator.
		if (ctx.first != null)
			reportError(UNEXPECTED_INPUT_WITH_ARGS, (ctx.first.getFirst()!=null)?ctx.first.getFirst().toString():null, ctx.first);
		return result;
	}

	@Override
	public TypedAST visit(Parenthesis node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}

}