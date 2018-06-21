package wyvern.tools.parsing.coreparser.arch;

public class ModuleCheckVisitor extends ArchParserVisitorAdapter {

  public Object visit(ASTComponentTypeDecl node, Object data) {
    if (!node.checkModule()) {
      System.out
          .println("component module " + node.getTypeName() + " not found");
    }
    return super.visit(node, data);
  }

  public Object visit(ASTConnectorTypeDecl node, Object data) {
    if (!node.checkModule()) {
      System.out.println("connector type " + node.getTypeName() + " not found");
    }
    return super.visit(node, data);
  }
}
