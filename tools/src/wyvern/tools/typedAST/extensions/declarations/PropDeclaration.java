package wyvern.tools.typedAST.extensions.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class PropDeclaration extends Declaration implements CoreAST {
	TypeBinding typeBinding;
	NameBinding nameBinding;
	
	public PropDeclaration(String name, TypeBinding typeBinding, FileLocation line2) {
		this.typeBinding = typeBinding;
		this.nameBinding = new NameBindingImpl(name, typeBinding.getType());
		this.location = line2;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(nameBinding.getName(), typeBinding);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (nameBinding.getType() == null) {
			// this.definitionType = this.definition.typecheck(env);
			this.nameBinding = new NameBindingImpl(nameBinding.getName(), typeBinding.getType());
		}
		return typeBinding.getType();
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public NameBinding getBinding() {
		return nameBinding;
	}

	@Override
	public Type getType() {
		return typeBinding.getType();
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(typeBinding).extend(nameBinding);
	}

	@Override
	protected Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), typeBinding.getType()));
		return newEnv;
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	protected void evalDecl(Environment evalEnv, Environment declEnv) {
		// Value defValue = definition.evaluate(evalEnv);
		// ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		// vb.setValue(defValue);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}
}