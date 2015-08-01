package wyvern.target.corewyvernIL.decl;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class DefDeclaration extends Declaration {

	private String methodName;
	private List<FormalArg> formalArgs;
	private ValueType type;
	private Expression body;
	
	public DefDeclaration(String methodName, List<FormalArg> formalArgs,
			ValueType type, Expression body) {
		super();
		this.methodName = methodName;
		this.formalArgs = formalArgs;
		this.type = type;
		this.body = body;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
	public DeclType typeCheck(TypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
