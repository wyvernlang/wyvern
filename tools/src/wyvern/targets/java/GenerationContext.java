package wyvern.targets.java;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;

public class GenerationContext {
	private HashMap<String, byte[]> classes = new HashMap<>();
	public Type getType(wyvern.tools.types.Type type) {
		return null;
	}

	public Type[] getTypes(wyvern.tools.types.Type type) {
		return null;
	}

	public ClassVisitor newClass(String rootClassname) {
		if (classes.containsKey(rootClassname))
			throw new RuntimeException();

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		return new ClassVisitor(Opcodes.V1_7, writer) {
			@Override
			public void visitEnd() {
				super.visitEnd();
				classes.put(rootClassname, writer.toByteArray());
			}
		};
	}
}
