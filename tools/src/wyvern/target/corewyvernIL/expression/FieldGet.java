package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DeclTypeWithResult;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class FieldGet extends Expression implements Path {

	private IExpr objectExpr;
	private String fieldName;

	public FieldGet(IExpr objectExpr, String fieldName) {
		super();
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		objectExpr.doPrettyPrint(dest,indent);
		dest.append('.').append(fieldName);
	}

	public IExpr getObjectExpr() {
		return objectExpr;
	}

	public String getName() {
		return fieldName;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		ValueType vt = objectExpr.typeCheck(ctx);
		DeclType dt = vt.findDecl(fieldName, ctx);
		if (dt == null)
			ToolError.reportError(ErrorMessage.VARIABLE_NOT_DECLARED, this, fieldName);
		if (!(dt instanceof ValDeclType || dt instanceof VarDeclType))
			ToolError.reportError(ErrorMessage.OPERATOR_DOES_NOT_APPLY, this, dt.getName());
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

	@Override
	public Set<String> getFreeVariables() {
		Set<String> freeVars = objectExpr.getFreeVariables();
		freeVars.add(fieldName);
		return freeVars;
	}
}