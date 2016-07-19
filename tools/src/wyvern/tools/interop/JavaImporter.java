package wyvern.tools.interop;

import java.lang.reflect.Field;

public class JavaImporter implements Importer {

    @Override
    public FObject find(String qualifiedName) throws ReflectiveOperationException {

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
