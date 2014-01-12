package wyvern.tools.typedAST.core.expressions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class New extends CachingTypedAST implements CoreAST {
	ClassDeclaration cls;
	Map<String, TypedAST> args = new HashMap<String, TypedAST>();
	boolean isGeneric = false;

	private static final ClassDeclaration EMPTY = new ClassDeclaration("Empty", "", "", null, FileLocation.UNKNOWN);
	private static int generic_num = 0;

	public static void resetGenNum() {
		generic_num = 0;
	}

	public New(Map<String, TypedAST> args, FileLocation fileLocation) {
		this.args = args;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(cls);
		// FIXME: Not sure if this is rigth (Alex).
		for (TypedAST a : this.args.values()) {
			writer.writeArgs(a);
		}
	}

	@Override
	protected Type doTypecheck(Environment env) {
		// TODO check arg types
		// Type argTypes = args.typecheck();
		
		ClassBinding classVarTypeBinding = (ClassBinding) env.lookupBinding("class", ClassBinding.class);

		if (classVarTypeBinding != null) { //In a class method
			Type classVarType = classVarTypeBinding.getType();
			if (!(classVarType instanceof ClassType)) {
				// System.out.println("Type checking classVarType: " + classVarType + " and clsVar = " + clsVar);
				ToolError.reportError(ErrorMessage.MUST_BE_LITERAL_CLASS, this, classVarType.toString());
			}

			// TODO SMELL: do I really need to store this?  Can get it any time from the type
			cls = classVarTypeBinding.getClassDecl();

			return classVarType;
		} else { // Standalone
			isGeneric = true;
			LinkedList<Declaration> decls = new LinkedList<>();

            Environment mockEnv = Environment.getEmptyEnvironment();

			for (Map.Entry<String, TypedAST> elem : args.entrySet()) {
				ValDeclaration e = new ValDeclaration(elem.getKey(), elem.getValue(), elem.getValue().getLocation());
				e.typecheck(env);
                mockEnv = e.extend(mockEnv);
				decls.add(e);
			}

			ClassDeclaration classDeclaration = new ClassDeclaration("generic" + generic_num++, "", "", new DeclSequence(decls), mockEnv, new LinkedList<String>(), getLocation());
			cls = classDeclaration;
			return new ClassType(new Reference<>(mockEnv), new Reference<>(mockEnv), new LinkedList<String>());
		}
	}

	@Override
	public Value evaluate(Environment env) {
		Environment argValEnv = Environment.getEmptyEnvironment();
		for (Entry<String, TypedAST> elem : args.entrySet())
			argValEnv = argValEnv.extend(new ValueBinding(elem.getKey(), elem.getValue().evaluate(env)));
		cls.evalDecl(env, cls.extendWithValue(Environment.getEmptyEnvironment()));
		AtomicReference<Value> objRef = new AtomicReference<>();
		objRef.set(new Obj(cls.getFilledBody(objRef).extend(argValEnv)));
		return objRef.get();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return args;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {

		return new New(newChildren, location);
	}

	public ClassDeclaration getClassDecl() {
		return cls;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		//TODO: fix args
		visitor.visit(this);
	}
	
	public Map<String, TypedAST> getArgs() {
		return args;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

	public boolean isGeneric() {
		return isGeneric;
	}
}