/* Generated By:JJTree: Do not edit this line. ASTConnectorDecl.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,
NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package wyvern.tools.parsing.coreparser.arch;

public class ASTConnectorDecl extends SimpleNode {
    private String type, name;

    public ASTConnectorDecl(int id) {
        super(id);
    }

    public ASTConnectorDecl(ArchParser p, int id) {
        super(p, id);
    }

    public ASTConnectorDecl(ArchParser p, int id, String t, String n,
                            Node parent) {
        super(p, id);
        this.type = t;
        this.name = n;
        this.jjtSetParent(parent);
    }

    public void setType(String t) {
        type = t;
    }

    public String getType() {
        return type;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return type + " " + name;
    }

    /**
     * Accept the visitor.
     **/
    public Object jjtAccept(ArchParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
/*
 * JavaCC - OriginalChecksum=9670377f97f23accce82607da0be5fb8 (do not edit this
 * line)
 */
