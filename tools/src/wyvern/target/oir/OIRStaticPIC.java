package wyvern.target.oir;

import java.util.HashSet;

public class OIRStaticPIC {
    private HashSet<String> classesWithMethod;

    public OIRStaticPIC(HashSet<String> classesWithMethod, String methodName) {
        this.classesWithMethod = classesWithMethod;
    }

    public OIRStaticPIC(String methodName) {
        this.classesWithMethod = new HashSet<String>();
    }

    public boolean containsClass(String className) {
        return classesWithMethod.contains(className);
    }

    public void addClassName(String name) {
        classesWithMethod.add(name);
    }
}
