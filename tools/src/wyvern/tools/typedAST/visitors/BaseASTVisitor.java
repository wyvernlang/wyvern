package wyvern.tools.typedAST.visitors;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.MethDeclaration;
import wyvern.tools.typedAST.core.declarations.PropDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.TypeInstance;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;

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
		if (valDeclaration.getNextDecl() != null)
			((CoreAST) valDeclaration.getNextDecl()).accept(this);
	}

	@Override
	public void visit(PropDeclaration propDeclaration) {
	}

	@Override
	public void visit(VarDeclaration varDeclaration) {
	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {
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
	}

	@Override
	public void visit(LetExpr let) {
	}

	@Override
	public void visit(MethDeclaration meth) {
		TypedAST body = meth.getBody();
		
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

}
