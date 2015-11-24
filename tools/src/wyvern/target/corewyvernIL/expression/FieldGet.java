package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DeclTypeWithResult;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class FieldGet extends Expression implements Path {

	private Expression objectExpr;
	private String fieldName;
	
	public FieldGet(Expression objectExpr, String fieldName) {
		super();
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
	}

	public Expression getObjectExpr() {
		return objectExpr;
	}
	
	public void setObjectExpr(Expression objectExpr) {
		this.objectExpr = objectExpr;
	}
	
	public String getName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public String toString() {
		return objectExpr.toString() + "." + fieldName;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		ValueType vt = objectExpr.typeCheck(ctx);
		DeclType dt = vt.findDecl(fieldName, ctx);
		if (dt == null)
			throw new RuntimeException("typechecking error: operation not found");
		if (!(dt instanceof ValDeclType || dt instanceof VarDeclType))
			throw new RuntimeException("typechecking error: can't treat a method or type member as a field");
		this.setExprType(((DeclTypeWithResult)dt).getResultType(View.from(objectExpr, ctx)));
		return getExprType();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Value receiver = objectExpr.interpret(ctx);
		if (!(receiver instanceof Invokable))
			throw new RuntimeException("expected an object value at field get");
		Invokable ov = (Invokable) receiver;
		return ov.getField(fieldName, ctx);
 	}

	@Override
	public Path adapt(View v) {
		if (!(objectExpr instanceof Path))
			throw new RuntimeException("tried to adapt something that's not a path or type");
		return new FieldGet((Expression)((Path)objectExpr).adapt(v), fieldName);
	}
}