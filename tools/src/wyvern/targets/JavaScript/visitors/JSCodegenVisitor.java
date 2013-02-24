package wyvern.targets.JavaScript.visitors;

import java.util.LinkedList;
import java.util.Stack;

import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.Assignment;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.extensions.BooleanConstant;
import wyvern.tools.typedAST.extensions.ClassDeclaration;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.LetExpr;
import wyvern.tools.typedAST.extensions.Meth;
import wyvern.tools.typedAST.extensions.New;
import wyvern.tools.typedAST.extensions.StringConstant;
import wyvern.tools.typedAST.extensions.TupleObject;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Int;

public class JSCodegenVisitor extends BaseASTVisitor {
	private class ASTElement {
		public String generated;
		public CoreAST elem;
		
		public ASTElement(CoreAST elem, String generated) {
			this.generated = generated;
			this.elem = elem;
		}
	}
	
	public String Indent(String input) {
		
		return input.replaceAll("\n", "\n\t");
	}
	
	//Checks to see if the type can be represented without explicit boxing
	//Important for simple operators, an int object won't have an add method.
	private boolean isRawType(Type type) {
		return (type instanceof Int) ||
				(type instanceof Bool) || 
				(type instanceof Str);
	}
	
	Stack<ASTElement> elemStack = new Stack<ASTElement>();
	boolean inClass = false, inBody = false;
	
	private boolean isInfix(String operator) {
		switch(operator) {
			case "+": return true;
			case "-": return true;
			case "*": return true;
			case "/": return true;
			case ">": return true;
			case "<": return true;
			case ">=":return true;
			case "<=":return true;
			case "==":return true;
			case "!=":return true;
			default: return false;
		}
	}
	
	public String getCode() {
		return elemStack.pop().generated;
	}
	
	
	@Override
	public void visit(Fn fn) {
		// TODO: support multiple arguments
		super.visit(fn);
		elemStack.push(new ASTElement(fn, "function("+fn.getArgBindings().get(0).getName()+") { return "+elemStack.pop().generated+"; }"));
	}

	@Override
	public void visit(Invocation invocation) {
		super.visit(invocation);
		
		ASTElement receiver = elemStack.pop();
		ASTElement argument = null;
		if (invocation.getArgument() != null)
			argument = elemStack.pop();

		String operationName = invocation.getOperationName();
		
		//If the first element can be operated on by native operators, then use them!
		//Also check to see if it the operator could work.
		//TODO: make something that checks this last bit better
		if (isRawType(receiver.elem.getType()) && argument != null && isInfix(operationName)) {
			elemStack.push(new ASTElement(invocation, receiver.generated+" "+operationName+" "+argument.generated));
			return;
		}
		
		if (argument != null) { //Fn call
			if (argument.elem instanceof UnitVal)
				elemStack.push(new ASTElement(invocation, receiver.generated+"."+operationName+"()"));
			else
				if (argument.elem instanceof TupleObject)
					elemStack.push(new ASTElement(invocation, receiver.generated+"."+operationName+argument.generated));
				else
					elemStack.push(new ASTElement(invocation, receiver.generated+"."+operationName+"("+argument.generated+")"));
			return;
		} else { //Var access
			elemStack.push(new ASTElement(invocation, receiver.generated+"."+operationName));
			return;
		}
	}

	@Override
	public void visit(Application application) {
		super.visit(application);
		
		ASTElement fnElem = elemStack.pop();
		ASTElement argElem = elemStack.pop();
		
		if (argElem.elem instanceof UnitVal)
			elemStack.push(new ASTElement(application, "("+fnElem.generated+")()"));
		else
			if (argElem.elem instanceof TupleObject)
				elemStack.push(new ASTElement(application, "("+fnElem.generated+")"+argElem.generated+""));
			else
				elemStack.push(new ASTElement(application, "("+fnElem.generated+")("+argElem.generated+")"));
	}

	@Override
	public void visit(ValDeclaration valDeclaration) {
		super.visit(valDeclaration);
		
		ASTElement nextDeclElem = (valDeclaration.getNextDecl() != null) ? elemStack.pop() : null;
		ASTElement declelem = elemStack.pop();
		
		// TODO SMELL: somewhat hackish
		String nextDeclText = (nextDeclElem == null)? "" : "\n" + nextDeclElem.generated;
		elemStack.push(new ASTElement(valDeclaration, 
				((inClass)?"this.":"var ")+valDeclaration.getBinding().getName() +" = "+declelem.generated +";" + nextDeclText));
	}

	@Override
	public void visit(IntegerConstant intConst) {
		super.visit(intConst);
		elemStack.push(new ASTElement(intConst, ""+intConst.getValue()));
	}

	@Override
	public void visit(StringConstant strConst) {
		super.visit(strConst);
		elemStack.push(new ASTElement(strConst, "\""+strConst.getValue() + "\""));
	}

	@Override
	public void visit(BooleanConstant booleanConstant) {
		super.visit(booleanConstant);
		elemStack.push(new ASTElement(booleanConstant, 
				""+booleanConstant.getValue()));
	}

	@Override
	public void visit(UnitVal unitVal) {
		super.visit(unitVal);
		elemStack.push(new ASTElement(unitVal, "null"));
	}

	@Override
	public void visit(Variable variable) {
		super.visit(variable);
		elemStack.push(new ASTElement(variable, variable.getName()));
	}

	@Override
	public void visit(New new1) {
		super.visit(new1);
		elemStack.push(new ASTElement(new1, "new "+new1.getClassDecl().getName()+"()"));
	}

	@Override
	public void visit(ClassDeclaration clsDeclaration) {
		Declaration decls = clsDeclaration.getDecls();
		
		if (!(decls instanceof CoreAST))
			throw new RuntimeException("All visited elements must implement CoreAST.");
		
		inClass = true;
		((CoreAST)decls).accept(this);
		inClass = false;
		
		if (clsDeclaration.getNextDecl() != null)
			((CoreAST) clsDeclaration.getNextDecl()).accept(this);
		
		ASTElement nextDeclElem = (clsDeclaration.getNextDecl() != null) ? elemStack.pop() : null;
		
		StringBuilder textRep = new StringBuilder();
		textRep.append("function ");
		textRep.append(clsDeclaration.getName());
		textRep.append("() {");
		
		StringBuilder declBuilder = new StringBuilder();
		declBuilder.append('\n');

		ASTElement decl = elemStack.pop();
		declBuilder.append(decl.generated);
		
		textRep.append(Indent(declBuilder.toString())+"\n");
		textRep.append("}\n");
		
		String nextDeclText = (nextDeclElem == null)? "" : nextDeclElem.generated;
		elemStack.push(new ASTElement(clsDeclaration, textRep.toString() + nextDeclText));
	}

	@Override
	public void visit(LetExpr let) {
		super.visit(let);
		
		inBody = true;
		ASTElement bodyelem = elemStack.pop();
		inBody = false;
		
		ASTElement declelem = elemStack.pop();
		
		//TODO: Quick hack to get it working
		if (bodyelem.elem instanceof LetExpr)
			elemStack.push(new ASTElement(let, 
					declelem.generated + "\n" + bodyelem.generated));
		else {
			elemStack.push(new ASTElement(let, 
					declelem.generated + "\n" + "return "+ bodyelem.generated + ";"));
		}
	}


	@Override
	public void visit(Meth meth) {
		super.visit(meth);
		

		ASTElement nextMethElem = (meth.getNextDecl() != null) ? elemStack.pop() : null;
		
		StringBuilder methodDecl = new StringBuilder();
		if (!inClass)
			methodDecl.append("function " + meth.getName() + "(");
		else
			methodDecl.append("this."+meth.getName()+" = function(");
		
		boolean first = true;
		for (NameBinding binding : meth.getArgBindings()) {
			if (!first)
				methodDecl.append(", ");
			methodDecl.append(binding.getName());
			first = false;
		}
		methodDecl.append(") {");
		ASTElement elem = elemStack.pop();
		
		
		//TODO: Quick hack to get it working
		if (elem.elem instanceof LetExpr)
			methodDecl.append(Indent("\n"+elem.generated));
		else {
			methodDecl.append(Indent("\n"+"return "+elem.generated + ";"));
		}
		
		methodDecl.append("\n}");
		

		String nextMethText = (nextMethElem == null)? "" : "\n" + nextMethElem.generated;
		elemStack.push(new ASTElement(meth, methodDecl.toString() + nextMethText));
	}
	
	@Override
	public void visit(TupleObject tuple) {
		super.visit(tuple);
		
		StringBuilder tupleStr = new StringBuilder(2*tuple.getObjects().length + 2);
		tupleStr.append("(");
		
		LinkedList<String> args = new LinkedList<String>();
		for (int i = 0; i < tuple.getObjects().length; i++) {
			args.push(elemStack.pop().generated);
		}
		for (int i = 0; i < tuple.getObjects().length; i++) {
			tupleStr.append(((i > 0) ? ", " : "") + args.pollFirst());
		}
		tupleStr.append(")");
		
		elemStack.push(new ASTElement(tuple, tupleStr.toString()));
	}
	
	public void visit(JSFunction jsfunction) {
		elemStack.push(new ASTElement(jsfunction, jsfunction.getName()));
	}

}
