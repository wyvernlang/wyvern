package wyvern.stdlib.support;

import java.util.Arrays;
import java.util.LinkedList;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.Literal;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.interop.FObject;
import wyvern.tools.interop.JObject;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

public class AST {
	public static AST utils = new AST();
	
	public ValueType intType() {
		return Util.intType();
	}

    public ValueType dynType() {
        return Util.dynType();
	}

	public Expression intLiteral(int i) {
		return new IntegerLiteral(i);
	}
	
	public Value stringLiteral(String s) {
		return new StringLiteral(s);
	}
	
	public Expression variable(String s) {
		return new Variable(s);
	}
	
	public Expression oneArgCall(ObjectValue receiver, String methodName, ObjectValue argument) {
		return new MethodCall(getExpr(receiver), methodName, Arrays.asList(getExpr(argument)), null);
	}
	
	public Expression oneDeclObject(ObjectValue decl) {
		final JavaValue fieldValue = (JavaValue) decl.getField("decl");
		NamedDeclaration realDecl = (NamedDeclaration) fieldValue.getWrappedValue();
		return new New(realDecl);
	}

    public Expression twoDeclObject(ObjectValue decl1, ObjectValue decl2) {
        final JavaValue fieldValue1 = (JavaValue) decl1.getField("decl");
        NamedDeclaration realDecl1 = (NamedDeclaration) fieldValue1.getWrappedValue();
        final JavaValue fieldValue2 = (JavaValue) decl2.getField("decl");
        NamedDeclaration realDecl2 = (NamedDeclaration) fieldValue2.getWrappedValue();
        return new New(realDecl1, realDecl2);
    }
	
	private Expression getExpr(ObjectValue wyvernAST) {
		final Value ast = wyvernAST.getField("ast");
		if (ast instanceof JavaValue) {
			final JavaValue value = (JavaValue) ast;
			return (Expression) value.getWrappedValue();
		} else if (ast instanceof Literal) {
			return (Expression) ast;
		} else {
			throw new RuntimeException("unexpected!");
		}
	}
	
	public DefDeclaration OneArgDefn(String name, ObjectValue resultType, ObjectValue body) {
		final JavaValue fieldValue = (JavaValue) resultType.getField("typ");
		ValueType realType= (ValueType) fieldValue.getWrappedValue();
		Expression realBody = getExpr(body);
		return new DefDeclaration(name, new LinkedList<FormalArg>(), realType, realBody, null);
	}

    public IExpr parseExpression(String input) throws ParseException {
        System.out.println("parseExpression recieved input '"+input+"'");
        ExpressionAST ast = (ExpressionAST)TestUtil.getNewAST(input + "\n", "TSL Parse");
        // TODO: Handle InterpreterState/GenContext
        return ast.generateIL(Globals.getStandardGenContext(), null, null);
    }
}
