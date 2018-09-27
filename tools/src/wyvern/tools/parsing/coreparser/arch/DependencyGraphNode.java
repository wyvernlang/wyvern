package wyvern.tools.parsing.coreparser.arch;

import java.util.HashSet;

public class DependencyGraphNode {
    private ASTComponentDecl component;
    private HashSet<DependencyGraphNode> requires;
    private HashSet<DependencyGraphNode> provides;
    private boolean visited;

    public DependencyGraphNode(ASTComponentDecl component) {
        this.component = component;
        requires = new HashSet<>();
        provides = new HashSet<>();
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

    public HashSet<DependencyGraphNode> getRequires() {
        return requires;
    }

    public HashSet<DependencyGraphNode> getProvides() {
        return provides;
    }
}
