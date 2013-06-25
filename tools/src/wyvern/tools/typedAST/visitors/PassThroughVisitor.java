package wyvern.tools.typedAST.visitors;

import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.IfExpr;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.TypeInstance;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.expressions.WhileStatement;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;

public class PassThroughVisitor implements CoreASTVisitor {

	private CoreASTVisitor next;

	public PassThroughVisitor(CoreASTVisitor next) {
		this.next = next;
	}
	
	@Override
	public void visit(Fn fn) {
		next.visit(fn);
		
	}

	@Override
	public void visit(Invocation invocation) {
		next.visit(invocation);
	}

	@Override
	public void visit(Application application) {
		next.visit(application);
	}

	@Override
	public void visit(ValDeclaration valDeclaration) {
		next.visit(valDeclaration);
	}

	@Override
	public void visit(VarDeclaration valDeclaration) {
		next.visit(valDeclaration);
	}

	@Override
	public void visit(Variable variable) {
		next.visit(variable);
	}

	@Override
	public void visit(IntegerConstant booleanConstant) {
		next.visit(booleanConstant);
	}

	@Override
	public void visit(StringConstant booleanConstant) {
		next.visit(booleanConstant);
	}

	@Override
	public void visit(BooleanConstant booleanConstant) {
		next.visit(booleanConstant);
	}

	@Override
	public void visit(UnitVal unitVal) {
		next.visit(unitVal);
	}

	@Override
	public void visit(ClassDeclaration clsDeclaration) {
		next.visit(clsDeclaration);
	}

	@Override
	public void visit(New new1) {
		next.visit(new1);
	}

	@Override
	public void visit(LetExpr let) {
		next.visit(let);
	}

	@Override
	public void visit(DefDeclaration meth) {
		next.visit(meth);
	}

	@Override
	public void visit(TupleObject meth) {
		next.visit(meth);
	}

	@Override
	public void visit(TypeInstance typeInstance) {
		next.visit(typeInstance);
	}

	@Override
	public void visit(Assignment assignment) {
		next.visit(assignment);
	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {
		next.visit(interfaceDeclaration);
	}

	@Override
	public void visit(Sequence sequence) {
		next.visit(sequence);
	}

	@Override
	public void visit(IfExpr ifExpr) {
		next.visit(ifExpr);
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		next.visit(whileStatement);
	}

}
