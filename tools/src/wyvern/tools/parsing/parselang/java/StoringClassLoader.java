package wyvern.tools.parsing.parselang.java;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import wyvern.tools.util.Pair;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

//Largely from http://www.ibm.com/developerworks/java/library/j-jcomp/index.html
public class StoringClassLoader extends ClassLoader {
	private final Map<String, JavaFileObject> classes = new HashMap<>();

	public StoringClassLoader(ClassLoader parent) {
		super(parent);
	}

	public void applyTransformer(Predicate<String> chooser, Function<ClassWriter, ClassVisitor> transformer) {
		Pair<String, byte[]> toTransform = (classes.entrySet().stream().filter(ent->chooser.test(ent.getKey()))
				.findFirst().map(entry->new Pair<String,byte[]>(entry.getKey(), ((CachingJavaFileObject)entry.getValue()).getByteCode()))
				.orElseThrow(()-> new RuntimeException()));
		ClassWriter target = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		ClassVisitor inner = transformer.apply(target);
		new ClassReader(toTransform.second).accept(inner, 0);
		classes.put(toTransform.first, new CachingJavaFileObject(toTransform.first, target.toByteArray()));
	}

	public void add(String qualifiedName, JavaFileObject file) {
		classes.put(qualifiedName, file);
	}

	public Collection<? extends JavaFileObject> files() {
		return Collections.unmodifiableCollection(classes.values());
	}

	@Override
	protected Class<?> findClass(final String qualifiedClassName)
			throws ClassNotFoundException {
		JavaFileObject file = classes.get(qualifiedClassName);
		if (file != null) {
			byte[] bytes = ((CachingJavaFileObject) file).getByteCode();
			return defineClass(qualifiedClassName, bytes, 0, bytes.length);
		}
		// Workaround for "feature" in Java 6
		// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434149
		try {
			Class<?> c = Class.forName(qualifiedClassName);
			return c;
		} catch (ClassNotFoundException nf) {
			// Ignore and fall through
		}
		return super.findClass(qualifiedClassName);
	}
}
