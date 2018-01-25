package wyvern.target.oir;

public class RuntimeType {
    private String className;
    private int fieldValueAssignedPos;
    private long fieldAddress;
    private String fieldValueClass;
    private RuntimeType parent;
    private int classID;

    public RuntimeType(int classID, String className, int fieldValueAssigned,
            long fieldAddress, String fieldValueClass, RuntimeType parent) {
        this.setClassID(classID);
        this.className = className;
        this.fieldAddress = fieldAddress;
        this.fieldValueAssignedPos = fieldValueAssigned;
        this.fieldValueClass = fieldValueClass;
        this.parent = parent;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
}
