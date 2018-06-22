package wyvern.tools.parsing.coreparser.arch;

//combine with modulecheckvisitor
import java.util.HashMap;
import java.util.HashSet;

public class DeclCheckVisitor extends ArchParserVisitorAdapter {
  private HashSet<String> componentTypes = new HashSet<>();
  private HashSet<String> connectorTypes = new HashSet<>();

  private HashMap<String, String> components = new HashMap<>();
  private HashMap<String, String> connectors = new HashMap<>();

  private HashMap<String, String> entrypoints = new HashMap<>();
  private HashMap<String, HashSet<String>> attachments = new HashMap<>();

  public Object visit(ASTAttachmentDecl node, Object data) {
    String connector = node.getConnector();
    HashSet<String> ports = node.getAllPorts();
    if (!connectors.containsKey(connector)) {
      // connector not declared
    }
    if (attachments.putIfAbsent(connector, ports) == null) {
      // duplicate connector use
    }
    return super.visit(node, data);
  }

  public Object visit(ASTEntryPointDecl node, Object data) {
    entrypoints.put(node.getName(), node.getAction());
    return super.visit(node, data);
  }

  public Object visit(ASTComponentDecl node, Object data) {
    String name = node.getName();
    String type = node.getType();
    if (components.putIfAbsent(name, type) == null) {
      // duplicate members
    }
    return super.visit(node, data);
  }

  public Object visit(ASTConnectorDecl node, Object data) {
    String name = node.getName();
    String type = node.getType();
    if (connectors.putIfAbsent(name, type) == null) {
      // duplicate members
    }
    return super.visit(node, data);
  }

  public Object visit(ASTComponentTypeDecl node, Object data) {
    if (componentTypes.contains(node.getTypeName())) {
      // type already exists
    }
    if (!node.checkModule()) {
      // module not found
      System.out
          .println("component module " + node.getTypeName() + " not found");
    } else {
      componentTypes.add(node.getTypeName());
    }
    return super.visit(node, data);
  }

  public Object visit(ASTConnectorTypeDecl node, Object data) {
    if (connectorTypes.contains(node.getTypeName())) {
      // type already exists
    }
    if (!node.checkModule()) {
      // module not found
      System.out.println("connector type " + node.getTypeName() + " not found");
    } else {
      connectorTypes.add(node.getTypeName());
    }
    return super.visit(node, data);
  }

  public void showCounts() {
    System.out.println("components: " + components.size());
    System.out.println("connectors: " + connectors.size());
  }
}
