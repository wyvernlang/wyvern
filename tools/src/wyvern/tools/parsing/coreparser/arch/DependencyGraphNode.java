package wyvern.tools.parsing.coreparser.arch;

import java.util.ArrayList;
import java.util.List;

public class DependencyGraphNode {
    private ASTComponentDecl component;
    private List<ASTComponentDecl> dependencies;

    public DependencyGraphNode(ASTComponentDecl component)  {
        this.component = component;
        dependencies = new ArrayList<>();
    }

    public void addDependency(ASTComponentDecl dep) {
        dependencies.add(dep);
    }
}
