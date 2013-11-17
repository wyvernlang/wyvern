package wyvern.targets.Common.WyvernIL;

import org.objectweb.asm.commons.StaticInitMerger;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben Chung on 11/11/13.
 */
public class ExnFromAST implements CoreASTVisitor {
	private List<Statement> statements = new ArrayList<Statement>();

	public List<Statement> getStatments() {
		return statements;
	}



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
	public void visit(ValDeclaration valDeclaration) {

	}

	@Override
	public void visit(VarDeclaration valDeclaration) {

	}

	@Override
	public void visit(Variable variable) {

	}

	@Override
	public void visit(IntegerConstant booleanConstant) {

	}

	@Override
	public void visit(StringConstant booleanConstant) {

	}

	@Override
	public void visit(BooleanConstant booleanConstant) {

	}

	@Override
	public void visit(UnitVal unitVal) {

	}

	@Override
	public void visit(ClassDeclaration clsDeclaration) {

	}

	@Override
	public void visit(New new1) {

	}

	@Override
	public void visit(LetExpr let) {

	}

	@Override
	public void visit(DefDeclaration meth) {

	}

	@Override
	public void visit(TupleObject meth) {

	}

	@Override
	public void visit(TypeInstance typeInstance) {

	}

	@Override
	public void visit(Assignment assignment) {

	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {

	}

	@Override
	public void visit(Sequence sequence) {
		Iterator<TypedAST> flatten = sequence.flatten();
		List<Statement> foo = new ArrayList<Statement>();

		while(flatten.hasNext()){
			TypedAST ast = flatten.next();

			if (!(ast instanceof CoreAST)) {
				throw new RuntimeException();
			}

			CoreAST cast = (CoreAST)ast;
			ExnFromAST visitor = new ExnFromAST();
			cast.accept(visitor);

			foo.addAll(visitor.getStatments());
		}

		this.statements = foo;
	}

	@Override
	public void visit(IfExpr ifExpr) {

	}

	@Override
	public void visit(WhileStatement whileStatement) {

	}
}
