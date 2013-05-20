package wyvern.targets.Java.visitors;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

import static org.objectweb.asm.Opcodes.*;

//The names are getting pretty bad...
public class MethVisitor extends BaseASTVisitor {
	private ClassVisitor jv;
	private MethodVisitor mv;
	private Frame frame;
	private Handle bootstrap;
	private Handle bootstrapVar;
	private Handle bootstrapFun;
	
	private Environment localEnv = Environment.getEmptyEnvironment();
	
	private Boolean isStatic = false;
	private Boolean isReceiver = false;
	private Type receiverType = null;
	
	private MethodExternalContext mec = new MethodExternalContext();


	private Type convertClassType(Type in) {
		if (in instanceof ClassType)
			return jv.getStore().getObjectType();
		return in;
	}
	private class MethodExternalContext {
		private HashMap<String, Pair<Type, Integer>> varMap = new HashMap<String, Pair<Type, Integer>>();
		
		public static final int STATIC = 0;
		public static final int INTERFACE = 1;
		
		public void addExternal(Iterable<Pair<String, Type>> externalDecls, int type) {
			for (Pair<String, Type> pair : externalDecls) {
				varMap.put(pair.first, new Pair<Type, Integer>(pair.second, type));
			}
		}
		
		public boolean isVarExternal(String name) {
			return varMap.containsKey(name);
		}
		
		public Pair<Type, Integer> getExternalVar(String name) {
			return varMap.get(name);
		}
	}
	public static void generateReturn(Type retType, MethodVisitor mv) {
		if (retType == null)
			mv.visitInsn(RETURN);
		else {
			if (retType instanceof Int)
				mv.visitInsn(IRETURN);
			else
				mv.visitInsn(ARETURN);
		}
	}
	public Arrow addClassToArgs(Arrow input, Type newt) {
		Type argType = input.getArgument();
		if (argType instanceof Tuple) {
			ArrayList<Type> types = new ArrayList<Type>();
			types.add(newt);
			for (Type t : ((Tuple) argType).getTypes()) 
				types.add(t);
			return new Arrow(new Tuple((Type[])types.toArray()), input.getResult());
		}
		if (argType instanceof Unit)
			return new Arrow(newt, input.getResult());
		return new Arrow(new Tuple(new Type[] { newt, argType }), input.getResult());
	}
	private String getMethodDescription(Arrow arrow) {
		StringBuilder output = new StringBuilder("(");
		Type arrowArgument = arrow.getArgument();
		if (arrowArgument instanceof Tuple) {
			Tuple tuple = (Tuple) arrowArgument;
			Type[] types = tuple.getTypes();
			for (Type t : types) {
				output.append(jv.getStore().getTypeName((t instanceof ClassType)?jv.getStore().getObjectType() : t,true,true));
			}
		} else {
			if (!(arrowArgument instanceof Unit))
				output.append(jv.getStore().getTypeName((arrowArgument instanceof ClassType)?jv.getStore().getObjectType() : arrowArgument,true,true));
		}
		output.append(")");
		Type result = arrow.getResult();

		output.append(jv.getStore().getTypeName((result instanceof ClassType)?jv.getStore().getObjectType() : result, true,true));
		return output.toString();
	}
	private void doFnInv(Arrow arrow) {
		Type arrowArgument = arrow.getArgument();
		if (arrowArgument instanceof Tuple) {
			Tuple tuple = (Tuple) arrowArgument;
			Type[] types = tuple.getTypes();
			for (int i = types.length-1; i >= 0; i--) {
				Type t = types[i];
				Type type = frame.popStackType();
				assert type.equals(t);
			}
		} else if (arrowArgument instanceof Unit) {
			//Nothing
		} else {
			Type fromStack = frame.popStackType();
			assert arrowArgument.subtype(fromStack);
		}
	}
	private void putFnRes(Arrow arrow) {
		frame.pushStackType(arrow.getResult());
	}
	private void pushClassType(MethodVisitor mv, String descriptor) {
		if (descriptor.equals("V")) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
		} else if (descriptor.equals("I")) {
			mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
		} else
			mv.visitLdcInsn(org.objectweb.asm.Type.getType(descriptor));
	}
	private class ObjectType implements Type {
		private ClassType refType;

		public ObjectType(ClassType inT) {
			this.refType = inT;
		}
		
		public Type getInstanceType() {
			return refType;
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {
		}

		@Override
		public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
			return false;
		}

		@Override
		public boolean subtype(Type other) {
			return false;
		}
	}
	private class Frame {
		public class VarInfo {
			private String name;
			private int index;
			private Type type;
			private Label defLoc;

			public VarInfo(String varName, int index, Type varType, Label defLoc) {
				this.name = varName;
				this.index = index;
				this.type = varType;
				this.defLoc = defLoc;
			}
			
			public Label getDefLoc() { return defLoc; }
			public Type getType() { return type; }
			public int getIdx() { return index; }
			public String getName() { return name; }
		}
		private Hashtable<String, VarInfo> variableMap = new Hashtable<String, VarInfo>();
		private int maxVar = 0;
		private int maxStack = 0;
		
		private LinkedList<Type> stackTypes = new LinkedList<Type>();
		
		public boolean containsVariable(String varName) {
			return variableMap.containsKey(varName);
		}
		
		public int getVariableIndex(String varName) {
			if (!variableMap.containsKey(varName))
				throw new RuntimeException();
			return variableMap.get(varName).getIdx();
		}

		public Type getVariableType(String varName) {
			if (!variableMap.containsKey(varName))
				throw new RuntimeException();
			return variableMap.get(varName).getType();
		}
		
		public void registerVariable(String varName, Type varType, Label definitionLocation) {
			variableMap.put(varName, new VarInfo(varName, maxVar++, varType, definitionLocation));
		}
		
		public void pushStackType(Type t) {
			stackTypes.push(t);
			if (stackTypes.size() > maxStack)
				maxStack = stackTypes.size();
		}
		
		public boolean stackEmpty() {
			return stackTypes.isEmpty();
		}
		
		public Type popStackType() {
			return stackTypes.pop();
		}

		public void swap() {
			Type a = stackTypes.pop();
			Type b = stackTypes.pop();
			stackTypes.push(a);
			stackTypes.push(b);
		}
		
		public Pair<Type, Type> popStackTypePair() {
			return new Pair<Type, Type>(stackTypes.pop(), stackTypes.pop());
		}

		public void endFrame(MethodVisitor mv) {
			Label endLabel = new Label();
			mv.visitLabel(endLabel);
			for (VarInfo vi : variableMap.values()) {
				mv.visitLocalVariable(vi.getName(), jv.getStore().getTypeName(vi.getType(), true, true), null, vi.getDefLoc(), endLabel, vi.getIdx());
			}
			
		}
		
		public Map<String, Type> getVars() {
			Map<String, Type> map = new HashMap<String, Type>();
			for (Entry<String, VarInfo> entry : variableMap.entrySet()){
				map.put(entry.getKey(), entry.getValue().getType());
			}
			return map;
		}
	}

	public MethVisitor(ClassVisitor jv, MethodVisitor mv, Iterable<Pair<String, Type>> externals) {
		this.jv = jv;
		this.mv = mv;
		mec.addExternal(externals, MethodExternalContext.STATIC);

		frame = new Frame();
		MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Object[].class);
		bootstrap = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrap", mt.toMethodDescriptorString());
		bootstrapVar = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrapMethHandle", mt.toMethodDescriptorString());
		bootstrapFun = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrapField", mt.toMethodDescriptorString());
	}

	public void visitInitial(MethDeclaration md) {

		Label startLabel = new Label();
		mv.visitLabel(startLabel);

		if (!md.isClassMeth())
			frame.registerVariable("this", jv.getCurrentType(), startLabel);

		for (NameBinding nb : md.getArgBindings()) {
			frame.registerVariable(nb.getName(), nb.getType(), startLabel);
		}

		mv.visitCode();
		super.visit(md);
		Type returnType = null;
		if (!frame.stackEmpty())
			returnType = frame.popStackType();

		generateReturn(returnType, mv);

		frame.endFrame(mv);

		mv.visitMaxs(0, 0); //Ignored
		mv.visitEnd();
		localEnv = localEnv.extend(new TypeBinding(md.getName(), md.getType()));
	}
	
	@Override
	public void visit(ClassDeclaration cd) {
		VariableResolver vr = new VariableResolver(frame.getVars()); //TODO: nest further.
		cd.accept(vr);
		List<Pair<String, Type>> usedVars = vr.getUsedVars();
		
		ClassVisitor classVisitor = new ClassVisitor("", jv, usedVars);
		classVisitor.visit(cd);
		
		for (Pair<String, Type> pair : usedVars) {
			writeVariable(pair.first, pair.second);
			mv.visitFieldInsn(PUTSTATIC, classVisitor.getCurrentTypeName(), pair.first+"$stat", jv.getStore().getTypeName(pair.second, true));
		}
		
		localEnv = localEnv.extend(new TypeBinding(cd.getName(), cd.getType()));
	}

    @Override
    public void visit(TypeDeclaration td) {
        TypeVisitor typev = new TypeVisitor(jv.getStore());
        typev.visit(td);
        localEnv = localEnv.extend(new TypeBinding(td.getName(), td.getType()));
    }
	
	@Override
	public void visit(MethDeclaration md) {
		VariableResolver visitor = new VariableResolver(frame.getVars());
		md.accept(visitor);
		List<Pair<String, Type>> usedVars = visitor.getUsedVars();

		List<NameBinding> newArgs = new ArrayList<NameBinding>(md.getArgBindings().size() + usedVars.size());
		for (Pair<String, Type> pair : usedVars) {
			newArgs.add(new NameBindingImpl(pair.first, pair.second));
		}
		for (NameBinding nb : md.getArgBindings()) {
			newArgs.add(nb);
		}

		Label defLoc = new Label();
		MethDeclaration implMd =
				new MethDeclaration(md.getName()+"$impl", newArgs, ((Arrow)md.getType()).getResult(), md.getBody(), true, md.getLocation());
		jv.visit(implMd);

		frame.registerVariable(md.getName(), md.getType(), defLoc);
		mv.visitLabel(defLoc);
		mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), md.getName()+"$impl$handle", "Ljava/lang/invoke/MethodHandle;");
		fillClosure(usedVars);
		mv.visitIntInsn(ASTORE, frame.getVariableIndex(md.getName()));
	}

	private void fillClosure(List<Pair<String, Type>> usedVars) {
		if (usedVars.size() > 0) {
			mv.visitInsn(ICONST_0);
			mv.visitLdcInsn(usedVars.size());
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			//mv.visitInsn(DUP);
			int idx = 0;
			for (Pair<String, Type> p : usedVars) {
				mv.visitInsn(DUP);
				mv.visitLdcInsn(idx++);

				if (frame.getVariableType(p.first) instanceof Int) {
					mv.visitIntInsn(ILOAD, frame.getVariableIndex(p.first));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				} else {
					mv.visitIntInsn(ALOAD, frame.getVariableIndex(p.first));
				}
				mv.visitInsn(AASTORE);
			}
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "insertArguments",
					"(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;");
		}
	}

	@Override
	public void visit(Application app) {
		//isReceiver = true;
		((CoreAST)app.getFunction()).accept(this); //Assumed to have pushed a MethodHandle
		//isReceiver = false;
		((CoreAST)app.getArgument()).accept(this); //Assumed to have pushed every argument

		if (app.getFunction() instanceof Invocation) {
			receiverType = ((Invocation)app.getFunction()).getReceiver().getType();
		}
		Arrow fnType;
		if (receiverType != null && !isStatic)
			fnType = addClassToArgs((Arrow) app.getFunction().getType(), receiverType);
		else {
			fnType = (Arrow) app.getFunction().getType();
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", getMethodDescription(fnType));
		doFnInv(fnType);
		assert frame.popStackType().equals(app.getFunction().getType());
		putFnRes(fnType);
		isStatic = false;
		/*
		if (app.getFunction() instanceof Invocation && ((Invocation) app.getFunction()).getArgument() == null) {
			Invocation funInv = (Invocation) app.getFunction();
			//argument == null
			//we now know that this is a method invocation directly on a class.
			((CoreAST)funInv.getReceiver()).accept(this);
			((CoreAST)app.getArgument()).accept(this);
			Arrow arrow = (Arrow)app.getFunction().getType();
			
			if (arrow.getArgument() instanceof Tuple) {
				for (int i = 0; i < ((Tuple)arrow.getArgument()).getTypes().length; i++) 
					frame.popStackType();
			} else if (!(arrow.getArgument() instanceof Unit))
				frame.popStackType();
			
			
			if (isStatic) { //Therefore, receiver is a Variable that set the isStatic flag
				mv.visitMethodInsn(INVOKESTATIC, jv.getStore().getUnmangledClassName((ClassType) staticType), funInv.getOperationName(), jv.getStore().getTypeName(app.getFunction().getType(), true));
				isStatic = false;
			} else {
                frame.popStackType();
				mv.visitInvokeDynamicInsn(
						funInv.getOperationName(), 
						jv.getStore().getTypeName(
								addClassToArgs((Arrow)app.getFunction().getType(), 
										funInv.getReceiver().getType()), 
										true), 
						bootstrap); 
			}

			frame.pushStackType(app.getType());
			return;
		}
		*/
		//throw new RuntimeException("Not implemented");
	}

	@Override
	public void visit(ValDeclaration vd) {
		super.visit(vd);
		Label defLoc = new Label();
		mv.visitLabel(defLoc);
		frame.registerVariable(vd.getName(), vd.getType(), defLoc);
		Type type = frame.popStackType();
		
		
		if (type instanceof Int)
			mv.visitIntInsn(ISTORE, frame.getVariableIndex(vd.getName()));
		else
			mv.visitIntInsn(ASTORE, frame.getVariableIndex(vd.getName()));
		
		Type outputType = vd.getType();
		if (outputType instanceof ClassType)
			outputType = new ObjectType((ClassType) outputType);

		localEnv = localEnv.extend(new TypeBinding(vd.getName(), outputType));
	}

	@Override
	public void visit(VarDeclaration vd) {
		super.visit(vd);
		Label defLoc = new Label();
		mv.visitLabel(defLoc);
		frame.registerVariable(vd.getName(), vd.getType(), defLoc);
		Type type = frame.popStackType();


		if (type instanceof Int)
			mv.visitIntInsn(ISTORE, frame.getVariableIndex(vd.getName()));
		else if (type instanceof ClassType)
			mv.visitIntInsn(ASTORE, frame.getVariableIndex(vd.getName()));
		else
			throw new RuntimeException("Not implemented");

		Type outputType = vd.getType();
		if (outputType instanceof ClassType)
			outputType = new ObjectType((ClassType) outputType);

		localEnv = localEnv.extend(new TypeBinding(vd.getName(), outputType));
	}
	
	@Override
	public void visit(Invocation inv) {

		TypedAST argument = inv.getArgument();
		TypedAST receiver = inv.getReceiver();
		if (receiver instanceof CoreAST)
			((CoreAST) receiver).accept(this);
		if (argument instanceof CoreAST)
			((CoreAST) argument).accept(this);
		
		if (argument != null) {
			Pair<Type, Type> pair = frame.popStackTypePair();
			
			if (inv.getOperationName().equals("+") && 
					pair.first instanceof Int && 
					pair.second instanceof Int) {
				mv.visitInsn(IADD);
				frame.pushStackType(Int.getInstance());
			} else {
				throw new RuntimeException("Not implemented");
			}
		} else { //variable or function as first class value
			if (inv.getType() instanceof Arrow) {
				//TODO: Make this work
				if (isReceiver)
					receiverType = receiver.getType();
				//Method lookup, look in class for name$handle:MethodHande
				frame.pushStackType(inv.getType());
				if (isStatic) {
					mv.visitFieldInsn(GETSTATIC, jv.getStore().getRawTypeName(inv.getReceiver().getType()), inv.getOperationName() + "$handle", "Ljava/lang/invoke/MethodHandle;");
				} else {
					Type old = frame.popStackType();
					mv.visitInsn(DUP);
					frame.pushStackType(old);
					mv.visitInvokeDynamicInsn(inv.getOperationName(),"(Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;", bootstrapVar);
					frame.swap();
					mv.visitInsn(SWAP);
				}
				return;
			}
			//Variable, then
			//Invokedynamic to reference
			//Assigning in here is going to be fun
			assert frame.popStackType() == receiver.getType();
			String invTypeName = jv.getStore().getTypeName(convertClassType(inv.getType()), true, true);
			pushClassType(mv, invTypeName);
			mv.visitInvokeDynamicInsn(inv.getOperationName(),
					"("+
							jv.getStore().getTypeName(convertClassType(receiver.getType()),true,true)+
					"Ljava/lang/Class;)"+ invTypeName,
					bootstrapFun);
			frame.pushStackType(inv.getType());
			//throw new RuntimeException("Not implemented");
		}
	}
	
	@Override
	public void visit(New ndw) {
		//super.visit(ndw);
		mv.visitTypeInsn(NEW, jv.getCurrentTypeName());
		frame.pushStackType(jv.getCurrentType());
		mv.visitInsn(DUP);
		frame.pushStackType(jv.getCurrentType());
		frame.popStackType();
		mv.visitMethodInsn(INVOKESPECIAL, jv.getCurrentTypeName(), "<init>", "()V");

        for (Pair<String, Type> pair : jv.getExternalVars()) {
            mv.visitInsn(DUP);
            frame.pushStackType(jv.getCurrentType());
            mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), pair.first + "$stat", jv.getStore().getTypeName(pair.second, false));
            frame.pushStackType(pair.second);
            mv.visitFieldInsn(PUTFIELD, jv.getCurrentTypeName(), pair.first + "$dyn", jv.getStore().getTypeName(pair.second, false));
            frame.popStackTypePair();
        }
	}

	@Override
	public void visit(Variable var) {
		String name = var.getName();
		TypeBinding assocType = localEnv.lookupType(name);
		if (assocType != null && assocType.getType() instanceof ClassType) {
			isStatic = true;
			return;
		}
		Type type = var.getType();

		writeVariable(name, type);
	}

	private void writeVariable(String name, Type type) {
		if (!frame.containsVariable(name)) {
			if (mec.isVarExternal(name)) {
				Pair<Type, Integer> varInfo = mec.getExternalVar(name);
				if (varInfo.second == MethodExternalContext.STATIC && frame.containsVariable("this")) {
					mv.visitIntInsn(ALOAD, frame.getVariableIndex("this"));
					mv.visitFieldInsn(GETFIELD, jv.getCurrentTypeName(), name+"$dyn", jv.getStore().getTypeName(type, true));
					frame.pushStackType(type);
					return;
				} else {
					throw new RuntimeException();
				}
			}
			throw new RuntimeException();
		}
		int varIdx= frame.getVariableIndex(name);
		if (type instanceof Int) {
			mv.visitIntInsn(ILOAD, varIdx);
			frame.pushStackType(Int.getInstance());
		} else if (type instanceof ClassType
                || type instanceof TypeType
				|| type instanceof Arrow) {
			mv.visitIntInsn(ALOAD, varIdx);
			frame.pushStackType(type);
		}	else
			throw new RuntimeException("Not implemented");
	}
	
	@Override
	public void visit(IntegerConstant i) {
		switch (i.getValue())  {
		case 0:
			mv.visitInsn(ICONST_0);
			break;
		case 1:
			mv.visitInsn(ICONST_1);
			break;
		case 2:
			mv.visitInsn(ICONST_2);
			break;
		case 3:
			mv.visitInsn(ICONST_3);
			break;
		case 4:
			mv.visitInsn(ICONST_4);
			break;
		case 5:
			mv.visitInsn(ICONST_5);
			break;
		default:
			mv.visitIntInsn(BIPUSH, i.getValue());
		}
		frame.pushStackType(Int.getInstance());
	}

	@Override
	public void visit(Assignment ass) {
		((CoreAST)ass.getValue()).accept(this); //Assumes that the value is now on the top of the stack

		int variableIndex = frame.getVariableIndex(((Variable) ass.getTarget()).getName());

		if (ass.getValue().getType() instanceof Int)
			mv.visitIntInsn(ISTORE, variableIndex);
		else
			mv.visitIntInsn(ASTORE, variableIndex);
	}

	@Override
	public void visit(Fn fnDef) {
		VariableResolver visitor = new VariableResolver(frame.getVars());
		fnDef.accept(visitor);
		List<Pair<String, Type>> usedVars = visitor.getUsedVars();

		List<NameBinding> newArgs = new ArrayList<NameBinding>(fnDef.getArgBindings().size() + usedVars.size());
		for (Pair<String, Type> pair : usedVars) {
			newArgs.add(new NameBindingImpl(pair.first, pair.second));
		}
		for (NameBinding nb : fnDef.getArgBindings()) {
			newArgs.add(nb);
		}

		String anonFnName = jv.getAnonMethodName();
		MethDeclaration anonMeth = new MethDeclaration(anonFnName, newArgs, ((Arrow)fnDef.getType()).getResult(), fnDef.getBody(), true, fnDef.getLocation());
		jv.visit(anonMeth);
		mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), anonFnName+"$handle", "Ljava/lang/invoke/MethodHandle;");
		fillClosure(usedVars);
		frame.pushStackType(fnDef.getType());
	}
	//TODO: Invocation (urgh), Application (even more urgh), assignment, local variable definitions (we don't even do data flow analysis!), and so on
}
