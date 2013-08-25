package wyvern.targets.Java.visitors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
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
	private Hashtable<Declaration, Pair<String, byte[]>> classes = new Hashtable<>(); //Only Class or TypeDeclaration
	private Hashtable<Type, String> registeredTypes = new Hashtable<>();
    private HashSet<String> classNames = new HashSet<String>();

    public String mangleTypeName(Type type) {
        return type.toString().replace(" ", "");
    }

	public String getRawTypeName(Type type) {
		return registeredTypes.get(type);
	}

    public String getRawTypeName(ClassDeclaration decl) {
        return classes.get(decl).first;
    }

    public String getNewTypeName(Type type, String postfix) {
        String output = mangleTypeName(type) + postfix;
        while (classNames.contains(output))
            output += "$";
        classNames.add(output);
        return output;
    }

	public String getNewTypeName(Declaration decl, String postfix) {
		String output = decl.getName().replace(" ", "") + postfix;
		while (classNames.contains(output))
			output += "$";
		classNames.add(output);
		return output;
	}

	public String getNewTypeName(Type type) {
        return getNewTypeName(type, "");
	}
	public String getNewTypeName(Declaration decl) {
		return getNewTypeName(decl, "");
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
		} else if (registeredTypes.containsKey(type)) {
			return "L"+registeredTypes.get(type)+";";
		}
		return "L"+mangleTypeName(type)+";";
	}

	public String getUnmangledClassName(ClassType type) {
		return classes.get(type).first;
	}
	
	public void registerClass(ClassDeclaration decl) {
		if (!classes.containsKey(decl)) {
			String typeName = getNewTypeName(decl);
			classes.put(decl, new Pair<String, byte[]>(typeName, null));
			registeredTypes.put(decl.getType(), typeName);
		}
	}

	public void registerClass(ClassDeclaration decl, byte[] bytecode) {
		if (classes.containsKey(decl) && classes.get(decl).second == null) {
			classes.put(decl, new Pair<String, byte[]>(classes.get(decl).first, bytecode));
			registeredTypes.put(decl.getType(), classes.get(decl).first);
		} else
			throw new RuntimeException("Tried to create a second identical class");
	}



	public void registerClass(TypeDeclaration decl) {
		if (!classes.containsKey(decl)) {
			String typeName = getNewTypeName(decl.getType());
			classes.put(decl, new Pair<String, byte[]>(typeName, null));
			registeredTypes.put(decl.getType(), typeName);
		}
	}

	public void registerClass(TypeDeclaration decl, byte[] bytecode) {
		if (classes.containsKey(decl) && classes.get(decl).second == null) {
			classes.put(decl, new Pair<String, byte[]>(classes.get(decl).first, bytecode));
			registeredTypes.put(decl.getType(), classes.get(decl).first);
		} else
			throw new RuntimeException("Tried to create a second identical class");
	}
	
	public ClassLoader getLoader() {
		Hashtable<String, byte[]> nc = new Hashtable<String, byte[]>();
		for (Pair<String, byte[]> pair : classes.values()) {
			nc.put(pair.first, pair.second);
			/*
			try {
				FileOutputStream fso = new FileOutputStream(pair.first+".class");
				fso.write(pair.second);
				fso.close();
			} catch (Exception e) {

			}
			*/
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
