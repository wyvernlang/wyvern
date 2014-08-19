package wyvern.tools.typedAST.core.binding.compiler;

import java.util.List;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class KeywordInnerBinding implements Binding{

	private final Reference<Environment> innerEnv;
	
	public KeywordInnerBinding() {
		innerEnv = new Reference<>(Environment.getEmptyEnvironment());
	}
	
	public KeywordInnerBinding(Environment metaEnv) {
		innerEnv = new Reference<>(metaEnv);
	}

	public KeywordInnerBinding(Reference<Environment> rEnv) {
		innerEnv = rEnv;
	}
	
	public KeywordInnerBinding from(Environment env) {
		Environment oldEnv = env.lookupBinding("keywordEnv", KeywordInnerBinding.class)
				.map(KeywordInnerBinding::getInnerEnv).orElse(Environment.getEmptyEnvironment());
		return new KeywordInnerBinding(new Reference<>(() -> oldEnv.extend(innerEnv.get())));
	}
	
	public Environment getInnerEnv() {
		return innerEnv.get();
	}
	
	public void nameToType(Environment env) {
		List<Binding> bindings = innerEnv.get().getBindings(); 
		for (Binding i : bindings) {
			if (i instanceof KeywordBinding) {
				((KeywordBinding)i).setHostType(env);
			}
		}
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		
	}

	@Override
	public String getName() {
		return "keywordEnv";
	}

	@Override
	public Type getType() {
		return Unit.getInstance();
	}
}
