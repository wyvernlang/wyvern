package wyvern.tools.typedAST.core.expressions;

import static wyvern.tools.errors.ErrorMessage.TYPE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.AbstractTreeWritable;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class TypeInstance extends AbstractTreeWritable implements CoreAST {
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
	public Type typecheck(Environment env, Optional<Type> expected) {
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
	public Value evaluate(EvaluationEnvironment env) {
		Value value = env.lookup(binding.getName()).get().getValue(env);
		assert value != null;
		return value;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TypeInstance(binding);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new RuntimeException("Invalid type instance"); // TODO is this used?
    }

    private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	/*@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}*/
}
