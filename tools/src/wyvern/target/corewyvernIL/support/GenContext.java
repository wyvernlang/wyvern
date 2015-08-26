package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
	
	class ILMethod {
		String objName;
		wyvern.tools.typedAST.core.declarations.DefDeclaration defDecl;
		
		public ILMethod(String objName, wyvern.tools.typedAST.core.declarations.DefDeclaration defDecl) {
			this.objName = objName;
			this.defDecl = defDecl;
		}
		
		public String getObjName() {
			return objName;
		}
		public List<FormalArg> getArgsILType() {
			return defDecl.getArgILTypes();
		}
		public ValueType getReturnILType() {
			return defDecl.getReturnILType();
		}
	}
	
	private Map<String, ILMethod> methodBinding = new HashMap<String, ILMethod>();
	private Map<String, String> typeBinding = new HashMap<String, String>();
	
	public GenContext extend(String var, Expression expr, ValueType type) {
		return new VarGenContext(var, expr, type, this);
	}
	
	public abstract Expression lookupExp(String varName);
	
	public static GenContext empty() {
		return theEmpty;
	}
	
	abstract String endToString();
	
	private static GenContext theEmpty = new EmptyGenContext();

	public static String generateName() {
		return "var_"+(count++);
	} 
	private static int count = 0;

	public void rec(String newName, TypedAST ast) {
		System.out.println("rec" + ast.hashCode());
		if(ast instanceof TypeVarDecl) {
			String typeName = ((TypeVarDecl) ast).getName();
			typeBinding.put(typeName, newName);
		} else {
			assert (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration);
			wyvern.tools.typedAST.core.declarations.DefDeclaration methodDecl = (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
			String methodName = methodDecl.getName();
			
			methodBinding.put(methodName, new ILMethod(newName, methodDecl));
		}
		return;
	}

	public List<wyvern.target.corewyvernIL.decl.Declaration> genDeclSeq() {
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = 
				new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(Map.Entry<String, ILMethod> entry : methodBinding.entrySet()) {
			String methodName = entry.getKey();
			ILMethod method = entry.getValue();
			List<Expression> args = new LinkedList<Expression>();
			for(FormalArg arg : method.getArgsILType()) {
				args.add(new Variable(arg.getName()));
			}
			Expression body = new MethodCall(new Variable(method.getObjName()), methodName, args);
			
			DefDeclaration decl = new DefDeclaration(methodName, method.getArgsILType(), method.getReturnILType(), body);
			decls.add(decl);
		}
		
		for(Map.Entry<String, String> entry : typeBinding.entrySet()) {
			String typeName = entry.getKey();
			String objName = entry.getValue();
			TypeDeclaration decl = new TypeDeclaration(typeName, new NominalType(objName, typeName));
			decls.add(decl);
		}
		

		return decls;
	} 
	public List<DeclType> genDeclTypeSeq() {
		List<DeclType> declts = new LinkedList<DeclType>();
		
		for(Map.Entry<String, ILMethod> entry : methodBinding.entrySet()) {
			String methodName = entry.getKey();
			ILMethod method = entry.getValue();
			List<Expression> args = new LinkedList<Expression>();
			for(FormalArg arg : method.getArgsILType()) {
				args.add(new Variable(arg.getName()));
			}
			
			DeclType declt = new DefDeclType(methodName, method.getReturnILType(), method.getArgsILType() );
			declts.add(declt);
		}
		
		for(Map.Entry<String, String> entry : typeBinding.entrySet()) {
			String typeName = entry.getKey();
			String objName = entry.getValue();
			DeclType declt = null; // to be implemented
			declts.add(declt);
		}
		return declts;
	}
	
	public String getMethod(String methodName) {
		return methodBinding.get(methodName).getObjName();
	}
	
	public String getType(String typeName) {
		return typeBinding.get(typeName);
	}
}
