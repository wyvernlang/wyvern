package wyvern.targets.JavaScript.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Stack;

import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
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
	String className = null;
	
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
		
		if (valDeclaration.getDefinition() != null) {
			ASTElement declelem = elemStack.pop();
			elemStack.push(new ASTElement(valDeclaration, 
					((inClass)?className+".prototype.":"var ")+valDeclaration.getBinding().getName() +" = "+declelem.generated +";"));
		} else {
			//Nothing, just add the variable at runtime
			elemStack.push(new ASTElement(valDeclaration, ""));
		}
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
		
		if (new1.getArgs().isEmpty())
			elemStack.push(new ASTElement(new1, "new "+new1.getClassDecl().getName()+"()"));
		else {
			StringBuilder body = new StringBuilder("\nthis.__proto__ = new "+new1.getClassDecl().getName()+"();");
			
			int setSize = new1.getArgs().size();
			String[] reverser = new String[setSize];
			int idx = 1;
			for (Entry<String,TypedAST> elem : new1.getArgs().entrySet()) {
				reverser[setSize - (idx++)] = (elemStack.pop().generated);
			}
			
			idx = 0;
			for (Entry<String,TypedAST> elem : new1.getArgs().entrySet()) {
				body.append("\nthis."+elem.getKey() +" = "+reverser[idx++] +";");
			}
			elemStack.push(new ASTElement(new1, "new function() {"+Indent(body.toString()) + "\n}"));
		}
	}

	@Override
	public void visit(ClassDeclaration clsDeclaration) {
		DeclSequence decls = clsDeclaration.getDecls();
		
		boolean lic = inClass;
		String oldClassName = className;
		inClass = true;
		className = clsDeclaration.getName();
		((CoreAST)decls).accept(this);
		className = oldClassName;
		inClass = lic;
		
		
		if (clsDeclaration.getNextDecl() != null)
			((CoreAST) clsDeclaration.getNextDecl()).accept(this);
		
		ASTElement nextDeclElem = (clsDeclaration.getNextDecl() != null) ? elemStack.pop() : null;
		
		StringBuilder textRep = new StringBuilder();
		textRep.append("function ");
		textRep.append(clsDeclaration.getName());
		textRep.append("() {}\n");
		
		elemStack.push(new ASTElement(clsDeclaration, textRep.toString() + elemStack.pop().generated));
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
	public void visit(MethDeclaration meth) {
		super.visit(meth);
		

		ASTElement nextMethElem = (meth.getNextDecl() != null) ? elemStack.pop() : null;
		
		StringBuilder methodDecl = new StringBuilder();
		if (!inClass)
			methodDecl.append("function " + meth.getName() + "(");
		else
			if (!meth.isClassMeth())
				methodDecl.append(className+".prototype."+meth.getName()+" = function(");
			else
				methodDecl.append(className+"."+meth.getName()+" = function(");
		
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
		if (elem.elem instanceof Sequence)
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
	
	@Override
	public void visit(Assignment assignment) {
		super.visit(assignment);
		ASTElement value = elemStack.pop();
		ASTElement target = elemStack.pop();
		elemStack.push(new ASTElement(assignment, target.generated + " = " + value.generated + ";"));
	}

	// TODO: Ben, please implement, though nothing much to do here at this stage...

	@Override
	public void visit(TypeInstance typeInstance) {
		super.visit(typeInstance);
	}

	@Override
	public void visit(VarDeclaration varDeclaration) {
		super.visit(varDeclaration);
		
		if (varDeclaration.getDefinition() != null) {
			ASTElement declelem = elemStack.pop();
			elemStack.push(new ASTElement(varDeclaration, 
					((inClass)?className+".prototype.":"var ")+varDeclaration.getBinding().getName() +" = "+declelem.generated +";"));
		} else {
			//Nothing, just add the variable at runtime
			elemStack.push(new ASTElement(varDeclaration, ""));
		}
	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {
		super.visit(interfaceDeclaration);
		//Not needed
	}
	
	@Override
	public void visit(PropDeclaration propDeclaration) {
		super.visit(propDeclaration);
	}
	
	@Override
	public void visit(Sequence sequence) {
		super.visit(sequence);
		
		Iterator<TypedAST> elems = sequence.iterator();
		StringBuilder declString = new StringBuilder();
		LinkedList<ASTElement> args = new LinkedList<ASTElement>();
		while (elems.hasNext()) {
			TypedAST elem = elems.next();
			args.push(elemStack.pop());
		}

		Iterator<ASTElement> elemStrs = args.iterator();
		while (elemStrs.hasNext()) {
			ASTElement str = elemStrs.next();
			String out = str.generated;
			if (!elemStrs.hasNext() && !(sequence instanceof DeclSequence))
				out = "return "+str.generated+";";
			else if (elemStrs.hasNext() && !(str.elem instanceof Sequence) && !(str.elem instanceof ClassDeclaration) && !str.generated.isEmpty())
				out = str.generated + "\n";
			declString.append(out);
		}
		
		elemStack.push(new ASTElement(sequence, declString.toString()+"\n"));
	}
}
