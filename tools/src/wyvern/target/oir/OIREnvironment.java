package wyvern.target.oir;

import java.util.HashMap;
import java.util.LinkedList;

import wyvern.target.oir.declarations.OIRType;

public class OIREnvironment {
    private OIREnvironment parent;
    private HashMap<String, OIRType> nameTable;
    private HashMap<String, OIRType> typeTable;
    private LinkedList<OIREnvironment> children;

    private static OIREnvironment rootEnvironment = new OIREnvironment(null);

    public static OIREnvironment getRootEnvironment() {
        return rootEnvironment;
    }

    public static void resetRootEnvironment() {
        rootEnvironment = new OIREnvironment(null);
    }

    public OIREnvironment(OIREnvironment parent) {
        children = new LinkedList<OIREnvironment>();
        nameTable = new HashMap<String, OIRType>();
        typeTable = new HashMap<String, OIRType>();
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void addChild(OIREnvironment environment) {
        children.add(environment);
    }

    public void addName(String name, OIRType type) {
        nameTable.put(name, type);
    }

    public void addType(String name, OIRType type) {
        typeTable.put(name, type);
    }

    public HashMap<String, OIRType> getNameTable() {
        return nameTable;
    }

    public HashMap<String, OIRType> getTypeTable() {
        return typeTable;
    }

    public LinkedList<OIREnvironment> getChildren() {
        return children;
    }

    public OIRType lookup(String name) {
        if (name == null) {
            return null;
        }
        OIRType type = nameTable.get(name);
        if (type == null) {
            if (parent == null) {
                throw new RuntimeException("OIREnvironment looking up \"" + name + "\", parent is null");
            }
            return parent.lookup(name);
        }
        return type;
    }

    public OIRType lookupType(String name) {
        if (name == null) {
            return null;
        }
        OIRType type = typeTable.get(name);
        if (type == null) {
            if (parent == null) {
                throw new RuntimeException("OIREnvironment looking up type \"" + name + "\", parent is null");
            }
            return parent.lookupType(name);
        }
        return type;
    }

    // TODO: Is this a reasonable approach to finding class declarations?
    public OIRType topDownLookupType(String name) {
        if (name == null) {
            return null;
        }
        OIRType type = typeTable.get(name);
        if (type == null) {
            for (OIREnvironment child : children) {
                type = child.topDownLookupType(name);
                if (type != null) {
                    return type;
                }
            }
        }
        return type;
    }

    public String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        doPrettyPrint(builder, "");
        return builder.toString();
    }

    private void doPrettyPrint(StringBuilder builder, String indent) {
        builder.append(indent + "OIREnvironment " + this + ":\n");
        builder.append(indent + "nameTable:\n");
        for (String name : nameTable.keySet()) {
            builder.append(indent + "  " + name + "\n");
        }
        builder.append(indent + "typeTable:\n");
        for (String name : typeTable.keySet()) {
            builder.append(indent + "  " + name + "\n");
        }
        for (OIREnvironment child : children) {
            child.doPrettyPrint(builder, indent + "  ");
        }
    }
}
