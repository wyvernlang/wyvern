package wyvern.target.oir.declarations;

import java.util.HashSet;
import java.util.List;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRInterface extends OIRType {
    private String name;
    private List<OIRMethodDeclaration> methods;
    private String selfName;
    private HashSet<String> methodHashSet;

    public OIRInterface(OIREnvironment environment, String name, String selfName, List<OIRMethodDeclaration> methods) {
        super(environment);
        this.name = name;
        this.methods = methods;
        this.selfName = selfName;
        methodHashSet = new HashSet<String>();

        if (methods != null) {
            for (OIRMethodDeclaration method : methods) {
                methodHashSet.add(method.getName());
            }
        }
    }

    public boolean isMethodInClass(String method) {
        return methodHashSet.contains(method);
    }

    public String getSelfName() {
        return selfName;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OIRMethodDeclaration> getMethods() {
        return methods;
    }

    public void addMethod(OIRMethodDeclaration method) {
        methods.add(method);
    }

    public void setMethods(List<OIRMethodDeclaration> methods) {
        this.methods = methods;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
    public OIRType getTypeForMember(String methodName) {
        for (OIRMethodDeclaration methDecl : methods) {
            if (methDecl.getName() == methodName) {
                return methDecl.getReturnType();
            }
        }
        return null;
    }
}
