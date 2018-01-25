package wyvern.target.oir;

import java.util.HashMap;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.tools.errors.WyvernException;

public class PICEntry {
    private int classID;
    private int fieldPos;
    private HashMap<Integer, PICEntry> mapClassIDPICEntry;
    private PICEntry[] vectorPICEntry;
    private int entries;
    private boolean isFinal;
    private OIRClassDeclaration classDecl;

    /* Members instantiated only if isFinal is valid */
    private HashMap<Long, FinalPICNode> objectAddresses;

    public PICEntry(int classID, int fieldPos, OIRClassDeclaration classDecl) {
        isFinal = false;
        entries = 0;
        this.classID = classID;
        this.fieldPos = fieldPos;
        this.classDecl = classDecl;
        mapClassIDPICEntry = new HashMap<Integer, PICEntry>();
        vectorPICEntry = new PICEntry[PIC.getLinkedListMapThreshold()];
    }

    public PICEntry(int classID, OIRClassDeclaration classDecl) {
        entries = 0;
        this.classID = classID;
        this.classDecl = classDecl;
        this.fieldPos = -1;
        mapClassIDPICEntry = new HashMap<Integer, PICEntry>();
        vectorPICEntry = new PICEntry[PIC.getLinkedListMapThreshold()];
    }

    public FinalPICNode containsObjectAddress(long objectAddress) {
        return objectAddresses.get(objectAddress);
    }

    public void setFinalObjectAddress(long objectAddress, long fieldAddress, PICEntry picEntry) {
        objectAddresses.put(objectAddress, new FinalPICNode(fieldAddress, picEntry));
    }

    public void setIsFinal(boolean isFinal) {
        if (this.isFinal) {
            throw new WyvernException("Cannot set isFinal if it is already set to true");
        }
        this.isFinal = isFinal;
        objectAddresses = new HashMap<Long, FinalPICNode>();
    }

    public int getFieldPos() {
        return fieldPos;
    }

    public void setFeildPos(int fieldPos) {
        if (fieldPos != -1) {
            throw new WyvernException("Cannot set field position more than once");
        }
        this.fieldPos = fieldPos;
    }

    public PICEntry getEntry(int classID) {
        if (entries < PIC.getLinkedListMapThreshold()) {
            for (int i = 0; i < entries; i++) {
                if (vectorPICEntry[i].getClassID() == classID) {
                    return vectorPICEntry[i];
                }
            }
            return null;
        } else {
            return mapClassIDPICEntry.get(classID);
        }
    }

    public int getClassID() {
        return classID;
    }

    public void addChildEntry(int fieldPos, int entryClassID, OIRClassDeclaration classDecl) {
        PICEntry entry = new PICEntry(entryClassID, classDecl);
        if (entries < PIC.getLinkedListMapThreshold()) {
            vectorPICEntry[entries] = entry;
        }
        entries++;
        this.fieldPos = fieldPos;
        mapClassIDPICEntry.put(entryClassID, entry);
    }

    public void addChildEntry(int entryClassID, OIRClassDeclaration classDecl) {
        if (fieldPos == -1) {
            throw new WyvernException("Field Position for this class is not set. "
                    + "Please set it first");
        }
        PICEntry entry = new PICEntry(entryClassID, classDecl);
        if (entries < PIC.getLinkedListMapThreshold()) {
            vectorPICEntry[entries] = entry;
        }
        entries++;
        mapClassIDPICEntry.put(entryClassID, entry);
    }

    public void addChildEntry(int classID, PICEntry entry) {
        if (entries < PIC.getLinkedListMapThreshold()) {
            vectorPICEntry[entries] = entry;
        }
        entries++;
        mapClassIDPICEntry.put(classID, entry);
    }

    public OIRClassDeclaration getClassDecl() {
        return classDecl;
    }

    public boolean getIsFinal() {
        return isFinal;
    }
}