package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.TreeWriter;

public class Fn extends CachingTypedAST implements CoreAST {
	NameBinding binding;
	TypedAST body;

	public Fn(NameBinding binding, TypedAST body) {
		this.binding = binding;
		this.body = body;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding, body);
	}

	@Override
	protected Type doTypecheck() {
		Type argType = binding.getType();
		Type resultType = body.typecheck();
		return new Arrow(argType, resultType);
	}

	@Override
	public Value evaluate(Environment env) {
		return new Closure(this, env);
	}

	public NameBinding getBinding() {
		return binding;
	}

	public TypedAST getBody() {
		return body;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		if (body instanceof CoreAST)
			((CoreAST)body).accept(visitor);
		visitor.visit(this);
	}

}
