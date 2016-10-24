package wyvern.tools.typedAST.core.declarations;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class DelegateDeclaration extends Declaration implements CoreAST {
	private TypedAST target;
	private Type type;
	private FileLocation location;
	
	public DelegateDeclaration(Type type, TypedAST target, FileLocation location) {
		this.type = type;
		this.target = target;
		this.location = location;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("target", target);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new DelegateDeclaration(getType(), nc.get("target"), this.location);
	}

	@Override
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		// TODO: fixme?
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		type = TypeResolver.resolve(type, against);
		if (!(type instanceof RecordType)) {
			ToolError.reportError(ErrorMessage.EXPECTED_RECORD_TYPE, this);
		}
		TypeType tt = ((RecordType) type).getEquivType();
		
		for (Map.Entry<String, Type> e : tt.getMembers().entrySet()) {
			env = env.extend(new NameBindingImpl(e.getKey(), e.getValue()));
		}
		
		return env;
	}

	@Override
	public String getName() {
		return "aDelegation";
	}

	@Override
	protected Type doTypecheck(Environment env) {
		boolean targetType = this.target.typecheck(env, Optional.of(type)).subtype(type);
		if (!targetType)
			ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this);
		return type;
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return extendName(old, against);
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
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
      if (!(target instanceof Variable)) {
          ToolError.reportError(ErrorMessage.DELEGATE_MUST_BE_VARIABLE,
                                this,
                                target.toString());
      }
		String targetName = ((Variable)target).getName();
		wyvern.target.corewyvernIL.decl.DelegateDeclaration iLDelegateDecl = new wyvern.target.corewyvernIL.decl.DelegateDeclaration(type.getILType(ctx), targetName, location);
		return iLDelegateDecl;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

}
