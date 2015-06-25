package wyvern.tools.parsing.coreparser;

import java.net.URI;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.types.Type;

public interface ASTBuilder<AST,Type> {
	public AST sequence(AST t1, AST t2, boolean inModule);
	public AST moduleDecl(String name, AST ast, FileLocation loc);
	public AST importDecl(URI uri, FileLocation loc);
	/** if type is null, it will be inferred*/
	public AST valDecl(String name, Type type, AST exp, FileLocation loc);
	public AST defDecl(String name, Type type, List args, AST body, boolean isClassDef, FileLocation loc);
	public Object formalArg(String name, Type type);
	public AST fn(List args, AST body);
	public AST var(String name, FileLocation loc);
	public Type nominalType(String name);
	public AST stringLit(String value);
	public AST integerLit(int value);
	public AST invocation(AST receiver, String name, AST argument, FileLocation loc);
	public AST application(AST function, AST arguments, FileLocation loc);
	public AST unitValue(FileLocation loc);
	public AST tuple(List<AST> members);
}
