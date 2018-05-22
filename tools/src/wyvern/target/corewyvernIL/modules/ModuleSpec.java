package wyvern.target.corewyvernIL.modules;

import wyvern.target.corewyvernIL.BindingSite;

public class ModuleSpec {
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ModuleSpec)) {
            return false;
        }
        ModuleSpec other = (ModuleSpec) obj;
        if (qualifiedName == null) {
            if (other.qualifiedName != null) {
                return false;
            }
        } else if (!qualifiedName.equals(other.qualifiedName)) {
            return false;
        }
        return true;
    }

    private static final String INTERNAL_MODULE_PREFIX = "MOD$";

    private final String qualifiedName;
    private final BindingSite site;

    public ModuleSpec(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.site = new BindingSite(getInternalName());
    }

    public BindingSite getSite() {
        return site;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getInternalName() {
        return INTERNAL_MODULE_PREFIX + qualifiedName;
    }

    @Override
    public String toString() {
        return "ModuleSpec(" + qualifiedName + ")";
    }
}
