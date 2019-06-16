package wyvern.target.oir;

import java.util.List;
import java.util.Vector;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRType;
import wyvern.target.oir.expressions.OIRExpression;
import wyvern.tools.errors.WyvernException;

class MethodAddress {
    private String className;
    private long objectAddress;

    public String getClassName() {
        return className;
    }

    public long getObjectAddress() {
        return objectAddress;
    }

    MethodAddress(String className, long objectAddress) {
        super();
        this.className = className;
        this.objectAddress = objectAddress;
    }
}

public final class OIRProgram extends OIRAST {
    private List<OIRType> typeDeclarations;
    private OIRExpression mainExpression;
    private static int classID = 0;
    private PIC[] picArray;
    private int totalCallSites;

    public static final OIRProgram PROGRAM = new OIRProgram();

    public enum ForwardImplementation {
        HASH_TABLE_NAIVE, /* Every call will do Hash Table Lookup */
        PIC, /* Using PIC */
    }

    private static ForwardImplementation forwardImplementation = ForwardImplementation.HASH_TABLE_NAIVE;


    public static void setForwardImplementation(ForwardImplementation forwardImpl) {
        forwardImplementation = forwardImpl;
    }

    public OIRExpression getMainExpression() {
        return mainExpression;
    }

    public void setMainExpression(OIRExpression mainExpression) {
        this.mainExpression = mainExpression;
    }

    public void typeCheck(OIREnvironment environment) {
        for (OIRType oirType : typeDeclarations) {
            if (oirType instanceof OIRClassDeclaration) {
                OIRClassDeclaration classDecl = (OIRClassDeclaration) oirType;
                for (OIRMemberDeclaration memDecl : classDecl.getMembers()) {
                    if (memDecl instanceof OIRMethod) {
                        OIRMethod methDecl = (OIRMethod) memDecl;
                        methDecl.getBody().typeCheck(methDecl.getEnvironment());
                    }
                }
            }
        }
        mainExpression.typeCheck(environment);
    }

    public void addTypeDeclaration(OIRType typeDeclaration) {
        typeDeclarations.add(typeDeclaration);
        if (typeDeclaration instanceof OIRClassDeclaration) {
            ((OIRClassDeclaration) typeDeclaration).setClassID(classID);
            classID++;
        }
    }

    public List<OIRType> typeDeclarations() {
        return typeDeclarations;
    }

    private OIRProgram() {
        typeDeclarations = new Vector<OIRType>();
        mainExpression = null;
        totalCallSites = 0;
    }

    public void setCallSites(int callSitesNum, String[] methodArray) {
        totalCallSites = callSitesNum;
        picArray = new PIC[callSitesNum];
        for (int i = 0; i < callSitesNum; i++) {
            picArray[i] = new PIC(i, methodArray[i]);
        }
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    public int getFieldPositionInClass(int classID, String fieldName) {
        OIRClassDeclaration classDecl = getClassDeclaration(classID);
        return classDecl.getFieldPosition(fieldName);
    }

    public OIRClassDeclaration getClassDeclaration(int classID) {
        for (OIRType decl : typeDeclarations) {
            if (decl instanceof OIRClassDeclaration) {
                OIRClassDeclaration classDecl = (OIRClassDeclaration) decl;
                if (classDecl.getClassID() == classID) {
                    return classDecl;
                }
            }
        }
        return null;
    }

    public String getClassName(int classID) {
        for (OIRType decl : typeDeclarations) {
            if (decl instanceof OIRClassDeclaration) {
                OIRClassDeclaration classDecl = (OIRClassDeclaration) decl;
                if (classDecl.getClassID() == classID) {
                    return classDecl.getName();
                }
            }
        }
        return "";
    }

    public MethodAddress getClassNameForCallSite(long objectAddress, int classID,
            int callSiteID, String methodName) {
        if (forwardImplementation == ForwardImplementation.HASH_TABLE_NAIVE) {
            return forwardHashTableNaive(objectAddress, classID, methodName);
        } else if (forwardImplementation == ForwardImplementation.PIC) {
            PIC pic = picArray[callSiteID];
            return pic.search(classID, objectAddress);
        }
        throw new WyvernException("Invalid Forward Implementation selected");
    }

    public MethodAddress forwardHashTableBuildPICEntry(long objectAddress,
            int classID, OIRClassDeclaration oirClassDecl, String methodName, PICEntry classPICEntry,
            long fieldAddress, int fieldPos, int fieldClassID) {
        PICEntry lastFinalEntry;
        long lastFinalObjAddress;
        PICEntry entry = classPICEntry;
        if (entry.getIsFinal()) {
            lastFinalEntry = entry;
            lastFinalObjAddress = objectAddress;
        } else {
            lastFinalObjAddress = -1;
            lastFinalEntry = null;
        }

        if (fieldAddress != -1 && fieldPos != -1 && fieldClassID != -1) {
            /* This means PIC's search method have already found the field's
             * classID. So let us first search in the field.
             * Then go to the object's field
             * */
            oirClassDecl = getClassDeclaration(classID);
            fieldPos = oirClassDecl.getForwardMethodFieldHashMap(methodName);

            if (fieldPos == -1) {
                System.out.println("Error: Cannot find method in any of the fields");
                System.exit(-1);
            }

            fieldAddress = ForwardNative.getFieldAddress(oirClassDecl.getName(), objectAddress, fieldPos);
            fieldClassID = ForwardNative.getObjectClassID(fieldAddress);
            OIRClassDeclaration fieldClassDecl = getClassDeclaration(fieldClassID);
            PICEntry fieldPICEntry = new PICEntry(fieldClassID, fieldClassDecl);

            if (oirClassDecl.getFieldDeclarationForPos(fieldPos).isFinal()) {
                entry.setIsFinal(true);
                if (lastFinalEntry == null) {
                    lastFinalEntry = entry;
                    lastFinalObjAddress = objectAddress;
                }
            } else {
                if (lastFinalEntry != null && lastFinalObjAddress != -1) {
                    lastFinalEntry.setFinalObjectAddress(lastFinalObjAddress, fieldAddress, fieldPICEntry);
                }
                lastFinalEntry = null;
                lastFinalObjAddress = -1;
            }

            entry.setFeildPos(fieldPos);
            entry.addChildEntry(fieldClassID, fieldPICEntry);
            entry = fieldPICEntry;
            classID = fieldClassID;
            objectAddress = fieldAddress;
            oirClassDecl = fieldClassDecl;

            if (oirClassDecl.isMethodInClass(methodName)) {
                return new MethodAddress(oirClassDecl.getName(), objectAddress);
            }
        }

        /* Now start looking in the object's fields */
        while (true) {
            oirClassDecl = getClassDeclaration(classID);
            fieldPos = oirClassDecl.getForwardMethodFieldHashMap(methodName);
            if (fieldPos == -1) {
                System.out.println("Error: Cannot find method in any of the fields");
                System.exit(-1);
            }
            fieldAddress = ForwardNative.getFieldAddress(oirClassDecl.getName(), objectAddress, fieldPos);
            fieldClassID = ForwardNative.getObjectClassID(fieldAddress);
            OIRClassDeclaration fieldClassDecl = getClassDeclaration(fieldClassID);
            PICEntry fieldPICEntry = new PICEntry(fieldClassID, fieldClassDecl);
            if (oirClassDecl.getFieldDeclarationForPos(fieldPos).isFinal()) {
                entry.setIsFinal(true);
                if (lastFinalEntry == null) {
                    lastFinalEntry = entry;
                    lastFinalObjAddress = objectAddress;
                }
            } else {
                if (lastFinalEntry != null && lastFinalObjAddress != -1) {
                    lastFinalEntry.setFinalObjectAddress(lastFinalObjAddress, fieldAddress, fieldPICEntry);
                }
                lastFinalEntry = null;
                lastFinalObjAddress = -1;
            }
            entry.setFeildPos(fieldPos);
            entry.addChildEntry(fieldClassID, fieldPICEntry);
            entry = fieldPICEntry;
            classID = fieldClassID;
            objectAddress = fieldAddress;
            oirClassDecl = fieldClassDecl;
            if (oirClassDecl.isMethodInClass(methodName)) {
                return new MethodAddress(oirClassDecl.getName(), objectAddress);
            }
        }
    }

    public MethodAddress forwardHashTableNaive(long objectAddress, int classID, String methodName) {
        OIRClassDeclaration oirClassDecl = getClassDeclaration(classID);
        boolean ans = oirClassDecl.isMethodInClass(methodName);
        if (ans) {
            return new MethodAddress(oirClassDecl.getName(), objectAddress);
        }
        while (true) {
            int fieldPos = oirClassDecl.getForwardMethodFieldPosNaive(methodName);
            if (fieldPos == -1) {
                System.out.println("Error: Cannot find method");
                System.exit(-1);
            }
            objectAddress = ForwardNative.getFieldAddress(oirClassDecl.getName(), objectAddress, fieldPos);
            classID = ForwardNative.getObjectClassID(objectAddress);
            oirClassDecl = getClassDeclaration(classID);
            if (oirClassDecl.isMethodInClass(methodName)) {
                return new MethodAddress(oirClassDecl.getName(), objectAddress);
            }
        }
    }

//    public void buildStaticPIC ()
//    {
//        methodStaticPICMap = new HashMap<String, OIRStaticPIC> ();
//
//        for (OIRType decl : typeDeclarations)
//        {
//            if (!(decl instanceof OIRClassDeclaration))
//                continue;
//
//            OIRClassDeclaration classDecl;
//
//            classDecl = (OIRClassDeclaration)decl;
//
//            for (OIRMemberDeclaration memDecl : classDecl.getMembers())
//            {
//                if (!(memDecl instanceof OIRMethod))
//                    continue;
//
//                OIRMethod method = (OIRMethod)memDecl;
//                String name = method.getDeclaration().getName();
//                OIRStaticPIC pic;
//
//                if (methodStaticPICMap.containsKey(name))
//                {
//                    pic = methodStaticPICMap.get(name);
//                }
//                else
//                {
//                    pic = new OIRStaticPIC (name);
//                    methodStaticPICMap.put(name, pic);
//                }
//
//                pic.addClassName(classDecl.getName());
//            }
//        }
//    }
}
