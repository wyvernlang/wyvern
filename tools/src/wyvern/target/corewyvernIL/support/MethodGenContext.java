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
	String methodName;
	GenContext genContext;
	
	public MethodGenContext(String methodName, ILMethod method, GenContext genContext) {
		this.method = method;
		this.methodName = methodName;
		this.genContext = genContext;
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return methodName + " : " + method.getObjName() +  ", " + genContext.endToString();
	}

	@Override
	public ValueType lookup(String varName) {
		return genContext.lookup(varName);
	}

	@Override
	public String getType(String varName) {
		return genContext.getType(varName);
	}

	@Override
	public ILMethod getMethod(String varName) {
		if(this.methodName == varName) return method;
		else return genContext.getMethod(varName);
	}

	@Override
	public List<Declaration> genDeclSeq() {
		List<Declaration> decls = genContext.genDeclSeq();
		List<Expression> args = new LinkedList<Expression>();
		for(FormalArg arg : method.getArgsILType()) {
			args.add(new Variable(arg.getName()));
		}
		Expression body = new MethodCall(new Variable(method.getObjName()), methodName, args);
		
		DefDeclaration decl = new DefDeclaration(methodName, method.getArgsILType(), method.getReturnILType(), body);
		decls.add(decl);
		return decls;
	}

	@Override
	public List<DeclType> genDeclTypeSeq() {
		List<DeclType> declts = genContext.genDeclTypeSeq();
		List<Expression> args = new LinkedList<Expression>();
		for(FormalArg arg : method.getArgsILType()) {
			args.add(new Variable(arg.getName()));
		}
		DeclType declt = new DefDeclType(methodName, method.getReturnILType(), method.getArgsILType() );
		declts.add(declt);
		return declts;
	}

	@Override
	public Expression lookupExp(String varName) {
		return genContext.lookupExp(varName);
	}

}
