package wyvern.targets.Java.visitors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.MethDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.V1_7;

public class ClassVisitor extends BaseASTVisitor {

	private String typePrefix;
	private ClassStore store = null;
	private ExternalContext context = new ExternalContext();
	

	public ClassVisitor(String typePrefix, ClassStore store) {
		this.typePrefix = typePrefix;
		this.store = store;
		context = new ExternalContext();
	}
	
	
	public ClassVisitor(String typePrefix, ClassVisitor pcv, List<Pair<String, Type>> list) {
		this.typePrefix = typePrefix;
		this.store = pcv.store;
		context.setVariables(list, ExternalContext.EXTERNAL);
	}

	private ClassWriter cw = null;
	
	private String mangleTypeName(Type type) {
		return store.mangleTypeName(type);
	}
	
	private String getTypeName(Type type) {
		return getTypeName(type, true);
	}
	
	private String getTypeName(Type type, boolean isUnitVoid) {
		return store.getTypeName(type, isUnitVoid);
	}
	
	private void registerClass(Type type) {
		store.registerClass(type);
	}
	
	private void registerClass(Type type, byte[] bytecode) {
		store.registerClass(type, bytecode);
	}
	
	private Type currentType;
	private String currentTypeName;
	
	@Override
	public void visit(ClassDeclaration classDecl) {
		// No support for inner classes (yet)
		for (Declaration decl : classDecl.getDecls().getDeclIterator()) {
			context.setVariable(decl.getName(), decl.getType(), ExternalContext.INTERNAL);
		}

        registerClass(classDecl.getType());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS); // Makes it a LOT slower. Easier, though. TODO: Actually implement own stack/var size determination.
		cw.visit(V1_7, 
				ACC_PUBLIC,
                store.getRawTypeName(classDecl.getType()),
				null, 
				"java/lang/Object", 
				null);
		
		currentType = classDecl.getType();
		currentTypeName = store.getRawTypeName(classDecl.getType());

		addDefaultConstructor();
		
		super.visit(classDecl);
		
		for (Pair<String, Type> externalVar : context.getExternalDecls()) {

			cw.visitField(ACC_PUBLIC | ACC_STATIC, externalVar.first, 
					getTypeName(externalVar.second),
					null, 
					new Integer(0)).visitEnd();
		}
		
		cw.visitEnd();
		registerClass(classDecl.getType(), cw.toByteArray());
	}

	private void addDefaultConstructor() {
		MethodVisitor defaultCstr = cw.visitMethod(ACC_PRIVATE, 
				"<init>", 
				"()V", 
				null,
				null);
		defaultCstr.visitCode();
		defaultCstr.visitIntInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
		defaultCstr.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		defaultCstr.visitInsn(org.objectweb.asm.Opcodes.RETURN);
		defaultCstr.visitMaxs(0, 0);//Unused
		defaultCstr.visitEnd();
	}
	
	@Override
	public void visit(Sequence sequence) {
		if (sequence instanceof DeclSequence)
			for (Declaration decl : ((DeclSequence)sequence).getDeclIterator()) {
				if (decl instanceof ClassDeclaration)
					store.registerClass(decl.getType());
			}
		
		super.visit(sequence);
	}

	@Override
	public void visit(ValDeclaration valDeclaration) {
		super.visit(valDeclaration);
		cw.visitField(ACC_PRIVATE,
				valDeclaration.getName(), 
				getTypeName(valDeclaration.getBinding().getType()),
				null, 
				new Integer(0)).visitEnd();
	}

	@Override
	public void visit(VarDeclaration valDeclaration) {
		super.visit(valDeclaration);
		cw.visitField(ACC_PUBLIC,
				valDeclaration.getName(), 
				getTypeName(valDeclaration.getBinding().getType()),
				null, 
				new Integer(0)).visitEnd();
	}
	
	@Override
	public void visit(MethDeclaration methDeclaration) {
		int access = ACC_PUBLIC;
		if (methDeclaration.isClassMeth())
			access += ACC_STATIC;
		
		MethodVisitor mv = cw.visitMethod(access, methDeclaration.getName(),
				getTypeName(methDeclaration.getType(), false), null, null);
		new MethVisitor(this, mv, context.getExternalDecls()).visit(methDeclaration);
	}

	public String getCurrentTypeName() {
		return currentTypeName;
	}

	public Type getCurrentType() {
		return currentType;
	}


	public ClassStore getStore() {
		return store;
	}
}
