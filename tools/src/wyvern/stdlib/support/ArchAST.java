package wyvern.stdlib.support;

import wyvern.target.corewyvernIL.support.ILFactory;
import wyvern.tools.parsing.coreparser.arch.ASTPortDecl;

public class ArchAST {
  public static final ArchAST utils = new ArchAST();
  private int identNum = 0;
  private ILFactory f = ILFactory.instance();

  public ASTPortDecl portDecl(String req, String prov, String name) {
    ASTPortDecl p = new ASTPortDecl(identNum++);
    p.setRequires(req);
    p.setProvides(prov);
    p.setPort(name);
    return p;
  }
}
