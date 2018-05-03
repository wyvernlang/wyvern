package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public final class GenUtil {
    private GenUtil() { }
    public static final String javaTypesObjectName = "java$types";
    private static final Variable javaTypesObject = new Variable(javaTypesObjectName);
    private static ValueType javaTypes = null;

    public static ValueType javaClassToWyvernType(Class<?> javaClass, TypeContext ctx) {
        //return javaClassToWyvernTypeRec(javaClass, new HashSet<String>());
        // TODO: extend to types other than int, and structural types based on that
        if (javaClass.getName().equals("int")) {
            return Util.intType();
        }

        if (javaClass.getName().equals("java.math.BigInteger")) {
            return Util.intType();
        }

        if (javaClass.getName().equals("java.lang.String")) {
            return Util.stringType();
        }

        if (javaClass.getName().equals("boolean")) {
            return Util.booleanType();
        }

        // TODO: might be unnecessary
        if (javaClass.getName().equals("wyvern.target.corewyvernIL.expression.ObjectValue")) {
            return Util.dynType();
        }

        // TODO: might be unnecessary
        if (javaClass.getName().equals("java.lang.Object")) {
            return Util.dynType();
        }

        if (javaClass.getName().equals("java.util.List")
            || javaClass.getName().equals("java.util.LinkedList") || javaClass.getName().equals("java.util.ArrayList")) {
            StructuralTypesFromJava type = (StructuralTypesFromJava) ctx.lookupTypeOf(javaTypesObjectName);
            type.getJavaType(javaClass, ctx); // run for side effect - makes sure this java type is in the contxt
            return Util.listType();
        }

        StructuralTypesFromJava type = (StructuralTypesFromJava) ctx.lookupTypeOf(javaTypesObjectName);
        return type.getJavaType(javaClass, ctx);
    }

    public static Variable getJavaTypesObject() {
        return javaTypesObject;
    }

    public static GenContext ensureJavaTypesPresent(GenContext ctx) {
        if (ctx.isPresent(javaTypesObjectName, true)) {
            return ctx;
        }
        // we just reuse the Java structural types object
        // no harm in this provided we aren't loading multiple versions of the same Java class
        if (javaTypes == null) {
            javaTypes = new StructuralTypesFromJava();
        }
        return ctx.extend(javaTypesObjectName, javaTypesObject, javaTypes);
    }
}
