package wyvern.tools.parsing.coreparser.arch;

public class ArchParserVisitorAdapter implements ArchParserVisitor {

  public Object visit(SimpleNode node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTArchDesc node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTComponentTypeDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTConnectorTypeDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTArchitectureTypeDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTComponentDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTConnectorDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTAttachmentDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTBindingDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTEntryPointDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTPortDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

  public Object visit(ASTValDecl node, Object data) {
    return node.childrenAccept(this, data);
  }

}
