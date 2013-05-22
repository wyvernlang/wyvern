package wyvern.targets.Java.visitors;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.util.CheckClassAdapter;
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
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

import static org.objectweb.asm.Opcodes.*;

public class ClassVisitor extends BaseASTVisitor {
	private String typePrefix;
	private ClassStore store = null;
	private ExternalContext context = new ExternalContext();
	private List<MethDeclaration> meths = new ArrayList<MethDeclaration>();
	private Map<String, TypedAST> initalizeFieldValues = new HashMap<String,TypedAST>();
	private int anonMethNum = 0;
	private org.objectweb.asm.ClassVisitor cw = null;
	private ClassWriter writer = null;

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

    public Iterable<Pair<String, Type> > getExternalVars() {
        return context.getExternalDecls();
    }


	public String getAnonMethodName() {
		return "func$"+anonMethNum++;
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

	private Type checkForClassType(Type type) {
		if (type instanceof ClassType)
			return store.getObjectType();
		else
			return type;
	}

	private void pushClassType(MethodVisitor mv, String descriptor) {
		if (descriptor.equals("V")) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
		} else if (descriptor.equals("I")) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
		} else
			mv.visitLdcInsn(org.objectweb.asm.Type.getType(descriptor));
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

		writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // Makes it a LOT slower. Easier, though. TODO: Actually implement own stack/var size determination.
		cw = new CheckClassAdapter(writer, false);
		cw.visit(V1_7,
				ACC_PUBLIC,
                store.getRawTypeName(classDecl.getType()),
				null, 
				"java/lang/Object", 
				null);
		
		currentType = classDecl.getType();
		currentTypeName = store.getRawTypeName(classDecl.getType());

		super.visit(classDecl);

		addDefaultConstructor();
		
		for (Pair<String, Type> externalVar : context.getExternalDecls()) {

			cw.visitField(ACC_PUBLIC | ACC_STATIC, externalVar.first + "$stat",
					getTypeName(externalVar.second),
					null, 
					new Integer(0)).visitEnd();

            cw.visitField(ACC_PRIVATE, externalVar.first + "$dyn",
                    getTypeName(externalVar.second),
                    null,
                    new Integer(0)).visitEnd();
		}
		initializeMethodHandles();
		cw.visitEnd();
		registerClass(classDecl.getType(), writer.toByteArray());
	}

	private void initializeMethodHandles() {
		if (meths.isEmpty())
			return;
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		Label start = new Label();
		Label exceptionBlock = new Label();
		Label exceptionEnd = new Label();
		Label handler = new Label();
		mv.visitTryCatchBlock(exceptionBlock, exceptionEnd, handler, "java/lang/Exception");
		Label returnStatement = new Label();

		mv.visitLabel(start);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;");
		mv.visitVarInsn(ASTORE, 0);
		pushClassType(mv, store.getTypeName(currentType, true));
		mv.visitVarInsn(ASTORE, 1);

		mv.visitLabel(exceptionBlock);

		for (MethDeclaration md : meths) {
			mv.visitVarInsn(ALOAD,0);
			mv.visitVarInsn(ALOAD,1);
			mv.visitLdcInsn(md.getName());
			StringBuilder sb = new StringBuilder("(Ljava/lang/Class;");
			Arrow arrow = (Arrow)md.getType();
			pushClassType(mv, store.getTypeName(arrow.getResult(), true, true));
			if (arrow.getArgument() instanceof Tuple) {
				Type[] types = ((Tuple)arrow.getArgument()).getTypes();
				makeAndPopulateTypes(mv, sb, types);
			} else if (!(arrow.getArgument() instanceof Unit)) {
				sb.append("Ljava/lang/Class;");
				pushClassType(mv, store.getTypeName(arrow.getArgument(), true, true));
			}
			sb.append(")Ljava/lang/invoke/MethodType;");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType","methodType",sb.toString());
			if (!md.isClassMeth())
				mv.visitMethodInsn(INVOKEVIRTUAL,
						"java/lang/invoke/MethodHandles$Lookup",
						"findVirtual",
						"(Ljava/lang/Class;" +
								"Ljava/lang/String;" +
								"Ljava/lang/invoke/MethodType;)" +
								"Ljava/lang/invoke/MethodHandle;");
			else
				mv.visitMethodInsn(INVOKEVIRTUAL,
						"java/lang/invoke/MethodHandles$Lookup",
						"findStatic",
						"(Ljava/lang/Class;" +
								"Ljava/lang/String;" +
								"Ljava/lang/invoke/MethodType;)" +
								"Ljava/lang/invoke/MethodHandle;");
			//Just the methodhandle on the stack here

			sb = new StringBuilder("(Ljava/lang/Class;");// Construct the return type argument
			pushClassType(mv, store.getTypeName(checkForClassType(arrow.getResult()), true, true)); // Return type

			if (!md.isClassMeth()) {
				sb.append("Ljava/lang/Class;"); //Append the receiver type
				pushClassType(mv, store.getTypeName(store.getObjectType(), true, true)); // Push the receiver type
			}

			if (arrow.getArgument() instanceof Tuple) {
				Type[] types = ((Tuple)arrow.getArgument()).getTypes();
				Type[] nTypes = new Type[types.length];
				int nTypePtr = 0;
				for (Type t : types) {
					nTypes[nTypePtr++] = checkForClassType(t);
				}

				makeAndPopulateTypes(mv, sb, nTypes);
			} else if (!(arrow.getArgument() instanceof Unit)) {
				makeAndPopulateTypes(mv, sb, new Type[] { checkForClassType(arrow.getArgument()) });
			}
			sb.append(")Ljava/lang/invoke/MethodType;");//The return value

			mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType","methodType",sb.toString());

			mv.visitMethodInsn(INVOKEVIRTUAL,"java/lang/invoke/MethodHandle","asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;");
			mv.visitFieldInsn(
					PUTSTATIC,
					store.getRawTypeName(getCurrentType()),
					md.getName()+"$handle","Ljava/lang/invoke/MethodHandle;");
		}
		mv.visitLabel(exceptionEnd);
		mv.visitJumpInsn(GOTO, returnStatement);
		mv.visitLabel(handler);
		mv.visitVarInsn(ASTORE, 2);
		mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V");
		mv.visitInsn(ATHROW);
		mv.visitLabel(returnStatement);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0,0);
		mv.visitEnd();
	}

	private void makeAndPopulateTypes(MethodVisitor mv, StringBuilder sb, Type[] types) {
		int nth = 0;
		sb.append("[Ljava/lang/Class;");
		mv.visitLdcInsn(types.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE,2);
		for (Type type : types) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(nth);
			pushClassType(mv, store.getTypeName(type, true, true));
			mv.visitInsn(AASTORE);
			++nth;
		}
	}

	private void addDefaultConstructor() {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE,
				"<init>", 
				"()V", 
				null,
				null);
		mv.visitCode();
		mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		MethVisitor imv = new MethVisitor(this, mv, new ArrayList<Pair<String, Type>>());
		for (Map.Entry<String, TypedAST> entry : initalizeFieldValues.entrySet()) {
			mv.visitInsn(DUP);
			if (entry.getValue() == null)
				continue;
			((CoreAST)entry.getValue()).accept(imv);
			mv.visitFieldInsn(PUTFIELD, getCurrentTypeName(), entry.getKey(), store.getTypeName(entry.getValue().getType(), true, true));
		}
		mv.visitInsn(org.objectweb.asm.Opcodes.RETURN);
		mv.visitMaxs(0, 0);//Unused
		mv.visitEnd();
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
		cw.visitField(ACC_PUBLIC,
				valDeclaration.getName(), 
				getTypeName(valDeclaration.getBinding().getType()),
				null, 
				new Integer(0)).visitEnd();
		initalizeFieldValues.put(valDeclaration.getName(), valDeclaration.getDefinition());
	}

	@Override
	public void visit(VarDeclaration valDeclaration) {
		super.visit(valDeclaration);
		cw.visitField(ACC_PUBLIC,
				valDeclaration.getName(), 
				getTypeName(valDeclaration.getBinding().getType()),
				null, 
				new Integer(0)).visitEnd();
		initalizeFieldValues.put(valDeclaration.getName(), valDeclaration.getDefinition());
	}
	
	@Override
	public void visit(MethDeclaration methDeclaration) {
		int access = ACC_PUBLIC;
		if (methDeclaration.isClassMeth())
			access += ACC_STATIC;


		cw.visitField(ACC_PUBLIC | ACC_STATIC,
				methDeclaration.getName() + "$handle",
				org.objectweb.asm.Type.getType(MethodHandle.class).getDescriptor(),
				null,
				null).visitEnd();

		meths.add(methDeclaration);

		MethodVisitor mv = cw.visitMethod(access, methDeclaration.getName(),
				getTypeName(methDeclaration.getType(), false), null, null);
		new MethVisitor(this, mv, context.getExternalDecls()).visitInitial(methDeclaration);
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
