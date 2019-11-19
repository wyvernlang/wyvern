package wyvern.tools.types;

import wyvern.target.corewyvernIL.support.GenContext;

public interface NamedType extends Type {
    String getName();
    String getFullName();
    boolean isPresent(GenContext ctx);
}
