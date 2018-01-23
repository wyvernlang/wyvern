package wyvern.tools.typedAST.interfaces;

import java.util.List;

import wyvern.tools.typedAST.core.binding.NameBinding;

public interface BoundCode {

    List<NameBinding> getArgBindings();
    TypedAST getBody();

}
