package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.evaluation.VarValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
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
import java.util.Map;
import java.util.Optional;

public class VarDeclaration extends Declaration implements CoreAST {
	TypedAST definition;
	Type definitionType;
	NameBinding binding;

	private boolean isClass;
	public boolean isClassMember() {
		return isClass;
	}

	public VarDeclaration(String varName, Type parsedType, TypedAST definition) {
		this.definition=definition;
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
        environment.register(getName());
        String genName = GenerationEnvironment.generateVariableName();
        writer.wrap(e->new Let(genName, Optional.ofNullable(definition).<Expression>map(d -> ExpressionWriter.generate(ew -> d.codegenToIL(environment, ew))).orElse(null), (Expression)e));
        writer.write(new wyvern.target.corewyvernIL.decl.VarDeclaration(getName(), getType().generateILType(), new Variable(genName)));
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
}