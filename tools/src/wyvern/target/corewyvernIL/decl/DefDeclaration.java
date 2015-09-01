package wyvern.target.corewyvernIL.decl;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class DefDeclaration extends NamedDeclaration {

	private List<FormalArg> formalArgs;
	private ValueType type;
	private Expression body;
	
	public DefDeclaration(String methodName, List<FormalArg> formalArgs,
			ValueType type, Expression body) {
		super(methodName);
		this.formalArgs = formalArgs;
		if (type == null) throw new RuntimeException();
		this.type = type;
		this.body = body;
	}

	@Override
	public String toString() {
		return "DefDeclaration[" + getName() + "(...) : " + type + " = ...]";
	}
	
	public List<FormalArg> getFormalArgs() {
		return formalArgs;
	}
	
	public void setFormalArgs(List<FormalArg> formalArgs) {
		this.formalArgs = formalArgs;
	}
	
	public ValueType getType() {
		return type;
	}
	public void setType(ValueType type) {
		this.type = type;
	}
	
	public Expression getBody() {
		return body;
	}
	public void setBody(Expression body) {
		this.body = body;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		TypeContext methodCtx = thisCtx;
		for (FormalArg arg : formalArgs) {
			methodCtx = methodCtx.extend(arg.getName(), arg.getType());
		}
		if (!body.typeCheck(methodCtx).isSubtypeOf(getType(), methodCtx))
			throw new RuntimeException("body doesn't match declared type");
		return new DefDeclType(getName(), type, formalArgs);
	}
}
