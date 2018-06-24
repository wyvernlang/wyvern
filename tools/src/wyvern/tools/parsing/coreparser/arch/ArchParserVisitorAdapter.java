package wyvern.tools.parsing.coreparser.arch;

public class ArchParserVisitorAdapter implements ArchParserVisitor {

  public Object visit(SimpleNode node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTArchDesc node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTComponentTypeDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTConnectorTypeDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTArchitectureTypeDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTComponentDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTConnectorDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTAttachmentDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTBindingDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTEntryPointDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTPortDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTValDecl node, Object data) {
    // System.out.println(node.getClass() + ": " + node.getLocation());
    return node.childrenAccept(this, data);
  }

}
