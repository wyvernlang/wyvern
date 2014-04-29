package wyvern.targets;


import org.objectweb.asm.signature.SignatureVisitor;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TargetManager {
	private static Map<String, Target> targets = new HashMap<>();

	public Target getTarget(String targetName) {
		return targets.get(targetName);
	}

	public Pair<String,String> test(int t) {
		return new Pair<>("","");
	}

	@Test
	public void tiny() throws IOException {
		Class c = TargetManager.class;
		String className = c.getName();
		String classAsPath = className.replace('.', '/') + ".class";
		InputStream asStream = c.getClassLoader().getResourceAsStream("wyvern/targets/TargetManager.class");
		ClassReader cr = new ClassReader(c.getCanonicalName());

		ASMifier ifier = new ASMifier();
		cr.accept(new TraceClassVisitor(null, ifier, new PrintWriter(System.out)),0);
	}

	@Test
	public void tiny2() {
		SignatureWriter sw = new SignatureWriter();
		sw.visitClassType(Type.getInternalName(Pair.class));
		SignatureVisitor sw2 = sw.visitTypeArgument('=');
		sw2.visitClassType(Type.getInternalName(String.class));
		sw2.visitEnd();
		sw.visitEnd();
		String res = sw.toString();
	}
}
