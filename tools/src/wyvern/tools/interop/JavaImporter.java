package wyvern.tools.interop;

import static wyvern.tools.errors.ToolError.reportError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;

public class JavaImporter implements Importer {

    @Override
    public FObject find(String qualifiedName, HasLocation errorLocation) throws ReflectiveOperationException {

        // The qualified name could either be a static variable, or a class name.
        // Assume it's a class
        boolean isField = false;
        JObject obj = null;
        try {
            Class cls = java.lang.Class.forName(qualifiedName);
            obj = new JObject(cls);
        } catch (ReflectiveOperationException e1) {
            isField = true;
        }

        // Now, let's handle the execution path where the exception is thrown
        if(isField) {
            try {
                // Let's try to find it as a class field name
                int lastDot = qualifiedName.lastIndexOf('.');
                String fieldName = qualifiedName.substring(lastDot+1);
                String className = qualifiedName.substring(0, lastDot);
                Class cls = java.lang.Class.forName(className);
                Field field = cls.getField(fieldName);
                if (!Modifier.isStatic(field.getModifiers())) {
        			reportError(ErrorMessage.IMPORT_MUST_BE_STATIC_FIELD, errorLocation, qualifiedName);
                }
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
