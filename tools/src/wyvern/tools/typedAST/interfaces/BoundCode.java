package wyvern.tools.typedAST.interfaces;

import java.util.List;

import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.types.Type;

public interface BoundCode {

	Type getType();
	List<NameBinding> getArgBindings();
	TypedAST getBody();

}
