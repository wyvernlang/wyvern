package wyvern.tools.parsing.coreparser.arch;

import java.util.ArrayList;
import java.util.List;

public class DependencyGraphNode {
    private ASTComponentDecl component;
    private List<DependencyGraphNode> requires;
    private List<DependencyGraphNode> provides;
    private boolean visited;

    public DependencyGraphNode(ASTComponentDecl component) {
        this.component = component;
        requires = new ArrayList<>();
        provides = new ArrayList<>();
        visited = false;
    }

    public void addRequires(DependencyGraphNode dep) {
        requires.add(dep);
    }

    public void addProvides(DependencyGraphNode node) {
        provides.add(node);
    }

    public boolean visit() {
        if (visited) {
            return false;
        } else {
            visited = true;
            return true;
        }
    }

    public ASTComponentDecl getComponentDecl() {
        return component;
    }

    public List<DependencyGraphNode> getRequires() {
        return requires;
    }

    public List<DependencyGraphNode> getProvides() {
        return provides;
    }
}
