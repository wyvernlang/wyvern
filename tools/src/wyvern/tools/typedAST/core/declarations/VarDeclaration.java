package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.VarValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VarDeclaration extends Declaration implements CoreAST {
	ExpressionAST definition;
	Type definitionType;
	NameBinding binding;

	private boolean isClass;
	public boolean isClassMember() {
		return isClass;
	}

	public VarDeclaration(String varName, Type parsedType, TypedAST definition) {
		this.definition=(ExpressionAST)definition;
		binding = new AssignableNameBinding(varName, parsedType);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName(), definition);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (this.definition != null) {
			Type varType = definitionType;
			boolean defType = this.definition.typecheck(env, Optional.of(varType)).subtype(varType);
			if (!defType)
				ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this);
		}
		return binding.getType();
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
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
	
	public TypedAST getDefinition() {
		return definition;
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return old.extend(binding);
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		return old.extend(new VarValueBinding(binding.getName(), binding.getType(), null));
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		VarValueBinding vb = declEnv.lookupValueBinding(binding.getName(), VarValueBinding.class).get();
		if (definition == null) {
            vb.assign(null);
			return;
		}
		Value defValue = definition.evaluate(evalEnv);
		vb.assign(defValue);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("definition", definition);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new VarDeclaration(getName(), getType(), nc.get("definition"));
	}


    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
    	ValueType valType = getType().generateILType();
        environment.register(getName(), valType);
        String genName = GenerationEnvironment.generateVariableName();
        writer.wrap(e->new Let(genName, Optional.ofNullable(definition).<Expression>map(d -> ExpressionWriter.generate(ew -> d.codegenToIL(environment, ew))).orElse(null), (Expression)e));
        writer.write(new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(), valType, new Variable(genName)));
    }

    @Override
	public Environment extendType(Environment env, Environment against) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		definitionType = TypeResolver.resolve(binding.getType(), against);
		binding = new AssignableNameBinding(binding.getName(), definitionType);

		return env.extend(binding);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		ValueType vt = binding.getType().getILType(ctx);
		return new VarDeclType(getName(), vt);
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		return new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(), binding.getType().getILType(ctx), definition.generateIL(ctx));
	}

	/**
	 * Internal helper method. Return the ValueType of this definition. If the type isn't
	 * currently set, it will figure it out and set the type.
	 * @param ctx: ctx to evaulate.
	 * @return the ValueType of the definition of this VarDeclaration.
	 */
	private ValueType getILValueType (GenContext ctx) {
		if (definitionType == null)
			definitionType = definition.getType();
		return definitionType.getILType(ctx);
	}
	
	@Override
	public void genTopLevel (TopLevelContext tlc) {

		GenContext ctx = tlc.getContext();
		
		// Name and type of this variable.
		String varName = this.getName();
		
		if (definitionType == null)
			definitionType = definition.getType();
		
		Type typeOfVar = this.definitionType;
		ValueType valTypeOfVar = typeOfVar.getILType(ctx);	
		
		// Create the body of the anonymous object.
		List<Declaration> decls = new LinkedList<>();
		decls.add(this);

		// Object name and variable reference.
		String objName = TopLevelContext.getAnonymousVarName(varName);
		wyvern.tools.typedAST.core.expressions.Variable objReference =
				new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("this", null), null);
		
		// Create the getter declaration.
		String getterName = "get";
		Invocation getterBody = new Invocation(objReference, varName, null, null);
		DefDeclaration getterDecl = new DefDeclaration(getterName, typeOfVar, new LinkedList<>(),
														 getterBody, false, null);
		decls.add(getterDecl);
		
		// Create the anonymous object.
		DeclSequence declSeq = new DeclSequence(decls);
		New objInstantiation = new New(declSeq, null);
		Expression expr = objInstantiation.generateIL(ctx);
		ValueType objType = declSeq.figureOutType(ctx);
		
		GenContext newCtx = tlc.getContext().extend(objName, expr, objType);
		tlc.updateContext(newCtx);
		tlc.addLet(objName, objType, expr, false);
		
		/*
		 * 
	public New(DeclSequence seq, FileLocation fileLocation) {
		this.seq = seq;
		this.location = fileLocation;
	}

		 */
		
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		GenContext ctx = tlc.getContext();
		wyvern.target.corewyvernIL.decl.Declaration decl;
		decl = new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(),
																  getILValueType(ctx),
																  definition.generateIL(ctx));
		DeclType dt = genILType(tlc.getContext());
		tlc.addModuleDecl(decl,dt);		
	}
	
}