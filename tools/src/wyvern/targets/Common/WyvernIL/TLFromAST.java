package wyvern.targets.Common.WyvernIL;

import wyvern.targets.Common.WyvernIL.Def.Def;
import wyvern.targets.Common.WyvernIL.Expr.*;
import wyvern.targets.Common.WyvernIL.Imm.*;
import wyvern.targets.Common.WyvernIL.Stmt.*;
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
import wyvern.tools.types.extensions.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TLFromAST implements CoreASTVisitor {
	private List<Statement> statements = new LinkedList<Statement>();
	private Expression expr = null;
	private Operand op = null;
	private static AtomicInteger lambdaMeth = new AtomicInteger(0);
	private static AtomicInteger ifRet = new AtomicInteger(0);

	@Override
	public void visit(Fn fn) {
		List<NameBinding> bindings = fn.getArgBindings();
		TypedAST body = fn.getBody();
		
		List<Operand> args = new LinkedList<Operand>();
		List<Statement> innerStatements = new LinkedList<>();


		if (!(body instanceof CoreAST))
			throw new RuntimeException();
		
		CoreAST cBody = (CoreAST) body;
		ExnFromAST visitor = new ExnFromAST();
		
		cBody.accept(visitor);
		innerStatements.addAll(visitor.getStatments());
		LinkedList<Def.Param> params = new LinkedList<>();
		for (NameBinding binding : bindings) {
			params.add(new Def.Param(binding.getName(), binding.getType()));
		}

		String mName = lambdaMeth.getAndIncrement() + "$lambda";
		this.statements.add(new Defn(new Def(mName, params, innerStatements)));
		this.op = new VarRef(mName);
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
		this.op = new UnitValue();
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
		this.statements.add(new Assign(dstVisitor.getOp(), new Immediate(srcVisitor.getOp())));
	}

	private TLFromAST TLFromASTApply(TypedAST in) {
		if (!(in instanceof CoreAST))
			throw new RuntimeException();
		CoreAST ast = (CoreAST) in;
		TLFromAST t = new TLFromAST();
		ast.accept(t);
		return t;
	}
	private List<Statement> getBodyAST(TypedAST in) {
		if (!(in instanceof CoreAST))
			throw new RuntimeException();
		CoreAST ast = (CoreAST) in;
		ExnFromAST t = new ExnFromAST();
		ast.accept(t);
		return t.getStatments();
	}

	@Override
	public void visit(IfExpr ifExpr) {
		Label end = new Label();
		Label next = new Label();
		VarRef result = new VarRef("ifRet$"+ifRet.getAndIncrement());
		for(IfExpr.IfClause clause : ifExpr.getClauses()) {
			TLFromAST tl = TLFromASTApply(clause.getClause());
			Label ifT = new Label();
			statements.add(next);
			next = new Label();
			statements.addAll(tl.getStatements());
			statements.add(new IfStmt(tl.getOp(),ifT));
			statements.add(new Goto(next));
			statements.add(ifT);
			List<Statement> bodyAST = getBodyAST(clause.getBody());
			if (bodyAST.size() == 0) {
				statements.add(new Assign(result, new Immediate(new UnitValue())));
			} else {
				Statement last = bodyAST.get(bodyAST.size()-1);
				List<Statement> notLast = bodyAST.subList(0,bodyAST.size()-1);
				statements.addAll(notLast);
				if (!(last instanceof Pure))
					throw new RuntimeException();
				statements.add(new Assign(result, ((Pure)last).getExpression()));
			}
			statements.add(new Goto(end));
		}
		statements.add(next);
		statements.add(new Goto(end));
		statements.add(end);

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
