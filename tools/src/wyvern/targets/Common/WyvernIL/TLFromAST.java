package wyvern.targets.Common.WyvernIL;

import wyvern.targets.Common.WyvernIL.Expr.*;
import wyvern.targets.Common.WyvernIL.Imm.*;
import wyvern.targets.Common.WyvernIL.Stmt.Assign;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TLFromAST implements CoreASTVisitor {
	private List<Statement> statements = new LinkedList<Statement>();
	private Expression expr = null;
	private Operand op = null;

	@Override
	public void visit(Fn fn) {
		List<NameBinding> bindings = fn.getArgBindings();
		TypedAST body = fn.getBody();
		
		List<Operand> args = new LinkedList<Operand>();
		
		for (NameBinding binding : bindings){
			TypedAST ast = binding.getUse();
			
			if(!(ast instanceof CoreAST))
				throw new RuntimeException();
			
			CoreAST cAst = (CoreAST) ast;
			TLFromAST visitor = new TLFromAST();
			
			cAst.accept(visitor);
			
			statements.addAll(visitor.getStatements());
			args.add(visitor.getOp());
		}

		if (!(body instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cBody = (CoreAST) body;
		ExnFromAST visitor = new ExnFromAST();
		
		cBody.accept(visitor);
		this.statements.addAll(visitor.getStatments());
		
		this.op = new FnValue(args, statements);
	}

	@Override
	public void visit(Invocation invocation) {
		TypedAST arg = invocation.getArgument();
		TypedAST rec = invocation.getReceiver();
		String name = invocation.getOperationName();

		if(!(arg instanceof CoreAST) || !(rec instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cArg = (CoreAST) arg;
		CoreAST cRec = (CoreAST) rec;
		
		TLFromAST argVisitor = new TLFromAST();
		cArg.accept(argVisitor);
		
		TLFromAST recVisitor = new TLFromAST();
		cRec.accept(recVisitor);
		
		this.statements.addAll(recVisitor.getStatements());		
		this.statements.addAll(argVisitor.getStatements());
		
		this.op = recVisitor.getOp();
		this.expr = new Inv(op, name);
	}

	@Override
	public void visit(Application application) {
		TypedAST arg = application.getArgument();
		TypedAST func = application.getFunction();
				
		List<Operand> args = new LinkedList<Operand>();
		
		if(!(arg instanceof CoreAST) || !(func instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cArg = (CoreAST) arg;
		CoreAST cFunc = (CoreAST) func;
		
		TLFromAST argVisitor = new TLFromAST();
		cArg.accept(argVisitor);
		this.statements.addAll(argVisitor.getStatements());
		
		args.add(argVisitor.getOp());
		
		TLFromAST funcVisitor = new TLFromAST();
		cFunc.accept(funcVisitor);
		this.statements.addAll(funcVisitor.getStatements());
		
		this.expr = new FnInv (funcVisitor.getOp(), args);
	}

	@Override
	public void visit(Variable variable) {
		this.op = new VarRef(variable.getName());
	}

	@Override
	public void visit(IntegerConstant integerConstant) {
		this.op = new IntValue (integerConstant.getValue());
	}

	@Override
	public void visit(StringConstant stringConstant) {
		this.op = new StringValue( stringConstant.getValue());
	}

	@Override
	public void visit(BooleanConstant booleanConstant) {
		this.op = new BoolValue(booleanConstant.getValue());
	}

	@Override
	public void visit(UnitVal unitVal) {
		this.op = new UnitValue(unitVal.getType());
	}

	@Override
	public void visit(New new1) {
		Map<String, TypedAST> args = new1.getArgs();
		ClassDeclaration decl = new1.getClassDecl();
		
		if(!(decl instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cDecl = (CoreAST) decl;
		ExnFromAST visitor = new ExnFromAST();
		
		cDecl.accept(visitor);
		this.statements.addAll(visitor.getStatments());
		
		for (Map.Entry<String, TypedAST> arg : args.entrySet()){
			if(!(arg instanceof CoreAST))
				throw new RuntimeException();
			
			CoreAST cArg = (CoreAST) arg;
			TLFromAST argVisitor = new TLFromAST();
			
			cArg.accept(argVisitor);
			
			this.statements.addAll(argVisitor.getStatements());
		}
		
		this.expr = new wyvern.targets.Common.WyvernIL.Expr.New(decl.getName());
	}

	@Override
	public void visit(TupleObject tuple) {
		TypedAST[] objects = tuple.getObjects();

		List<Statement> stmts = new LinkedList<Statement>();
		List<Operand> ops = new LinkedList<Operand>();

		for(TypedAST object : objects){
			if (!(object instanceof CoreAST)) {
				throw new RuntimeException();
			}

			CoreAST cast = (CoreAST) object;
			TLFromAST visitor = new TLFromAST();
			cast.accept(visitor);

			stmts.addAll(visitor.getStatements());
			ops.add(visitor.getOp());
		}

		this.statements = stmts;
		this.op = new TupleValue(ops);
	}

	@Override
	public void visit(Assignment assignment) {
		TypedAST dst = assignment.getTarget();
		TypedAST src = assignment.getValue();
		
		if(!(dst instanceof CoreAST) || !(src instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cDst = (CoreAST) dst;
		CoreAST cSrc = (CoreAST) src;
		
		TLFromAST dstVisitor = new TLFromAST();
		cDst.accept(dstVisitor);
		
		TLFromAST srcVisitor = new TLFromAST();
		cSrc.accept(srcVisitor);
		
		this.statements.addAll(dstVisitor.getStatements());		
		this.statements.addAll(srcVisitor.getStatements());
		this.statements.add(new Assign(dstVisitor.getOp(), srcVisitor.getOp()));
	}












	@Override
	public void visit(ValDeclaration valDeclaration) {

	}

	@Override
	public void visit(VarDeclaration valDeclaration) {

	}

	@Override
	public void visit(DefDeclaration meth) {

	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {

	}

	@Override
	public void visit(ClassDeclaration clsDeclaration) {

	}

	@Override
	public void visit(LetExpr let) {

	}

	@Override
	public void visit(IfExpr ifExpr) {

	}

	@Override
	public void visit(WhileStatement whileStatement) {

	}

	@Override
	public void visit(TypeInstance typeInstance) {

	}

	@Override
	public void visit(Sequence sequence) {

	}


	public Operand getOp() {
		return op;
	}

	public List<Statement> getStatements() {
		return statements;
	}
	
	public Expression getExpr(){
		return expr;
	}
}
