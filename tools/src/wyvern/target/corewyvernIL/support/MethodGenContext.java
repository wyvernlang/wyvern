package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class MethodGenContext extends GenContext {

	ILMethod method;
	String objectName;
	String methodName;
	GenContext genContext;
	
	public MethodGenContext(String methodName, ILMethod method, GenContext genContext) {
		this.method = method;
		this.objectName = method.getObjName();
		this.methodName = methodName;
		this.genContext = genContext;
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return methodName + " = " + objectName + '.' + methodName +  ", " + genContext.endToString();
	}

	@Override
	public ValueType lookup(String varName) {
		return genContext.lookup(varName);
	}

	@Override
	public String getType(String varName) {
		return genContext.getType(varName);
	}

	// TODO: this whole approach here is kind of hacky
	@Override
	public List<Declaration> genDeclSeq(GenContext origCtx) {
		List<Declaration> decls = genContext == origCtx ? new LinkedList<Declaration>():genContext.genDeclSeq(origCtx);
		List<Expression> args = new LinkedList<Expression>();
		for(FormalArg arg : method.getArgsILType()) {
			args.add(new Variable(arg.getName()));
		}
		Expression body = new MethodCall(new Variable(method.getObjName()), methodName, args);
		
		DefDeclaration decl = new DefDeclaration(methodName, method.getArgsILType(), method.getReturnILType(), body);
		decls.add(decl);
		return decls;
	}

	// TODO: this whole approach here is kind of hacky
	@Override
	public List<DeclType> genDeclTypeSeq(GenContext origCtx) {
		List<DeclType> declts = genContext == origCtx ? new LinkedList<DeclType>():genContext.genDeclTypeSeq(origCtx);
		List<Expression> args = new LinkedList<Expression>();
		for(FormalArg arg : method.getArgsILType()) {
			args.add(new Variable(arg.getName()));
		}
		DeclType declt = new DefDeclType(methodName, method.getReturnILType(), method.getArgsILType() );
		declts.add(declt);
		return declts;
	}

	/*@Override
	public Expression lookupExp(String varName) {
		return genContext.lookupExp(varName);
	}*/

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (this.methodName.equals(varName))
			return new InvocationExprGenerator(new Variable(method.getObjName()), varName, origCtx);
		else
			return genContext.getCallableExprRec(varName, origCtx);
	}

	
	@Override
	public ValueType getAliasType(String aliasName) {
		return genContext.getAliasType(aliasName);
	}

}
