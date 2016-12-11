package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class FieldSet extends Expression {

	private IExpr objectExpr;
	private String fieldName;
	private IExpr exprToAssign;

	public FieldSet(ValueType exprType, IExpr objectExpr,
			String fieldName, IExpr exprToAssign) {
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

	public IExpr getExprToAssign() {
		return exprToAssign;
	}

	private boolean settingDynamicObject(TypeContext ctx) {
		ValueType receiverType = null;
		if (objectExpr instanceof FieldGet) {
			IExpr receiver = ((FieldGet)objectExpr).getObjectExpr();
			receiverType = receiver.typeCheck(ctx);
		} else if (objectExpr instanceof Variable) {
			receiverType = ctx.lookupTypeOf(((Variable)objectExpr).getName());
		} else {
		    throw new RuntimeException("Target of FieldSet is unsupported. Type: " + objectExpr.getClass());
		}
		return Util.isDynamicType(receiverType);
	}
	
	@Override
	public ValueType typeCheck(TypeContext ctx) {

	    // Setting the field of a dynamic object.
		if (settingDynamicObject(ctx)) {
		    exprToAssign.typeCheck(ctx);
		    return Util.unitType();
		}
		
		// Figure out types of object and expression.
		StructuralType varTypeStructural = objectExpr.typeCheck(ctx).getStructuralType(ctx);
		ValueType varTypeExpr = exprToAssign.typeCheck(ctx);

		// Figure out the type of the field.
		DeclType declTypeField = varTypeStructural.findDecl(fieldName, ctx);
		if (declTypeField == null)
			ToolError.reportError(ErrorMessage.NO_SUCH_FIELD, this, fieldName);
		if (!(declTypeField instanceof VarDeclType))
			ToolError.reportError(ErrorMessage.CANNOT_BE_ASSIGNED, this,
								  declTypeField.getName());
		ValueType valTypeField = ((VarDeclType) declTypeField).getResultType(View.from(objectExpr, ctx));

		// Make sure assigned type is compatible with the field's type.
		if (!varTypeExpr.isSubtypeOf(valTypeField, ctx))
			ToolError.reportError(ErrorMessage.ASSIGNMENT_SUBTYPING, this);
		return Util.unitType();
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
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
		
		// Evaluate the expression in the current context. Update the declaration.
		Value exprInterpreted = exprToAssign.interpret(ctx);
		VarDeclaration varDeclUpdated = null;
		varDeclUpdated = new VarDeclaration(fieldName, varDecl.getType(), exprInterpreted, getLocation());

		// Update object's declarations.
		object.setDecl(varDeclUpdated);
		return Util.unitValue();
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
