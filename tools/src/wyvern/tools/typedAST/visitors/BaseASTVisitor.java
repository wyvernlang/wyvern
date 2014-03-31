package wyvern.tools.typedAST.visitors;

import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
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

import java.util.Map.Entry;

public abstract class BaseASTVisitor implements CoreASTVisitor {

	@Override
	public void visit(TupleObject tuple) {
		for (TypedAST ast : tuple.getObjects()) {
			if (!(ast instanceof CoreAST))
				throw new RuntimeException("Something went wrong!");
			((CoreAST)ast).accept(this);
		}
	}
	
	@Override
	public void visit(Fn fn) {
		if (fn.getBody() instanceof CoreAST)
			((CoreAST)fn.getBody()).accept(this);
	}

	@Override
	public void visit(Invocation invocation) {
		TypedAST argument = invocation.getArgument();
		TypedAST receiver = invocation.getReceiver();
		
		if (argument instanceof CoreAST)
			((CoreAST) argument).accept(this);
		if (receiver instanceof CoreAST)
			((CoreAST) receiver).accept(this);
	}

	@Override
	public void visit(Application application) {
		TypedAST argument = application.getArgument();
		TypedAST function = application.getFunction();
		
		if (argument instanceof CoreAST)
			((CoreAST) argument).accept(this);
		if (function instanceof CoreAST)
			((CoreAST) function).accept(this);

	}

	@Override
	public void visit(ValDeclaration valDeclaration) {
		TypedAST definition = valDeclaration.getDefinition();
		
		if (definition instanceof CoreAST)
			((CoreAST) definition).accept(this);
	}

	@Override
	public void visit(VarDeclaration varDeclaration) {
		TypedAST definition = varDeclaration.getDefinition();
		
		if (definition instanceof CoreAST)
			((CoreAST) definition).accept(this);
	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {
        DeclSequence decls = interfaceDeclaration.getDecls();

        if (!(decls instanceof CoreAST))
            throw new RuntimeException("All visited elements must implement CoreAST.");
        ((CoreAST)decls).accept(this);
	}

	@Override
	public void visit(TypeInstance typeInstance) {
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
		DeclSequence decls = clsDeclaration.getDecls();
		
		if (!(decls instanceof CoreAST))
			throw new RuntimeException("All visited elements must implement CoreAST.");
		((CoreAST)decls).accept(this);
	}

	@Override
	public void visit(New new1) {
		for (Entry<String,TypedAST> elem : new1.getArgs().entrySet()) {
			((CoreAST)elem.getValue()).accept(this);
		}
	}

	@Override
	public void visit(LetExpr let) {
	}

	@Override
	public void visit(DefDeclaration meth) {
		TypedAST body = meth.getBody();

		if (body == null)
			return;

		if (!(body instanceof CoreAST))
			throw new RuntimeException("Codegenerated elements must implement CoreAST");
		
		((CoreAST)body).accept(this);
		
		if (meth.getNextDecl() != null)
			((CoreAST) meth.getNextDecl()).accept(this);

	}

	@Override
	public void visit(Assignment assignment) {
		TypedAST target = assignment.getTarget();
		TypedAST value = assignment.getValue();
		if (target instanceof CoreAST)
			((CoreAST) target).accept(this);
		if (value instanceof CoreAST)
			((CoreAST) value).accept(this);
		
	}


	@Override
	public void visit(Sequence sequence) {
		for (TypedAST elem : sequence) {
			if (!(elem instanceof CoreAST))
				throw new RuntimeException();
			((CoreAST)elem).accept(this);
		}
	}
	

	@Override
	public void visit(IfExpr ifExpr) {
		for (IfExpr.IfClause clause : ifExpr.getClauses()) {
			TypedAST exprClause = clause.getClause();
			TypedAST body = clause.getBody();

			((CoreAST)body).accept(this);
			((CoreAST)exprClause).accept(this);
		}
	}
	
	@Override
	public void visit(WhileStatement whileStatement) {
		TypedAST body = whileStatement.getBody();
		TypedAST conditional = whileStatement.getConditional();

		((CoreAST)body).accept(this);
		((CoreAST)conditional).accept(this);
	}

	@Override
	public void visit(ImportDeclaration id) {

	}

}
