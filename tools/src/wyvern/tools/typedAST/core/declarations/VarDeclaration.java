package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.VarValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.values.UnitVal;
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
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Unit;
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
		
		// Create a var declaration.
		wyvern.target.corewyvernIL.decl.VarDeclaration varDecl;
		varDecl = new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(), binding.getType().getILType(ctx), definition.generateIL(ctx, null));
		String varName = this.getName();
		return varDecl;

		/*

		// Create a declaration for getter method.
		String getterName = varNameToGetter(varName);
		wyvern.tools.typedAST.core.expressions.Variable gettersVarReference;
		gettersVarReference = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(), location)
		
		gettersObjReference = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(tempObjName, null), null);
		Invocation getterBody = new Invocation(gettersObjReference, varName, null, null);
		Arrow getterArrType = new Arrow(new Unit(), definitionType);
		DefDeclaration getterDecl = new DefDeclaration(getterName, getterArrType, new LinkedList<>(), getterBody, false, null);
		wyvern.target.corewyvernIL.decl.Declaration getterDeclIL = getterDecl.generateDecl(ctx, ctx);

		// Create a declaration for setter method.
		String setterName = varNameToSetter(varName);
		wyvern.tools.typedAST.core.expressions.Variable settersObjReference;
		settersObjReference = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(tempObjName, null), null);
		Invocation fieldGet = new Invocation(settersObjReference, varName, null, null);
		wyvern.tools.typedAST.core.expressions.Variable valueToAssign =
				new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("x", null), null);
		Assignment setterBody = new Assignment(fieldGet, valueToAssign, null);
		LinkedList<NameBinding> setterArgs = new LinkedList<>();
		setterArgs.add(new NameBindingImpl("x", definitionType));
		Arrow setterArrType = new Arrow(definitionType, new Unit());
		DefDeclaration setterDecl = new DefDeclaration(setterName, setterArrType, setterArgs, setterBody, false, null);
		wyvern.target.corewyvernIL.decl.Declaration setterDeclIL = setterDecl.generateDecl(ctx, ctx);
		
		
		
		return new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(), binding.getType().getILType(ctx), definition.generateIL(ctx, null));
		
		*/
		
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ValueType getILValueType (GenContext ctx) {
		if (definitionType == null)
			definitionType = definition.getType();
		return definitionType.getILType(ctx);
	}
	
	@Override
	public void genTopLevel (TopLevelContext tlc) {
		
		GenContext ctx = tlc.getContext();
		
		// Figure out name and type of this variable.
		String varName = this.getName();
		ValueType varValueType = getILValueType(ctx);
		
		// Create a temp object with a single var declaration.
		VarDeclaration varDecl = new VarDeclaration(varName, this.definitionType, this.definition);
		DeclSequence tempObjBody = new DeclSequence(varDecl);
		New tempObj = new New(tempObjBody, null);
		String tempObjName = varNameToTempObj(varName);
		ValDeclaration letDecl = new ValDeclaration(tempObjName, tempObj, null);
		
		// Update context.
		letDecl.genTopLevel(tlc);
		ctx = tlc.getContext();
		
		// Create a declaration for getter method.
		String getterName = varNameToGetter(varName);
		wyvern.tools.typedAST.core.expressions.Variable gettersObjReference;
		gettersObjReference = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(tempObjName, null), null);
		Invocation getterBody = new Invocation(gettersObjReference, varName, null, null);
		Arrow getterArrType = new Arrow(new Unit(), definitionType);
		DefDeclaration getterDecl = new DefDeclaration(getterName, getterArrType, new LinkedList<>(), getterBody, false, null);
		wyvern.target.corewyvernIL.decl.Declaration getterDeclIL = getterDecl.generateDecl(ctx, ctx);

		// Create a declaration for setter method.
		String setterName = varNameToSetter(varName);
		wyvern.tools.typedAST.core.expressions.Variable settersObjReference;
		settersObjReference = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl(tempObjName, null), null);
		Invocation fieldGet = new Invocation(settersObjReference, varName, null, null);
		wyvern.tools.typedAST.core.expressions.Variable valueToAssign =
				new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("x", null), null);
		Assignment setterBody = new Assignment(fieldGet, valueToAssign, null);
		LinkedList<NameBinding> setterArgs = new LinkedList<>();
		setterArgs.add(new NameBindingImpl("x", definitionType));
		Arrow setterArrType = new Arrow(definitionType, definitionType);
		DefDeclaration setterDecl = new DefDeclaration(setterName, setterArrType, setterArgs, setterBody, false, null);
		wyvern.target.corewyvernIL.decl.Declaration setterDeclIL = setterDecl.generateDecl(ctx, ctx);
		
		// Create a block of declarations.
		List<DeclType> declTypes = new LinkedList<>();
		declTypes.add(getterDecl.genILType(ctx));
		declTypes.add(setterDecl.genILType(ctx));
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<>();
		decls.add(getterDeclIL);
		decls.add(setterDeclIL);
		String newName = GenContext.generateName();
		StructuralType structType = new StructuralType(newName, declTypes);
		ctx = ctx.extend(newName,  new Variable(newName), structType);
		tlc.updateContext(ctx);
		
		// Create a new expression to add to the top-level.
		wyvern.target.corewyvernIL.expression.New newExp;
		newExp = new wyvern.target.corewyvernIL.expression.New(decls, newName, structType);
		tlc.addLet(newName, structType, newExp, true);
		
		// Create a method call to the getter.
		MethodCall methodCallExpr = new MethodCall(new Variable(newName), getterName, new LinkedList<>(), null);
				
		// Equate the var with a call to the getter.
		ctx = ctx.extend(varName, methodCallExpr, varValueType);
		tlc.updateContext(ctx);
		
	}
	
	public static String varNameToTempObj (String s) {
		return "__temp" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	public static String varNameToGetter (String s) {
		return "get" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	public static String varNameToSetter (String s) {
		return "set" + Character.toUpperCase(s.charAt(0)) + s.substring(1);		
	}

	public static String getterToVarName (String s) {
		return s.replaceFirst("get", "");
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		return; // do nothing--adding module declarations handle in genTopLevel.
	}
	
}