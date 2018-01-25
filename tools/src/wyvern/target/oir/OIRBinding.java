package wyvern.target.oir;

import wyvern.target.oir.declarations.OIRType;

public abstract class OIRBinding {
    private String name;
    private OIRType type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public OIRType getType() {
        return type;
    }
    public void setType(OIRType type) {
        this.type = type;
    }
    protected OIRBinding(String name, OIRType type) {
        super();
        this.name = name;
        this.type = type;
    }
}
