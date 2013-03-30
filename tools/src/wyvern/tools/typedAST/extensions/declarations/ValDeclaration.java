package wyvern.tools.typedAST.extensions.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ValDeclaration extends Declaration implements CoreAST {
	TypedAST definition;
	Type definitionType;
	NameBinding binding;
	
	public ValDeclaration(String name, TypedAST definition) {
		this.definition=definition;
		binding = new NameBindingImpl(name, null);
	}
	
	public ValDeclaration(String name, Type type, TypedAST definition) {
		this.definition=definition;
		binding = new NameBindingImpl(name, type);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName(), definition);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (this.definition != null)
			this.definitionType = this.definition.typecheck(env);
		if (binding.getType() == null) {
			this.definitionType = this.definition.typecheck(env);
			this.binding = new NameBindingImpl(binding.getName(), definitionType);
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
	protected Environment doExtend(Environment old) {
		return old.extend(binding);
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		if (declEnv.getValue(binding.getName()) != null)
			return;
			
		Value defValue = null;
		if (definition != null)
			defValue = definition.evaluate(evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		vb.setValue(defValue);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location; //TODO
	}
}
