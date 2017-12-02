package wyvern.tools.typedAST.interfaces;

import java.util.List;

import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.types.Type;

public interface BoundCode {

	List<NameBinding> getArgBindings();
	TypedAST getBody();

}
