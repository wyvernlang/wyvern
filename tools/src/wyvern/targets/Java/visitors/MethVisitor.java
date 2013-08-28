package wyvern.targets.Java.visitors;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import wyvern.targets.util.VariableResolver;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.*;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

import static org.objectweb.asm.Opcodes.*;

//The names are getting pretty bad...
public class MethVisitor extends BaseASTVisitor {

	private String name;

	private class WrappedMethDeclaration extends DefDeclaration {

		private String realName;
		private Environment env;
		private HashSet<String> extMethods;

		public WrappedMethDeclaration(String name, Type type, List<NameBinding> args, TypedAST body, boolean isClassMeth, FileLocation location, String realName, Environment env, HashSet<String> extMethods) {
			super(name, type, args, body, isClassMeth, location);
			this.realName = realName;
			this.env = env;
			this.extMethods = extMethods;
		}


		public Environment getEnv() {
			return env;
		}

		private String getRealName() {
			return realName;
		}

		public HashSet<String> getExtMethods() {
			return extMethods;
		}
	}

	private ClassVisitor jv;
	private MethodVisitor mv;
	private Frame frame;
	private Handle bootstrap;
	private Handle bootstrapMeth;
	private Handle bootstrapField;
	private Handle bootstrapVarSet;
	
	private Environment localEnv = Environment.getEmptyEnvironment();
	
	private Boolean isStatic = false;
	private Boolean isReceiver = false;
	private Type receiverType = null;

	private MethodExternalContext mec = new MethodExternalContext();
	private boolean isAssignment;
	private HashSet<String> scopeMethods = new HashSet<>();


	private Type convertClassType(Type in) {
		if (in instanceof ClassType)
			return jv.getStore().getObjectType();
		return in;
	}
	private class MethodExternalContext {
		private HashMap<String, Pair<Type, Integer>> varMap = new HashMap<String, Pair<Type, Integer>>();
		
		public static final int ARGUMENT = 0;
		public static final int CLASS_METH = 1;
		
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
			if (retType instanceof Int || retType instanceof Bool)
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
			return new Arrow(new Tuple(types.toArray(new Type[0])), input.getResult());
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
				if (!type.subtype(t))
					throw new RuntimeException();
			}
		} else if (arrowArgument instanceof Unit) {
			//Nothing
		} else {
			Type fromStack = frame.popStackType();
			if (! arrowArgument.subtype(fromStack))
				throw new RuntimeException();
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
	private void initalizeToDefault(int idx, Type type) {
		if (type instanceof Int || type instanceof Bool) {
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, idx);
	    } else {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, idx);
		}
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

        @Override
        public LineParser getParser() {
            return null;
        }

        @Override
		public boolean isSimple() {
			// TODO Auto-generated method stub
			return true;
		}
	}
	private class Frame {
		private final Frame parent;

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
		private int parentPull = 0;
		private int tvn = 0; //Number of temporary vars TODO: fix by generating stack frames

		private LinkedList<Type> stackTypes = new LinkedList<Type>();


		public Frame() {
			this.parent = null;
		}
		public Frame(Frame parent) {
			this.parent = parent;
			this.maxVar = parent.maxVar;
			this.maxStack = parent.maxStack;
		}
		public boolean containsVariable(String varName) {
			return variableMap.containsKey(varName) ||
					(parent != null && parent.variableMap.containsKey(varName));
		}

		private VarInfo getVarInfo(String varName) {
			if (variableMap.containsKey(varName))
				return variableMap.get(varName);
			else if (parent != null && parent.variableMap.containsKey(varName))
				return parent.variableMap.get(varName);
			else
				throw new RuntimeException();
		}

		private void setMaxStack(int maxStack) {
			this.maxStack = maxStack;
			if (parent != null)
				parent.setMaxStack(maxStack);
		}

		private int incrMaxVar() {
			int ret = this.maxVar++;
			if (parent != null)
				parent.incrMaxVar();
			return ret;
		}
		
		public int getVariableIndex(String varName) {
			return getVarInfo(varName).getIdx();
		}

		public Type getVariableType(String varName) {
			return getVarInfo(varName).getType();
		}
		
		public void registerVariable(String varName, Type varType, Label definitionLocation) {
			variableMap.put(varName, new VarInfo(varName, incrMaxVar(), varType, definitionLocation));
		}
		
		public void pushStackType(Type t) {
			stackTypes.push(t);
			if (stackTypes.size() > maxStack) {
				setMaxStack(stackTypes.size());
			}
		}
		
		public boolean stackEmpty() {
			if (parent == null)
				return stackTypes.isEmpty();
			else
				return stackTypes.isEmpty() && parent.stackEmpty();
		}
		
		public Type popStackType() {
			if (!stackTypes.isEmpty())
				return stackTypes.pop();
			else {
				parentPull++;
				return parent.popStackType();
			}
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

		public int getTempVarIdx(Type varType, Label definitionLocation) {
			registerVariable("temp$$"+tvn, varType, definitionLocation);
			return getVariableIndex("temp$$"+tvn);
		}

		public void endFrame(MethodVisitor mv) {
			Label endLabel = new Label();
			mv.visitLabel(endLabel);
			for (VarInfo vi : variableMap.values()) {
				mv.visitLocalVariable(vi.getName(), jv.getStore().getTypeName(vi.getType(), true, true), null, vi.getDefLoc(), endLabel, vi.getIdx());
			}
			
		}
		
		public Map<String, Type> getVars() {
			HashMap<String, Type> map = new HashMap<String, Type>();
			for (Entry<String, VarInfo> entry : variableMap.entrySet()){
				map.put(entry.getKey(), entry.getValue().getType());
			}
			if (parent != null)
				map.putAll(parent.getVars());
			return map;
		}

		public Frame getParent() {
			return parent;
		}
	}

	private void storeAtIdx(Type type, int idx) {
		if (type instanceof Int || type instanceof Bool)
			mv.visitVarInsn(ISTORE, idx);
		else
			mv.visitVarInsn(ASTORE, idx);
	}

	private void storeTopOfStack(int varIdx) {
		Type type = frame.popStackType();
		storeAtIdx(type, varIdx);
	}

	private void loadByType(Type type, int varIdx) {
		if (type instanceof Int || type instanceof Bool)
			mv.visitVarInsn(ILOAD, varIdx);
		else
			mv.visitVarInsn(ALOAD, varIdx);
	}

	public MethVisitor(ClassVisitor jv, MethodVisitor mv, Iterable<Pair<String, Type>> argVars) {
		this.jv = jv;
		this.mv = mv;
		mec.addExternal(argVars, MethodExternalContext.ARGUMENT);

		frame = new Frame();
		MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, Object[].class);
		bootstrap = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrap", mt.toMethodDescriptorString());
		bootstrapMeth = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrapMethHandle", mt.toMethodDescriptorString());
		bootstrapField = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrapField", mt.toMethodDescriptorString());
		bootstrapVarSet = new Handle(Opcodes.H_INVOKESTATIC, "wyvern/targets/Java/runtime/Runtime", "bootstrapSetField", mt.toMethodDescriptorString());
	}

	public void visitInitial(DefDeclaration md) {
		if (md instanceof WrappedMethDeclaration) {
			localEnv = ((WrappedMethDeclaration) md).getEnv();
			name = ((WrappedMethDeclaration) md).getRealName();
			scopeMethods = ((WrappedMethDeclaration) md).getExtMethods();
		}

		mv.visitCode();

		Label startLabel = new Label();
		mv.visitLabel(startLabel);

		if (!md.isClass())
			frame.registerVariable("this", jv.getCurrentType(), startLabel);

		for (NameBinding nb : md.getArgBindings()) {
			frame.registerVariable(nb.getName(), nb.getType(), startLabel);
		}

		super.visit(md);
		Type returnType = null;
		if (!frame.stackEmpty())
			returnType = frame.popStackType();

		generateReturn(returnType, mv);

		frame.endFrame(mv);

		mv.visitMaxs(10, 10); //Ignored
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
	public void visit(DefDeclaration md) {
		HashMap<String, Type> externalVars = new HashMap<>();
		externalVars.putAll(frame.getVars());
		//externalVars.put(md.getName(), md.getType());
		VariableResolver visitor = new VariableResolver(externalVars);
		md.accept(visitor);
		List<Pair<String, Type>> usedVars = visitor.getUsedVars();

		boolean selfRef = false;
		for (Pair<String, Type> var : usedVars)
			if (var.first.equals(md.getName())) {
				selfRef = true;
				break;
			}

		List<NameBinding> newArgs = new ArrayList<NameBinding>(md.getArgBindings().size() + usedVars.size());
		for (Pair<String, Type> pair : usedVars) {
			newArgs.add(new NameBindingImpl(pair.first, pair.second));
		}
		for (NameBinding nb : md.getArgBindings()) {
			newArgs.add(nb);
		}

		Label defLoc = new Label();
		HashSet<String> scopeMethodsClone = new HashSet<>(scopeMethods);
		scopeMethodsClone.add(md.getName());
		DefDeclaration implMd =
				new WrappedMethDeclaration(md.getName()+"$impl",
						DefDeclaration.getMethodType(newArgs, ((Arrow)md.getType()).getResult()),
						newArgs,
						md.getBody(), true, md.getLocation(), md.getName(), this.localEnv, scopeMethodsClone);
		scopeMethods.add(md.getName());
		jv.visit(implMd);

		frame.registerVariable(md.getName(), md.getType(), defLoc);
		mv.visitLabel(defLoc);
		mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), md.getName()+"$impl$handle", "Ljava/lang/invoke/MethodHandle;");
		fillClosure(usedVars);
		mv.visitVarInsn(ASTORE, frame.getVariableIndex(md.getName()));
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
					mv.visitVarInsn(ILOAD, frame.getVariableIndex(p.first));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				} else if (frame.getVariableType(p.first) instanceof Bool) {
					mv.visitVarInsn(ILOAD, frame.getVariableIndex(p.first));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
				} else {
					mv.visitVarInsn(ALOAD, frame.getVariableIndex(p.first));
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
		Type iRC = receiverType;
		receiverType = null;
		boolean fnIsStatic = isStatic;
		//isReceiver = false;
		((CoreAST)app.getArgument()).accept(this); //Assumed to have pushed every argument

		if (app.getFunction() instanceof Invocation) {
			iRC = ((Invocation)app.getFunction()).getReceiver().getType();
		}

		Arrow fnType;
		if (iRC != null && !fnIsStatic)
			fnType = addClassToArgs((Arrow) app.getFunction().getType(), iRC);
		else {
			fnType = (Arrow) app.getFunction().getType();
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", getMethodDescription(fnType));
		doFnInv(fnType);
		Type st = frame.popStackType();
		assert st.equals(app.getFunction().getType());
		putFnRes(fnType);

		Type resultType = fnType.getResult();

		if (resultType instanceof ClassType || resultType instanceof TypeType)
			isStatic = false;
		else
			isStatic = true;
	}

	@Override
	public void visit(ValDeclaration vd) {
		super.visit(vd);
		Label defLoc = new Label();
		mv.visitLabel(defLoc);
		frame.registerVariable(vd.getName(), vd.getType(), defLoc);
		Type type = frame.popStackType();
		
		storeAtIdx(type, frame.getVariableIndex(vd.getName()));
		
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

		storeAtIdx(type, frame.getVariableIndex(vd.getName()));

		Type outputType = vd.getType();
		if (outputType instanceof ClassType)
			outputType = new ObjectType((ClassType) outputType);

		localEnv = localEnv.extend(new TypeBinding(vd.getName(), outputType));
	}
	
	@Override
	public void visit(Invocation inv) {

		TypedAST argument = inv.getArgument();
		TypedAST receiver = inv.getReceiver();

		Label ssLabel = new Label();

		if (receiver instanceof CoreAST)
			((CoreAST) receiver).accept(this);

		switch (inv.getOperationName()) {
			case "||":
				mv.visitJumpInsn(IFNE, ssLabel);
				break;
			case "&&":
				mv.visitJumpInsn(IFEQ, ssLabel);
				break;
			default:
				break;
		}

		if (argument instanceof CoreAST)
			((CoreAST) argument).accept(this);
		if (inv.getOperationName().equals("||") || inv.getOperationName().equals("&&")) {
			Label resLabel = new Label();
			mv.visitJumpInsn(GOTO, resLabel);
			mv.visitLabel(ssLabel);
			switch (inv.getOperationName()) {
				case "||":
					mv.visitInsn(ICONST_1);
					break;
				case "&&":
					mv.visitInsn(ICONST_0);
					break;
				default:
					break;
			}
			mv.visitLabel(resLabel);
		}

		if (!isStatic) {
			if (!(receiver.getType() instanceof ClassType) && !(receiver.getType() instanceof TypeType)) {
				isStatic = true;
			}
		}
		
		if (argument != null) {
			Pair<Type, Type> pair = frame.popStackTypePair();
			
			if (inv.getOperationName().equals("+") && 
					pair.first instanceof Int && 
					pair.second instanceof Int) {
				mv.visitInsn(IADD);
				frame.pushStackType(Int.getInstance());
			} else if (inv.getOperationName().equals("-") &&
					pair.first instanceof Int &&
					pair.second instanceof Int) {
				mv.visitInsn(ISUB);
				frame.pushStackType(Int.getInstance());
			} else if (inv.getOperationName().equals("*") &&
						pair.first instanceof Int &&
						pair.second instanceof Int) {
					mv.visitInsn(IMUL);
					frame.pushStackType(Int.getInstance());
			} else if (inv.getOperationName().equals("/") &&
					pair.first instanceof Int &&
					pair.second instanceof Int) {
				mv.visitInsn(IDIV);
				frame.pushStackType(Int.getInstance());
			} else if (inv.getOperationName().equals(">") &&
					pair.first instanceof Int &&
					pair.second instanceof Int) {
				Label truel = new Label();
				Label end = new Label();
				mv.visitJumpInsn(IF_ICMPGT, truel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(truel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(end);
				frame.pushStackType(Bool.getInstance());
			} else if (inv.getOperationName().equals("==") &&
					((pair.first instanceof Int &&
					pair.second instanceof Int) ||
					(pair.first instanceof Bool &&
					pair.second instanceof Bool))) {
				Label truel = new Label();
				Label end = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, truel);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(truel);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(end);
				frame.pushStackType(Bool.getInstance());
			} else if ((inv.getOperationName().equals("&&") || inv.getOperationName().equals("||")) &&
					(pair.first instanceof Bool &&
					pair.second instanceof Bool)) {
				//Already done above
				frame.pushStackType(Bool.getInstance());
			} else if (inv.getOperationName().equals("+") &&
					pair.first instanceof Str &&
					pair.second instanceof Str) {
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
				frame.pushStackType(Str.getInstance());
			} else {
				throw new RuntimeException("Not implemented");
			}
		} else { //variable or function as first class value
			if (inv.getType() instanceof Arrow) {
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
					mv.visitInvokeDynamicInsn(inv.getOperationName(),"(Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;", bootstrapMeth);
					frame.swap();
					mv.visitInsn(SWAP);
				}
				return;
			}
			//Variable, then
			//Invokedynamic to reference
			//Assigning in here is going to be fun
			if (!isAssignment) {
				if (frame.popStackType() != receiver.getType())
					throw new RuntimeException("Invariant broken");
				String invTypeName = jv.getStore().getTypeName(convertClassType(inv.getType()), true, true);
				pushClassType(mv, invTypeName);
				mv.visitInvokeDynamicInsn(inv.getOperationName(),
						"("+
								jv.getStore().getTypeName(convertClassType(receiver.getType()),true,true)+
						"Ljava/lang/Class;)"+ invTypeName,
						bootstrapField);
				frame.pushStackType(inv.getType());
			} else {
				//We need the receiver, the new value, and the type on the stack in that order
				//The new value and the receiver are already on the stack, but in the order of value, receiver
				frame.swap();
				mv.visitInsn(SWAP);
				//Now in receiver, value form
				pushClassType(mv, jv.getStore().getTypeName(convertClassType(inv.getType()), true, true)); // class type of field
				//Do the invocation
				mv.visitInvokeDynamicInsn(inv.getOperationName(),
						"("+
								jv.getStore().getTypeName(convertClassType(receiver.getType()),true,true)+
								"Ljava/lang/Object;Ljava/lang/Class;)V",
						bootstrapVarSet);
				frame.popStackTypePair(); //Pull the receiver and value off the stack
			}
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
		for (Entry<String, TypedAST> arg : ndw.getArgs().entrySet()) {
			mv.visitInsn(DUP);
			((CoreAST)arg.getValue()).accept(this);
			mv.visitFieldInsn(PUTFIELD, jv.getCurrentTypeName(), arg.getKey(),jv.getStore().getTypeName(arg.getValue().getType(), true, true));
			Type frameType = frame.popStackType();

			if (!frameType.subtype(arg.getValue().getType()))
				throw new RuntimeException("Invariant broken");
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

		isStatic = false;
		Type type = var.getType();

		writeVariable(name, type);
	}

	private void writeVariable(String name, Type type) {
		if (!frame.containsVariable(name)) {
			if (mec.isVarExternal(name)) {
				Pair<Type, Integer> varInfo = mec.getExternalVar(name);
				if (varInfo.second == MethodExternalContext.ARGUMENT && frame.containsVariable("this")) {
					mv.visitVarInsn(ALOAD, frame.getVariableIndex("this"));
					mv.visitFieldInsn(GETFIELD, jv.getCurrentTypeName(), name+"$dyn", jv.getStore().getTypeName(type, true));
					frame.pushStackType(type);
					return;
				} else {
					throw new RuntimeException();
				}
			}
			if (scopeMethods.contains(name)) { //Self-ref
				mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), name+"$impl$handle", "Ljava/lang/invoke/MethodHandle;");
				frame.pushStackType(type);
				return;
			}
			throw new RuntimeException();
		}
		int varIdx= frame.getVariableIndex(name);
		loadByType(type, varIdx);
		frame.pushStackType(type);
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
		if (ass.getTarget() instanceof Variable) {
			int variableIndex = frame.getVariableIndex(((Variable) ass.getTarget()).getName());
			if (frame.popStackType() != frame.getVariableType(((Variable) ass.getTarget()).getName()))
				throw new RuntimeException();
			storeAtIdx(ass.getValue().getType(), variableIndex);

		} else if (ass.getTarget() instanceof Invocation) {
			Type assType = ass.getValue().getType();
			box(assType);
			isAssignment = true;
			((Invocation)ass.getTarget()).accept(this);
			isAssignment = false;
		}
	}

	private void box(Type assType) {
		if (assType instanceof Int)
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
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
		DefDeclaration anonMeth = new DefDeclaration(anonFnName,
				DefDeclaration.getMethodType(newArgs, ((Arrow)fnDef.getType()).getResult()),
				newArgs, fnDef.getBody(), true, fnDef.getLocation());
		jv.visit(anonMeth);
		mv.visitFieldInsn(GETSTATIC, jv.getCurrentTypeName(), anonFnName + "$handle", "Ljava/lang/invoke/MethodHandle;");
		fillClosure(usedVars);
		frame.pushStackType(fnDef.getType());
	}

	@Override
	public void visit(StringConstant sc) {
		mv.visitLdcInsn(sc.getValue());
		frame.pushStackType(Str.getInstance());
	}

	@Override
	public void visit(BooleanConstant bc) {
		if (bc.getValue())
			mv.visitInsn(ICONST_1);
		else
			mv.visitInsn(ICONST_0);
		frame.pushStackType(Bool.getInstance());
	}

	@Override
	public void visit(IfExpr ifStmt) {
		Label endOfStmt = new Label();

		Label tdl = new Label();
		mv.visitLabel(tdl);
		int tempIdx = frame.getTempVarIdx(ifStmt.getType(), tdl);
		initalizeToDefault(tempIdx, ifStmt.getType());
		for (IfExpr.IfClause clause : ifStmt.getClauses()) {
			frame = new Frame(frame);
			((CoreAST)clause.getClause()).accept(this); //Assumed to push a boolean value onto the stack
			if (!(frame.popStackType() instanceof Bool))
				throw new RuntimeException();
			Label end = new Label();
			mv.visitJumpInsn(IFEQ, end);
			((CoreAST)clause.getBody()).accept(this);
			storeTopOfStack(tempIdx);
			frame = frame.getParent();
			mv.visitJumpInsn(GOTO, endOfStmt); //Successful
			mv.visitLabel(end);
		}
		mv.visitLabel(endOfStmt);
		loadByType(ifStmt.getType(), tempIdx);
		frame.pushStackType(ifStmt.getType());
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		Label conditionalPos = new Label();
		Label end = new Label();
		mv.visitLabel(conditionalPos);
		((CoreAST)whileStatement.getConditional()).accept(this);
		if (!(frame.popStackType() instanceof Bool))
			throw new RuntimeException();
		mv.visitJumpInsn(IFEQ, end);
		((CoreAST)whileStatement.getBody()).accept(this);
		mv.visitJumpInsn(GOTO, conditionalPos);
		mv.visitLabel(end);
	}
}
