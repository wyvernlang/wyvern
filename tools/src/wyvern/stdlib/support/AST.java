package wyvern.stdlib.support;

import java.util.LinkedList;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.interop.FObject;
import wyvern.tools.interop.JObject;

public class AST {
	public static AST utils = new AST();
	
	public ValueType intType() {
		return Util.intType();
	}
	
	public Value intLiteral(int i) {
		return new IntegerLiteral(i);
	}
	
	public Expression oneDeclObject(ObjectValue decl) {
		final JavaValue fieldValue = (JavaValue) decl.getField("decl");
		NamedDeclaration realDecl = (NamedDeclaration) fieldValue.getWrappedValue();
		return new New(realDecl);
	}
	
	public DefDeclaration OneArgDefn(String name, ObjectValue resultType, ObjectValue body) {
		final JavaValue fieldValue = (JavaValue) resultType.getField("typ");
		ValueType realType= (ValueType) fieldValue.getWrappedValue();
		final Value ast = body.getField("ast");
		Expression realBody = null;
		if (ast instanceof JavaValue) {
			final JavaValue bodyValue = (JavaValue) ast;
			realBody= (Expression) bodyValue.getWrappedValue();
		} else if (ast instanceof IntegerLiteral) {
			realBody = (Expression) ast;
		} else {
			throw new RuntimeException("unexpected!");
		}
		return new DefDeclaration(name, new LinkedList<FormalArg>(), realType, realBody, null);
	}
}
