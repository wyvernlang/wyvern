package wyvern.tools.typedAST.core.declarations;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.StaticTypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.TypeInv;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class ValDeclaration extends Declaration implements CoreAST {
	ExpressionAST definition;
	Type definitionType;
	NameBinding binding;
	
	Type declaredType;
	String declaredTypeName;
	
	String variableName;

	private TaggedInfo ti;

	private boolean isClass;
	public boolean isClassMember() {
		return isClass;
	}

	public ValDeclaration(String name, TypedAST definition, FileLocation location) {
		this.definition=(ExpressionAST) definition;
		binding = new NameBindingImpl(name, null);
		this.location = location;
	}

	public ValDeclaration(String name, Type type, TypedAST definition, FileLocation location) {
		if (type instanceof UnresolvedType) {
			UnresolvedType t = (UnresolvedType) type;

			// System.out.println("t = " + t);
			
			TaggedInfo tag = TaggedInfo.lookupTagByType(t); // FIXME:
			
			// System.out.println("tag = " + tag);
			
			// if (tag != null) {
				//doing a tagged type
				ti = tag;
				
				variableName = name;
				
				declaredType = type; // Record this.
				
				//type = null;
			// }
		}

		this.definition=(ExpressionAST)definition;
		binding = new NameBindingImpl(name, type);
		this.location = location;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Type resolved = null;
		if (binding.getType() != null)
			resolved = TypeResolver.resolve(binding.getType(), env);
		if (this.definition != null)
			this.definitionType = this.definition.typecheck(env, Optional.ofNullable(resolved));
		if (resolved == null)
			resolved = definitionType;
		
		// FIXME:
		// System.out.println(resolved);

		binding = new NameBindingImpl(binding.getName(), resolved);
		if (binding.getType() == null) {
			this.binding = new NameBindingImpl(binding.getName(), resolved);
		} else if (this.definitionType != null &&
				!this.definitionType.subtype(resolved)){
			ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, this.definitionType.toString(), binding.getType().toString());
		}
		
		// System.out.println("dt = " + declaredType);
		
		if (declaredType instanceof UnresolvedType) {
			UnresolvedType ut = (UnresolvedType) declaredType;
			//System.out.println(ut.getName());
			this.declaredTypeName = ut.getName();
			declaredType = ((UnresolvedType) declaredType).resolve(env);
			//System.out.println("ut =" + ((ClassType) ut.resolve(env)).getName());
		}
		if (definitionType != null && declaredType != null && !definitionType.subtype(declaredType))
			ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, definitionType.toString(), declaredType.toString());

		// System.out.println(((ClassType) resolvedDeclaredType).getName());
		
		// Update tag.
		this.ti = TaggedInfo.lookupTagByType(resolved);
		
		return binding.getType();
	}

	public NameBinding getBinding() {
		return binding;
	}

	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public String getName() {
		return binding.getName();
	}
	
	public ExpressionAST getDefinition() {
		return definition;
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment env = extendName(old, against);
		if (variableName != null)
			env = env.extend(new StaticTypeBinding(variableName, this.declaredTypeName)); // FIXME:
		
		return env;
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		EvaluationEnvironment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		if (!declEnv.lookup(binding.getName()).isPresent()) return;
			
		Value defValue = null;
		if (definition != null)
			defValue = definition.evaluate(evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookupValueBinding(binding.getName(), ValueBinding.class).get();
		vb.setValue(defValue);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		if (definition != null)
			children.put("definition", definition);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		if (nc.containsKey("definition"))
			return new ValDeclaration(getName(), binding.getType(), nc.get("definition"), location);
		return new ValDeclaration(getName(), binding.getType(), null, location);
	}

    @Override
	public Environment extendType(Environment env, Environment against) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		// System.out.println("Resolving ValDeclaration using extendName: " + this.getName());
		
		Type resolved;
		if (binding.getType() != null) {
			
			// System.out.println("Inside ValDeclaration resolving type: " + binding.getType());
			// System.out.println("Inside ValDeclaration resolving type: " + binding.getType().getClass());
			
			if (binding.getType() instanceof TypeInv) {
				TypeInv ti = (TypeInv) binding.getType();
				
				// System.out.println("TypeInv = " + ti);
				// System.out.println("against = " + against);
			}
			resolved = TypeResolver.resolve(binding.getType(), against);
		} else {
			if (definitionType == null)
				typecheckSelf(against);
			resolved = definitionType;
		}
		definitionType = resolved;

		return env.extend(new NameBindingImpl(getName(), resolved));
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}

	@Override
	public void genTopLevel(TopLevelContext tlc) {
		ValueType declType = getILValueType(tlc.getContext());
		tlc.addLet(getName(), getILValueType(tlc.getContext()), definition.generateIL(tlc.getContext(), declType, tlc.getDependencies()), false);
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		ValueType vt = getILValueType(ctx);
		return new ValDeclType(getName(), vt);
	}

	private ValueType getILValueType(GenContext ctx) {
		ValueType vt;
		if (declaredType != null) {
			// convert the declared type if there is one
			vt = declaredType.getILType(ctx);
		} else {

            final Type type = this.binding.getType();
            if (type != null) {
			
			// then there is no proper R-value
			//if(definition == null) {
				vt = type.getILType(ctx);
			} else {
				// convert the declaration and typecheck it
				vt = definition.generateIL(ctx, null, null).typeCheck(ctx);
			}
		}
		return vt;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		
		ValueType expectedType = getILValueType(thisContext);
		/* uses ctx for generating the definition, as the selfName is not in scope */
		return new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(), expectedType, definition.generateIL(ctx, expectedType, null), location);
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		wyvern.target.corewyvernIL.decl.Declaration decl =
			new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(),
					getILValueType(tlc.getContext()),
					new wyvern.target.corewyvernIL.expression.Variable(getName()), location);
		DeclType dt = genILType(tlc.getContext());
		tlc.addModuleDecl(decl,dt);
	}

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("val ");
        sb.append(variableName);
        sb.append(" : ");
        if (declaredType != null)
            sb.append(declaredType.toString());
        else
            sb.append("null");
        sb.append(" = ");
        if (definition != null)
            sb.append(definition.prettyPrint());
        else
            sb.append("null");
        return sb;
    }
}
