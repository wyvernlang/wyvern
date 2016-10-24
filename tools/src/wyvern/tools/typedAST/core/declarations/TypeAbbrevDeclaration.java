package wyvern.tools.typedAST.core.declarations;

import static wyvern.tools.errors.ToolError.reportError;

import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

// TODO: this represents an abstract type when the reference is null.
// would be better to have a separate class for representing an abstract type
public class TypeAbbrevDeclaration extends Declaration implements CoreAST {

	private String alias;
	private Type reference;
	private FileLocation location;

	public TypeAbbrevDeclaration(String alias, Type reference, FileLocation loc) {
		this.alias = alias;
		this.reference = reference;
		this.location = loc;
	}
	
	public Type getReference() {
		return reference;
	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileLocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		// TODO Auto-generated method stub
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		// TODO Auto-generated method stub
		return env;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return alias;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (resolveReferenceType(env) != null) {
			return new Unit();
		}
		else {
			ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, this);
			return null;
		}
		
	}

	private Type resolveReferenceType(Environment env) {
		Type resolved_type = TypeResolver.resolve(reference, env);
		return resolved_type;
	}
	
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return old.extend(new TypeBinding(alias, resolveReferenceType(old)));
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		// TODO Auto-generated method stub
		return old;
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv,
			EvaluationEnvironment declEnv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DeclType genILType(GenContext ctx) {
        if (this.reference == null) {
            return new AbstractTypeMember(this.alias);
        }
		ValueType referenceILType = reference.getILType(ctx);
		return new ConcreteTypeMember(getName(), referenceILType);
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(
			GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return new TypeDeclaration(alias, reference.getILType(ctx), getLocation());
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(
			GenContext ctx, List<TypedModuleSpec> dependencies) {
		if (reference == null)
			reportError(ErrorMessage.NO_ABSTRACT_TYPES_IN_OBJECTS, this);
		ValueType referenceILType = reference.getILType(ctx);
		return new TypeDeclaration(getName(), referenceILType, getLocation());
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		wyvern.target.corewyvernIL.decl.Declaration decl = topLevelGen(tlc.getContext(), null);
		DeclType dt = genILType(tlc.getContext());
		tlc.addModuleDecl(decl,dt);
	}

	


	
}
