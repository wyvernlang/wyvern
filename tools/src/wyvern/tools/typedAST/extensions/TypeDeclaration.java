package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class TypeDeclaration extends Declaration implements CoreAST {
	TypeBinding binding;
	
	public TypeDeclaration(String name, Environment env) {
		binding = new TypeBinding(name, Unit.getInstance()); // TODO: Implement proper Type for "type"!
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName());
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return binding.getType();
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public TypeBinding getBinding() {
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
	
	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(binding);
	}

	@Override
	protected Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new TypeBinding(binding.getName(), binding.getType()));
		return newEnv;
	}

	@Override
	protected void evalDecl(Environment evalEnv, Environment declEnv) {
		// TODO: Check to confirm there is indeed nothing to do here.
	}
}
