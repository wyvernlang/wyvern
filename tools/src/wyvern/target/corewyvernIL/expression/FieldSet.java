package wyvern.target.corewyvernIL.expression;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public class FieldSet extends Expression {

	private Expression objectExpr;
	private String fieldName;
	private Expression exprToAssign;
	
	public FieldSet(ValueType exprType, Expression objectExpr,
			String fieldName, Expression exprToAssign) {
		super(exprType);
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
		this.exprToAssign = exprToAssign;
	}

	public Expression getObjectExpr() {
		return objectExpr;
	}
	
	public void setObjectExpr(Expression objectExpr) {
		this.objectExpr = objectExpr;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public Expression getExprToAssign() {
		return exprToAssign;
	}
	
	public void setExprToAssign(Expression exprToAssign) {
		this.exprToAssign = exprToAssign;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		
		// Figure out types of object and expression.
		ValueType valTypeExpr = exprToAssign.typeCheck(ctx);
		StructuralType structTypeObj = objectExpr.typeCheck(ctx).getStructuralType(ctx);
		
		// Figure out the type of the field.
		DeclType declTypeField = structTypeObj.findDecl(fieldName, ctx);
		if (!(declTypeField instanceof VarDeclType))
			throw new RuntimeException("Type error: trying to set the field " + fieldName + ", but this isn't"
										+ "a var declaration.");
		ValueType valTypeField = ((VarDeclType) declTypeField).getRawResultType();
	
		// Make sure assigned type is compatible with the field's type.
		if (!valTypeExpr.isSubtypeOf(valTypeField, ctx))
			throw new RuntimeException("Type error: trying to set the field " + fieldName + ", but the type "
										+ valTypeExpr + " cannot be assigned to " + valTypeField);
		return valTypeExpr;
		
	}
	
	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		
		// Evaluate object whose field is being set.
		Value objExprVal = objectExpr.interpret(ctx);
		if (!(objExprVal instanceof ObjectValue))
			throw new RuntimeException("Runtime error: trying to set field of something which isn't an object.");
		ObjectValue object = (ObjectValue) objExprVal;
		
		// Check the object has such a field.
		if (object.getField(fieldName, ctx) == null)
			throw new RuntimeException("Runtime error: trying to set the undeclared field " + fieldName);	
		
		// Construct new list of declarations.
		List<Declaration> newDeclarations  = new LinkedList<>();
		for (Declaration decl : object.getDecls()) {
			if (!decl.getName().equals(fieldName)) {
				newDeclarations.add(decl);
			}
			else {
				if (!(decl instanceof wyvern.target.corewyvernIL.decl.VarDeclaration))
					throw new RuntimeException("Expected assignment to var field in field set.");
				VarDeclaration vDecl = (VarDeclaration) decl;
				VarDeclaration vDeclUpdated = new VarDeclaration(fieldName, vDecl.getType(), exprToAssign);
				newDeclarations.add(vDeclUpdated.interpret(ctx));
			}
		}
		
		// Update object's declarations.
		object.setDecls(newDeclarations);
		return object;
		
	}
	
	@Override
	public String toString() {
		return objectExpr.toString() + "." + fieldName + " = " + exprToAssign.toString();
	}
	
	
}
