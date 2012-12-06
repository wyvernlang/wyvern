package wyvern.tools.parsing;

import wyvern.tools.rawAST.*;
import wyvern.tools.typedAST.*;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;
import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ErrorMessage.UNEXPECTED_INPUT;
import static wyvern.tools.errors.ToolError.reportError;

public class CoreParser implements RawASTVisitor<Environment, TypedAST> {
	private CoreParser() { }
	private static CoreParser instance = new CoreParser();
	public static CoreParser getInstance() { return instance; }

	@Override
	public TypedAST visit(Int node, Environment env) {
		return new IntegerConstant(node.data);
	}

	@Override
	public TypedAST visit(StringNode node, Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedAST visit(Symbol node, Environment env) {
		NameBinding binding = env.lookup(node.name);
		if (binding == null)
			reportError(VARIABLE_NOT_DECLARED, node.name, node);
			
		return binding.getUse();
	}

	@Override
	public TypedAST visit(Unit node, Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedAST visit(LineSequence node, Environment env) {
		// TODO: should not be necessary, but need sanity check somewhere!
		if (node.children.size() == 0)
			throw new RuntimeException("cannot parse an empty list");

		TypedAST first = node.getFirst().accept(this, env);
		LineSequenceParser parser = first.getLineSequenceParser();
		LineSequence rest = node.getRest();
		
		if (rest == null)
			return first;
		
		if (parser != null) {
			// if First is a Statement, get the statement continuation parser and use it to parse the rest
			return parser.parse(first, rest, env);
		} else {
			// otherwise, parse the rest and use a sequence
			throw new RuntimeException("sequences not implemented");
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
		if (node.children.size() == 0)
			throw new RuntimeException("cannot parse an empty list");

		TypedAST first = node.getFirst().accept(this, env);
		LineParser parser = first.getLineParser();
		ExpressionSequence rest = node.getRest();
		ctx.first = rest;
		
		if (rest == null || parser == null)
			return first;
		
		// if first is a special form, get the expression continuation parser and use it to parse the rest
		return parser.parse(first, ctx);
	}
	
	private TypedAST parseApplication(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseAtomicExpr(ctx);
		
		while (ctx.first != null && ctx.first.getFirst() instanceof Parenthesis) {
			TypedAST argument = parseAtomicExpr(ctx);
			ast = new Application(ast, argument);
		}
		
		return ast;
	}

	// TODO: refactor to reuse code between parseProduct and parseSum 
	private TypedAST parseProduct(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseApplication(ctx);
		
		while (ctx.first != null && isProductOperator(ctx.first.getFirst())) {
			String operatorName = ((Symbol)ctx.first.getFirst()).name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseApplication(ctx);
			ast = new Invocation(ast, operatorName, argument);
		}
		
		return ast;
	}
	
	private TypedAST parseSum(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseProduct(ctx);
		
		while (ctx.first != null && isSumOperator(ctx.first.getFirst())) {
			String operatorName = ((Symbol)ctx.first.getFirst()).name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseProduct(ctx);
			ast = new Invocation(ast, operatorName, argument);
		}
		
		return ast;
	}

	private TypedAST parseRelationalOps(Pair<ExpressionSequence, Environment> ctx) {
		TypedAST ast = parseSum(ctx);
		
		while (ctx.first != null && isRelationalOperator(ctx.first.getFirst())) {
			String operatorName = ((Symbol)ctx.first.getFirst()).name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseSum(ctx);
			ast = new Invocation(ast, operatorName, argument);
		}
		
		return ast;
	}
	
	private TypedAST parseAnd(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseRelationalOps(ctx);
		
		while (ctx.first != null && isAndOperator(ctx.first.getFirst())) {
			String operatorName = ((Symbol)ctx.first.getFirst()).name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseRelationalOps(ctx);
			ast = new Invocation(ast, operatorName, argument);
		}
		
		return ast;
	}
	
	private TypedAST parseOr(Pair<ExpressionSequence,Environment> ctx) {
		TypedAST ast = parseAnd(ctx);
		
		while (ctx.first != null && isOrOperator(ctx.first.getFirst())) {
			String operatorName = ((Symbol)ctx.first.getFirst()).name;
			ctx.first = ctx.first.getRest();
			TypedAST argument = parseAnd(ctx);
			ast = new Invocation(ast, operatorName, argument);
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
	
	private boolean isRelationalOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals(">") || operatorName.equals("<") || operatorName.equals("!=")
			|| operatorName.equals(">=") || operatorName.equals("<=") || operatorName.equals("==")	
			|| operatorName.equals("!=");
	}

	public TypedAST visit(ExpressionSequence node, Environment env) {
		Pair<ExpressionSequence,Environment> ctx = new Pair<ExpressionSequence,Environment>(node, env); 
		TypedAST result = parseOr(ctx);
		if (ctx.first != null)
			reportError(UNEXPECTED_INPUT, ctx.first);
		return result;
	}

	@Override
	public TypedAST visit(Parenthesis node, Environment env) {
		return visit((ExpressionSequence)node, env);
	}

}
