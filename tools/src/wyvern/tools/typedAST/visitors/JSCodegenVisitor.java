package wyvern.tools.typedAST.visitors;

import java.util.Stack;

import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.extensions.BooleanConstant;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.StringConstant;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Int;

public class JSCodegenVisitor implements CoreASTVisitor {
	private class ASTElement {
		public String generated;
		public CoreAST elem;
		
		public ASTElement(CoreAST elem, String generated) {
			this.generated = generated;
			this.elem = elem;
		}
	}
	
	//Checks to see if the type can be reperesented without explicit boxing
	//Important for simple operators, an int object won't have an add method.
	private boolean isRawType(Type type) {
		return (type instanceof Int) ||
				(type instanceof Bool) || 
				(type instanceof Str);
	}
	
	Stack<ASTElement> elemStack = new Stack<ASTElement>();
	
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
		elemStack.push(new ASTElement(fn, "function("+fn.getBinding().getName()+") { return "+elemStack.pop().generated+"; }"));
	}

	@Override
	public void visit(Invocation invocation) {
		ASTElement elem1 = elemStack.pop();
		ASTElement elem2 = elemStack.pop();

		String operationName = invocation.getOperationName();
		
		//If the first element can be operated on by native operators, then use them!
		//Also check to see if it the operator could work.
		//TODO: make something that checks this last bit better
		if (isRawType(elem1.elem.getType()) && isInfix(operationName)) {
			elemStack.push(new ASTElement(invocation, elem1.generated+" "+operationName+" "+elem2.generated));
			return;
		}
		elemStack.push(new ASTElement(invocation, elem1.generated+"."+operationName+"("+elem2.generated+")"));
	}

	@Override
	public void visit(Application application) {
		System.out.println("Apply "+application.getFunction().toString());
		
		ASTElement fnElem = elemStack.pop();
		ASTElement argElem = elemStack.pop();
		

		elemStack.push(new ASTElement(application, "("+fnElem.generated+")("+argElem.generated+")"));
	}

	@Override
	public void visit(ValDeclaration valDeclaration) {
		System.out.println("Val decl "+valDeclaration.toString());
		ASTElement bodyelem= elemStack.pop();
		ASTElement declelem = elemStack.pop();
		
		
		elemStack.push(new ASTElement(valDeclaration, 
				"var "+valDeclaration.getBinding().getName() +" = "+declelem.generated +";\n"
				+bodyelem.generated));
	}

	@Override
	public void visit(IntegerConstant intConst) {
		elemStack.push(new ASTElement(intConst, ""+intConst.getValue()));
	}

	@Override
	public void visit(StringConstant strConst) {
		elemStack.push(new ASTElement(strConst, ""+strConst.getValue()));
	}

	@Override
	public void visit(BooleanConstant booleanConstant) {
		elemStack.push(new ASTElement(booleanConstant, 
				""+booleanConstant.getValue()));
	}

	@Override
	public void visit(UnitVal unitVal) {
		elemStack.push(new ASTElement(unitVal, "null"));
	}

	@Override
	public void visit(Variable variable) {
		elemStack.push(new ASTElement(variable, variable.getName()));
	}

}
