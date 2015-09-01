package wyvern.tools.parsing.coreparser;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.DelegateDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Case;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.Match;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.QualifiedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.Reference;

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
	public TypedAST moduleDecl(String name, TypedAST ast, FileLocation loc, boolean isResource) {
		return  new ModuleDeclaration(name, (EnvironmentExtender)ast, loc, isResource);
	}

	@Override
	public TypedAST importDecl(URI uri, FileLocation loc, boolean isRequire) {
		return new ImportDeclaration(uri, loc, isRequire);
	}

	@Override
	public TypedAST valDecl(String name, Type type, TypedAST exp, FileLocation loc) {
		return new ValDeclaration(name, type, exp, loc);
	}

	@Override
	public TypedAST varDecl(String name, Type type, TypedAST exp, FileLocation loc) {
		// TODO: use loc!
		return new VarDeclaration(name, type, exp);
	}

	@Override
	public TypedAST defDecl(String name, Type type, List args,
			TypedAST body, boolean isClassDef, FileLocation loc) {
		return new DefDeclaration(name, type, args, body, isClassDef, loc);
	}
	
	@Override
	public TypedAST defDeclType(String name, Type type, List args, FileLocation loc) {
		return new DefDeclaration(name, type, args, null, false, loc);
	}
	
	@Override
	public TypedAST valDeclType(String name, Type type, FileLocation loc) {
		return new ValDeclaration(name, type, null, loc);
	}
	
	@Override
	public TypedAST typeDecl(String name, TypedAST body, Object tagInfo, TypedAST metadata, FileLocation loc, boolean isResource) {
		if (body == null) {
			body = new DeclSequence();
		}
		if (!(body instanceof DeclSequence)) {
			body = new DeclSequence(Arrays.asList(body));
		}
		//Reference<Value> meta = (metadata==null)?null:new Reference<Value>((Value)metadata);
		//return new TypeDeclaration(name, (DeclSequence) body, null, (TaggedInfo) tagInfo, loc);
		return new TypeVarDecl(name, (DeclSequence) body, (TaggedInfo) tagInfo, metadata, loc, isResource);
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
	public Type qualifiedType(TypedAST base, String name) {
		return new QualifiedType(base, name);
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

	@Override
	public TypedAST newObj(FileLocation loc) {
		return new New(new HashMap<String,TypedAST>(), loc);
	}

	@Override
	public void setNewBody(TypedAST newExp, TypedAST decls) {
		if (decls==null) {
			decls = new DeclSequence(Arrays.asList());
		}
		if (!(decls instanceof DeclSequence)) {
			decls = new DeclSequence(Arrays.asList(decls));
		}
		((New)newExp).setBody((DeclSequence)decls);
	}

	@Override
	public TypedAST assignment(TypedAST lhs, TypedAST rhs, FileLocation loc) {
		return new Assignment(lhs, rhs, loc);
	}

	@Override
	public TypedAST delegateDecl(Type type, TypedAST exp, FileLocation loc) {
		return new DelegateDeclaration(type, exp, loc);
	}

	@Override
	public TypedAST match(TypedAST exp, List cases, FileLocation loc) {
		return new Match(exp, cases, loc);
	}

	@Override
	public Object caseArm(String name, Type type, TypedAST exp,
			FileLocation loc) {
		return new Case(name, type, exp);
	}

	@Override
	public Object tagInfo(Type type, List<Type> comprises) {
		return new TaggedInfo(type, comprises);
	}

	@Override
	public TypedAST instantiation(URI uri, TypedAST arg, FileLocation loc) {
		return new Instantiation(uri, arg, loc);
	}

}
