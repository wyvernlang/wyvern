package wyvern.targets.Java.visitors;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.V1_7;

import java.io.IOException;
import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.FunDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;

public class JavaGenerator {

	private static ClassStore doCompile(TypedAST input) {
		ClassStore cs = new ClassStore();
		ClassVisitor cv = new ClassVisitor("", cs);
		ArrayList<Declaration> decls = new ArrayList<Declaration>();
		decls.add(new FunDeclaration("main", new ArrayList<NameBinding>(), input.getType(), input, true, FileLocation.UNKNOWN));
		cv.visit(new ClassDeclaration("wycCode", null, null, new DeclSequence(decls), FileLocation.UNKNOWN));
		return cs;
	}

	public static ClassLoader GenerateBytecode(TypedAST input) {
		ClassStore cs = doCompile(input);
		return cs.getLoader();
	}

	public static void generateFiles(TypedAST input, String outputDirectory) throws IOException {
		ClassStore cs = doCompile(input);
		cs.writeToDirectory(outputDirectory);
	}
}
