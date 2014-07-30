package wyvern.tools.typedAST.core.binding.compiler;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class KeywordBinding implements Binding{

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "keywordEnv";
	}

	@Override
	public Type getType() {
		Unit.getInstance();
		return null;
	}

}
