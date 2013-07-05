package wyvern.targets.Java.visitors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.*;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class ClassStore {
	private class ObjectType implements Type {

		@Override
		public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean subtype(Type other) {
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

        @Override
        public LineParser getParser() {
            return null;
        }

        @Override
		public void writeArgsToTree(TreeWriter writer) {
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean isSimple() {
			// TODO Auto-generated method stub
			return true;
		}
	}
    private class GenericClassType implements Type {
        public final Type parent;
        public final int n;

        public GenericClassType(Type parent, int n) {
            this.parent = parent;
            this.n = n;
        }

        @Override
        public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
            return false;
        }

        @Override
        public boolean subtype(Type other) {
            return false;
        }

        @Override
        public LineParser getParser() {
            return null;
        }

        @Override
        public void writeArgsToTree(TreeWriter writer) {
        }
		@Override
		public boolean isSimple() {
			// TODO Auto-generated method stub
			return true;
		}
    }
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

    public String getNewTypeName(Type type, String postfix) {
        String output = mangleTypeName(type) + postfix;
        while (classNames.contains(output))
            output += "$";
        classNames.add(output);
        return output;
    }

	public String getNewTypeName(Type type) {
        return getNewTypeName(type, "");
	}

	Type getObjectType() {
		return new ObjectType();
	}

	public String getTypeName(Type type, boolean isUnitVoid) {
		return getTypeName(type, isUnitVoid, false);
	}

	public String getTypeName(Type type, boolean isUnitVoid, boolean isArrowMethodHandle) {
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
				outputBuilder.append(getTypeName(t, isUnitVoid, isArrowMethodHandle));
			}
			return outputBuilder.toString();
		} else if (type instanceof Arrow && !isArrowMethodHandle) {
			return "(" + getTypeName(((Arrow) type).getArgument(), false, true) + ")" + getTypeName(((Arrow) type).getResult(), true, true);
		} else if (type instanceof Arrow && isArrowMethodHandle) {
			return "Ljava/lang/invoke/MethodHandle;";
        } else if (type instanceof TypeType || type instanceof ObjectType) {
            return "Ljava/lang/Object;";
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

    public Type registerGenericClass(Type type, int n) {
        Type gct = new GenericClassType(type, n);
        classes.put(gct, new Pair<String, byte[]>(getNewTypeName(type, "$"+n), null));
        return gct;
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

		ByteClassLoader byteClassLoader = new ByteClassLoader(nc, ClassStore.class.getClassLoader());
		return byteClassLoader;
	}

	public void writeToDirectory(String directory) throws IOException {
		for (Pair<String, byte[]> pair : classes.values()) {
			FileOutputStream fso = new FileOutputStream(new File(directory, pair.first+".class"));
			fso.write(pair.second);
			fso.close();
		}
	}

}
