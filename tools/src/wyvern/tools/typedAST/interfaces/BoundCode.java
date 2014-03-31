package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.types.Type;

import java.util.List;

public interface BoundCode {

	Type getType();
	List<NameBinding> getArgBindings();
	TypedAST getBody();

}
