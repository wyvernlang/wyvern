package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class FieldSet extends Expression {

	private IExpr objectExpr;
	private String fieldName;
	private Expression exprToAssign;

	public FieldSet(ValueType exprType, IExpr objectExpr,
			String fieldName, Expression exprToAssign) {
		super(exprType);
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
		this.exprToAssign = exprToAssign;
	}

	public IExpr getObjectExpr() {
		return objectExpr;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Expression getExprToAssign() {
		return exprToAssign;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {

		// Figure out types of object and expression.
		ValueType valTypeExpr = exprToAssign.typeCheck(ctx);
		StructuralType structTypeObj = objectExpr.typeCheck(ctx).getStructuralType(ctx);

		// Figure out the type of the field.
		DeclType declTypeField = structTypeObj.findDecl(fieldName, ctx);
		if (!(declTypeField instanceof VarDeclType))
			ToolError.reportError(ErrorMessage.CANNOT_BE_ASSIGNED, this,
								  declTypeField.getName());
		ValueType valTypeField = ((VarDeclType) declTypeField).getResultType(View.from(objectExpr, ctx));

		// Make sure assigned type is compatible with the field's type.
		if (!valTypeExpr.isSubtypeOf(valTypeField, ctx))
			ToolError.reportError(ErrorMessage.ASSIGNMENT_SUBTYPING, this);
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

		// find the declaration corresponding to the field
		Declaration decl = object.findDecl(fieldName);
		if (decl == null)
			throw new RuntimeException("Runtime error: trying to set the undeclared field " + fieldName);
		if (!(decl instanceof wyvern.target.corewyvernIL.decl.VarDeclaration))
			throw new RuntimeException("Expected assignment to var field in field set.");
		VarDeclaration varDecl = (VarDeclaration) decl;
		Value exprInterpreted = exprToAssign.interpret(ctx);
		VarDeclaration varDeclUpdated = null;

		// Evaluate the expression in the current context. Update the declaration.
		// VarDeclaration's constructor needs to take an expression, not a value.
		// TODO: is this an exhaustive case analysis?
		if (exprInterpreted instanceof AbstractValue) {
			varDeclUpdated = new VarDeclaration(fieldName, varDecl.getType(), (AbstractValue)exprInterpreted);
		}
		else {
			ToolError.reportError(ErrorMessage.ASSIGNMENT_SUBTYPING, this);
		}

		// Update object's declarations.
		object.setDecl(varDeclUpdated);
		return object;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		objectExpr.doPrettyPrint(dest,indent);
		dest.append('.').append(fieldName);
		dest.append(" = ");
		exprToAssign.doPrettyPrint(dest,indent);
	}

	@Override
	public Set<String> getFreeVariables() {
		Set<String> freeVars = objectExpr.getFreeVariables();
		freeVars.addAll(exprToAssign.getFreeVariables());
		return freeVars;
	}

}
