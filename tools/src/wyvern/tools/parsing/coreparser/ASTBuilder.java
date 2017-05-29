package wyvern.tools.parsing.coreparser;

import java.net.URI;
import java.util.List;

import wyvern.tools.errors.FileLocation;

public interface ASTBuilder<AST,Type> {
	public AST sequence(AST t1, AST t2, boolean inModule);
	
	public AST moduleDecl(String name, AST ast, FileLocation loc, boolean isResource);
	public AST importDecl(URI uri, FileLocation loc, Token name, boolean isRequire, boolean isMetadata);
	/** if type is null, it will be inferred*/
	public AST valDecl(String name, Type type, AST exp, FileLocation loc);
	public AST varDecl(String name, Type type, AST exp, FileLocation loc);
	public AST defDecl(String name, Type type, List<String> generics, List args, AST body, boolean isClassDef, FileLocation loc);
	public AST typeDecl(String name, AST body, Object tagInfo, AST metadata, FileLocation loc, boolean isResource, String selfName);
	public AST delegateDecl(Type type, AST exp, FileLocation loc);
	
	public AST defDeclType(String name, Type type, List<String> generics, List args, FileLocation loc);
	public AST valDeclType(String name, Type type, FileLocation loc);
	public AST varDeclType(String name, Type type, FileLocation loc);
	public AST typeAbbrevDecl(String alias, Type reference, FileLocation loc);
	public AST typeAbbrevDecl(String alias, Type reference, AST metadata, FileLocation loc);
	
	public Object formalArg(String name, Type type);
	public AST fn(List args, AST body, FileLocation loc);
	public AST var(String name, FileLocation loc);
	public AST stringLit(String value, FileLocation loc);
	public AST integerLit(int value, FileLocation loc);
	public AST booleanLit(boolean value, FileLocation loc);
	public AST invocation(AST receiver, String name, AST argument, FileLocation loc);
	public AST application(AST function, AST arguments, FileLocation loc, List<Type> generics);
	public AST assignment(AST lhs, AST rhs, FileLocation loc);
	public AST unitValue(FileLocation loc);
	public AST newObj(FileLocation loc, String selfName);
	public AST dsl(FileLocation loc);
	public AST tuple(List<AST> members, FileLocation loc);
	public AST match(AST exp, List cases, FileLocation loc);
	
	public Object caseArm(String name, Type type, AST exp, FileLocation loc);
	public Object tagInfo(Type type, List<Type> comprises);
	
	public Type nominalType(String name, FileLocation loc);
	public Type arrowType(Type argument, Type result);
	public Type parameterizedType(Type base, List<Type> arguments, FileLocation loc);
	
	public Type qualifiedType(AST base, String name);
	
	public void setNewBody(AST newExp, AST decls);
	public void setDSLBody(AST dslExp, String text);

	public AST instantiation(URI uri, AST arg, Token name, FileLocation loc);

}
