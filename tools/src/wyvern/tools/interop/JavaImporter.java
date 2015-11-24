package wyvern.tools.interop;

import java.lang.reflect.Field;

public class JavaImporter implements Importer {

	@Override
	public FObject find(String qualifiedName) throws ReflectiveOperationException {
		int lastDot = qualifiedName.lastIndexOf('.');
		String fieldName = qualifiedName.substring(lastDot+1);
		String className = qualifiedName.substring(0, lastDot);
		
		Class cls = java.lang.Class.forName(className);
		Field field = cls.getField(fieldName);
		Object result = field.get(null);
		
		return new JObject(result);
	}

}
