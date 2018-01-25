package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.tools.errors.FileLocation;

public abstract class NamedDeclaration extends Declaration {
    private String name;

    public NamedDeclaration(String name, FileLocation loc) {
        super(loc);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract DeclType getDeclType();
}
