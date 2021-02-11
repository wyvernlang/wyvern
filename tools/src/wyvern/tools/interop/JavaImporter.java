package wyvern.tools.interop;

import static wyvern.tools.errors.ToolError.reportError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import wyvern.stdlib.Globals;
import wyvern.stdlib.support.Pure;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class JavaImporter {

    private TypeContext ctx;

    public JavaImporter(TypeContext ctx) {
        this.ctx = ctx;
    }

    public FObject find(String qualifiedName, HasLocation errorLocation) throws ReflectiveOperationException {

        // The qualified name could either be a static variable, or a class name.
        // Assume it's a class
        boolean isField = false;
        JObject obj = null;
        try {

            // Load class without initialization to avoid unsafe side effects
            Class<?> cls = java.lang.Class.forName(qualifiedName,false, ClassLoader.getSystemClassLoader());
            // Unclear if this is ever used in practice. It creates a "Class" object in wyvern,
            // which is not usually intended.
            obj = new JObject(cls);
        } catch (ReflectiveOperationException e1) {
            isField = true;
        }

        // Now, let's handle the execution path where the exception is thrown
        if (isField) {
            try {
                // Let's try to find it as a class field name
                int lastDot = qualifiedName.lastIndexOf('.');
                String fieldName = qualifiedName.substring(lastDot + 1);
                String className = qualifiedName.substring(0, lastDot);
                System.out.println("Before load");
                // Load class without initialization to avoid unsafe side effects
                Class<?> cls = java.lang.Class.forName(className,false, ClassLoader.getSystemClassLoader());
                System.out.println("After load, before init");
                // check whether class is declared pure or not
                // note the annotations are trusted, not checked.
                Annotation[] annotations = cls.getAnnotations();
                boolean classInitIsPure = Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof Pure);
                boolean javaCapabilityPresent = ctx.isPresent("java", true);
                boolean classInWhitelist = Globals.checkSafeJavaImport(qualifiedName);
                if (!classInitIsPure && !javaCapabilityPresent && !classInWhitelist) {
                    ToolError.reportError(ErrorMessage.UNSAFE_JAVA_IMPORT, errorLocation, qualifiedName);
                }
                // continue and initialize the class and load the field.
                // else, error. require the java capability to proceed.
                Field field = cls.getField(fieldName);

                if (!Modifier.isStatic(field.getModifiers())) {
                    reportError(ErrorMessage.IMPORT_MUST_BE_STATIC_FIELD, errorLocation, qualifiedName);
                }
                // class initialization happens here.
                Object result = field.get(null);
                obj = new JObject(result);
            } catch (ReflectiveOperationException e1) {
                // Well, we tried!
                throw e1;
            }
        }

        return obj;
    }
}
