package wyvern.tools.typedAST;

import java.util.List;

import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.types.Type;

public interface BoundCode {

	Type getType();
	List<NameBinding> getArgBindings();
	TypedAST getBody();

}
