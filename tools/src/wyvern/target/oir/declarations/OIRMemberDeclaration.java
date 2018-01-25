package wyvern.target.oir.declarations;

import wyvern.target.oir.OIRAST;

public abstract class OIRMemberDeclaration extends OIRAST {
    private String name;

    public String getName() {
        return name;
    }

    public abstract OIRType getType();
}
