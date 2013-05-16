package wyvern.targets.Java.visitors;

import java.io.FileOutputStream;
import java.util.*;

import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class ClassStore {
	private static class ByteClassLoader extends ClassLoader {
		private final Map<String, byte[]> extraClassDefs;

		public ByteClassLoader(Map<String, byte[]> extraClassDefs, ClassLoader parent) {
			super(parent);
			this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			byte[] classBytes = this.extraClassDefs.remove(name);
			if (classBytes != null) {
				return defineClass(name, classBytes, 0, classBytes.length); 
			}
			return super.findClass(name);
		}

	}
	private Hashtable<Type, Pair<String, byte[]>> classes = new Hashtable<Type, Pair<String, byte[]>>();
    private HashSet<String> classNames = new HashSet<String>();

    public String mangleTypeName(Type type) {
        return type.toString().replace(" ", "");
    }

    public String getRawTypeName(Type type) {
        return classes.get(type).first;
    }

	public String getNewTypeName(Type type) {
        String output = mangleTypeName(type);
        while (classNames.contains(output))
            output += "$";
        classNames.add(output);
        return output;
	}

	public String getTypeName(Type type, boolean isUnitVoid) {
		if (type instanceof Int) {
			return "I";
		} else if (type instanceof Bool) {
			return "Z";
		} else if (type instanceof Str) {
			return "Ljava/lang/String;";
		} else if (type instanceof Unit) {
			if (isUnitVoid)
				return "V";
			else
				return "";
		} else if (type instanceof Tuple) {
			StringBuilder outputBuilder = new StringBuilder();
			
			for (Type t : ((Tuple)type).getTypes()) {
				outputBuilder.append(getTypeName(t, isUnitVoid));
			}
			return outputBuilder.toString();
		} else if (type instanceof Arrow) {
			return "(" + getTypeName(((Arrow) type).getArgument(), false) + ")" + getTypeName(((Arrow) type).getResult(), true);
		} else if (classes.containsKey(type)) {
			return "L"+classes.get(type).first+";";
		}
		return "L"+mangleTypeName(type)+";";
	}

	public String getUnmangledClassName(ClassType type) {
		return classes.get(type).first;
	}
	
	public void registerClass(Type type) {
		classes.put(type, new Pair<String, byte[]>(getNewTypeName(type), null));
		
	}

	public void registerClass(Type type, byte[] bytecode) {
        if (classes.containsKey(type) && classes.get(type).second == null)
		    classes.put(type, new Pair<String, byte[]>(classes.get(type).first, bytecode));
        else
            throw new RuntimeException("Tried to create a second identical class");
	}
	

	
	public ClassLoader getLoader() {
		Hashtable<String, byte[]> nc = new Hashtable<String, byte[]>();
		for (Pair<String, byte[]> pair : classes.values()) {
			nc.put(pair.first, pair.second);
			try {
				FileOutputStream fso = new FileOutputStream(pair.first+".class");
				fso.write(pair.second);
				fso.close();
			} catch (Exception e) {
				
			}
		}
	
		return new ByteClassLoader(nc, ClassStore.class.getClassLoader());
	}

}
