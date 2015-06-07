package wyvern.targets.java;

import org.objectweb.asm.signature.SignatureWriter;
import org.javatuples.*;
import org.junit.Test;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import sun.reflect.generics.tree.ClassSignature;
import wyvern.tools.types.extensions.*;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class GenerationContext {
	private HashMap<String, byte[]> classes = new HashMap<>();
	private HashMap<wyvern.tools.types.Type, String> names = new HashMap<>();
	private HashSet<String> takenNames = new HashSet<>();
	public void getSignature(wyvern.tools.types.Type type, SignatureVisitor sig, boolean baseAllowed) {
		if (type instanceof Arrow) {
			wyvern.tools.types.Type argument = ((Arrow) type).getArgument();
			if (argument instanceof Unit) {
				sig.visitClassType(Type.getInternalName(Supplier.class));
				SignatureVisitor sw2 = sig.visitTypeArgument('=');
				getSignature(((Arrow) type).getResult(), sw2, true);
				sw2.visitEnd();
				return;
			}
			if (argument instanceof Tuple) {
				wyvern.tools.types.Type[] types = ((Tuple) argument).getTypeArray();
				if (types.length == 2) {
					sig.visitClassType(Type.getInternalName(BiFunction.class));
					SignatureVisitor sw2 = sig.visitTypeArgument('=');
					getSignature(types[0], sw2, false);
					getSignature(types[1], sw2, false);
					getSignature(((Arrow) type).getResult(), sw2, false);
					sw2.visitEnd();
					return;
				}
			}
			if (argument instanceof Int) {
				sig.visitClassType(Type.getInternalName(IntFunction.class));
				SignatureVisitor sw2 = sig.visitTypeArgument('=');
				getSignature(((Arrow) type).getResult(), sw2, true);
				sw2.visitEnd();
				return;
			}
			sig.visitClassType(Type.getInternalName(Function.class));
			SignatureVisitor sw2 = sig.visitTypeArgument('=');
			getSignature(argument, sw2, false);
			getSignature(((Arrow) type).getResult(), sw2, false);
			sw2.visitEnd();
		} else if (type instanceof Tuple) {
			Class genTgt = null;
			switch (((Tuple) type).getTypeArray().length) {
				case 0: genTgt = null; break;
				case 1: genTgt = null; break;
				case 2: genTgt = Pair.class; break;
				case 3: genTgt = Triplet.class; break;
				case 4: genTgt = Quartet.class; break;
				case 5: genTgt = Quintet.class; break;
				case 6: genTgt = Sextet.class; break;
				case 7: genTgt = Septet.class; break;
				case 8: genTgt = Octet.class; break;
				case 9: genTgt = Ennead.class; break;
				case 10: genTgt = Decade.class; break;
				default: throw new RuntimeException();
			}
			sig.visitClassType(Type.getInternalName(genTgt));
			SignatureVisitor sw2 = sig.visitTypeArgument('=');
			for (wyvern.tools.types.Type tpe : ((Tuple) type).getTypeArray())
				getSignature(tpe, sw2, false);
			sw2.visitEnd();
			return;
		} else {
			Type rType = getType(type, baseAllowed);
			if (rType.getClassName() != null) {
				sig.visitClassType(rType.getClassName());
				sig.visitEnd();
			}
			sig.visitBaseType(rType.getDescriptor().charAt(0));
		}
	}

	@Test
	public void testSig1() {
		SignatureWriter sw = new SignatureWriter();
		wyvern.tools.types.Type toGen = new Arrow(new Tuple(new wyvern.tools.types.Type[]{new Int(), new Int(), new Bool()}), new Bool());
		getSignature(toGen, sw, true);
		String res = sw.toString();
		ClassSignature mts = sun.reflect.generics.parser.SignatureParser.make().parseClassSig(res);
	}

	public Type getType(wyvern.tools.types.Type type, boolean baseAllowed) {
		if (type instanceof Bool) {
			if (baseAllowed)
				return Type.getType(Boolean.TYPE);
			else
				return Type.getType(Boolean.class);
		} else if (type instanceof Int) {
			if (baseAllowed)
				return Type.getType(Integer.TYPE);
			else
				return Type.getType(Integer.class);
		} else if (type instanceof Str) {
			return Type.getType(String.class);
		} else if (type instanceof ClassType) {
			return Type.getType(names.get(type));
		}

		return null;
	}

	public Type[] getTypes(wyvern.tools.types.Type type) {
		if (type instanceof Tuple) {
			return Arrays.asList(((Tuple) type).getTypeArray()).stream().map(tpe -> getType(tpe,false)).toArray(Type[]::new);
		}
		if (type instanceof Unit) {
			return new Type[0];
		}
		return new Type[] {getType(type,false)};
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

	public void registerTypename(wyvern.tools.types.Type inT, String wantName) {
		if (takenNames.contains(wantName)) {
			int step = 0;
			while (takenNames.contains("L$"+step+"$"+wantName+";"))
				step++;
			names.put(inT, "L$"+step+"$"+wantName+";");
		}
		names.put(inT, wantName);
	}
}
