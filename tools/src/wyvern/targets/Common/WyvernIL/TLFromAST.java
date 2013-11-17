package wyvern.targets.Common.WyvernIL;

import org.objectweb.asm.commons.StaticInitMerger;
import wyvern.targets.Common.WyvernIL.Def.Def;
import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.targets.Common.WyvernIL.Def.ValDef;
import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Imm.*;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TLFromAST implements CoreASTVisitor {
	private List<Statement> statements = new LinkedList<Statement>();
	private Operand op;

	@Override
	public void visit(Fn fn) {

	}

	@Override
	public void visit(Invocation invocation) {

	}

	@Override
	public void visit(Application application) {

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

	}

	@Override
	public void visit(New new1) {

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

		statements = stmts;
		op = new TupleValue(ops);
	}

	@Override
	public void visit(Assignment assignment) {

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
}
