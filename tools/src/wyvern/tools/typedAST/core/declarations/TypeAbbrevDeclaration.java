package wyvern.tools.typedAST.core.declarations;

import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.binding.NameBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.AliasBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class TypeAbbrevDeclaration extends Declaration implements CoreAST {


	
	
	private String alias;
	private Type reference;
	private FileLocation location;

	public TypeAbbrevDeclaration() {
		
	}
	
	public TypeAbbrevDeclaration(String alias, Type reference, FileLocation loc) {
		this.alias = alias;
		this.reference = reference;
		this.location = loc;
				
	}
	
	public Type getReference() {
		return reference;
	}
	
	public String getAlias() {
		return alias;
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
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
		
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
	public void accept(CoreASTVisitor visitor) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(
			GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(
			GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	

	


	
}
