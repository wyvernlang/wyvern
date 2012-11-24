package wyvern.tools.typedAST.extensions;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ValDeclaration extends CachingTypedAST {
	TypedAST definition;
	TypedAST body;		 // initialized incrementally during parsing
	NameBinding binding;
	
	public ValDeclaration(String name, TypedAST definition) {
		this.definition=definition;
		binding = new NameBindingImpl(name, definition.typecheck());
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return new LineSequenceParser() {
			@Override
			public TypedAST parse(
					TypedAST first,
					LineSequence rest,
					Environment env) {
				
				Environment newEnv = env.extend(binding);
				body = rest.accept(CoreParser.getInstance(), newEnv);				
				return ValDeclaration.this;
			}
			
		};
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName(), definition, body);
	}

	@Override
	protected Type doTypecheck() {
		// definition is already typechecked
		Type bodyType = body.typecheck();
		return bodyType;
	}

	@Override
	public Value evaluate(Environment env) {
		Value defValue = definition.evaluate(env);
		Environment newEnv = env.extend(new ValueBinding(binding.getName(), defValue));
		return body.evaluate(newEnv);
	}
}
