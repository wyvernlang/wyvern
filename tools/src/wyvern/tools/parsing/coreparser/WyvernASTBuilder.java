package wyvern.tools.parsing.coreparser;

import java.net.URI;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class WyvernASTBuilder implements ASTBuilder<TypedAST, Type> {
	
    /* Weirdness: DeclSequence typechecks everything simultaneously without
     * extending the environment, unless it's inside a module.  So if we're
     * not inside a module, use Sequence instead.
     */
	@Override
    public TypedAST sequence(TypedAST t1, TypedAST t2, boolean inModule) {
    	if (inModule)
    		return DeclSequence.simplify(new DeclSequence(t1,t2));
    	else
    		return new Sequence(t1, t2);
    }
    
	@Override
	public TypedAST moduleDecl(String name, TypedAST ast, FileLocation loc) {
		return new ModuleDeclaration(name, (EnvironmentExtender)ast, loc);
	}

	@Override
	public TypedAST importDecl(URI uri, FileLocation loc) {
		return new ImportDeclaration(uri, loc);
	}

	@Override
	public TypedAST valDecl(String name, Type type, TypedAST exp, FileLocation loc) {
		return new ValDeclaration(name, type, exp, loc);
	}

	@Override
	public TypedAST defDecl(String name, Type type, List args,
			TypedAST body, boolean isClassDef, FileLocation loc) {
		return new DefDeclaration(name, type, args, body, isClassDef, loc);
	}

	@Override
	public Object formalArg(String name, Type type) {
		return new NameBindingImpl(name, type);
	}

	@Override
	public TypedAST fn(List args, TypedAST body) {
		return new Fn(args, body);
	}

	@Override
	public Type nominalType(String name) {
		return new UnresolvedType(name);
	}

	@Override
	public TypedAST var(String name, FileLocation loc) {
		return new Variable(new NameBindingImpl(name, null), loc);
	}

	@Override
	public TypedAST stringLit(String value) {
		return new StringConstant(value);
	}

	@Override
	public TypedAST integerLit(int value) {
		return new IntegerConstant(value);
	}

	@Override
	public TypedAST invocation(TypedAST receiver, String name,
			TypedAST argument, FileLocation loc) {
		return new Invocation(receiver, name, argument, loc);
	}

	@Override
	public TypedAST application(TypedAST function, TypedAST arguments,
			FileLocation loc) {
		return new Application(function, arguments, loc);
	}

	@Override
	public TypedAST unitValue(FileLocation loc) {
		return UnitVal.getInstance(loc);
	}

	@Override
	public TypedAST tuple(List<TypedAST> members) {
		return new TupleObject(members);
	}

}
