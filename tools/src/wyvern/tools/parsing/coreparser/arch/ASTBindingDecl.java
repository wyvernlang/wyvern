/* Generated By:JJTree: Do not edit this line. ASTBindingDecl.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package wyvern.tools.parsing.coreparser.arch;

public class ASTBindingDecl extends SimpleNode {
  private String name, target;

  public ASTBindingDecl(int id) {
    super(id);
  }

  public ASTBindingDecl(ArchParser p, int id) {
    super(p, id);
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public void setTarget(String t) {
    target = t;
  }

  public String getTarget() {
    return target;
  }

  public String toString() {
    return name + " is " + target;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ArchParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/*
 * JavaCC - OriginalChecksum=fd510aa9ed08a46db145df20696a8924 (do not edit this
 * line)
 */