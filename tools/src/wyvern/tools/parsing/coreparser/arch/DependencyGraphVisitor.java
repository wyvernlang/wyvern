package wyvern.tools.parsing.coreparser.arch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DependencyGraphVisitor extends ArchParserVisitorAdapter {
    private HashMap<String, ASTComponentDecl> components = new HashMap<>();
    private List<DependencyGraphNode> depNodes = new LinkedList<>();

    public Object visit(ASTAttachmentDecl node, Object data) {
        return super.visit(node, data);
    }

    public HashMap<String, ASTComponentDecl> getComponents() {
        return components;
    }

    public void setComponents(HashMap<String, ASTComponentDecl> components) {
        this.components = components;
    }
}
