package wyvern.target.oir.declarations;

import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public abstract class OIRType extends OIRAST {
    public abstract String getName();
    private OIREnvironment environment;

    public OIRType(OIREnvironment environment) {
        this.environment = environment;
    }

    public OIREnvironment getEnvironment() {
        return environment;
    }


}
