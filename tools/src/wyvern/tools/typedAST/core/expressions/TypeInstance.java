package wyvern.tools.typedAST.core.expressions;

import static wyvern.tools.errors.ErrorMessage.TYPE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class TypeInstance extends AbstractTypedAST implements CoreAST {
	private TypeBinding binding;
	
	public TypeInstance(TypeBinding binding) {
		this.binding = binding;
	}

	public String getName() {
		return this.binding.getName();
	}
	
	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName());		
	}

	@Override
	public Type typecheck(Environment env) {
		Type type = getType();
		if (type == null) {
			String name = binding.getName();
			binding = env.lookupType(name);
			if (binding == null)
				reportError(TYPE_NOT_DECLARED, this, name);
			else
				type = binding.getType();
		}
		return type;
	}

	@Override
	public Value evaluate(Environment env) {
		Value value = env.getValue(binding.getName());
		assert value != null;
		return value;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
