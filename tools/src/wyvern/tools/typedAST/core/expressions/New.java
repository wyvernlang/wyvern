package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.TreeWriter;

public class New extends CachingTypedAST implements CoreAST {
	ClassDeclaration cls;
	Map<String, TypedAST> args = new HashMap<String, TypedAST>();

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
		
		Type classVarType = env.lookupType("class").getType();
		
		if (!(classVarType instanceof ClassType)) {
			// System.out.println("Type checking classVarType: " + classVarType + " and clsVar = " + clsVar);
			ToolError.reportError(ErrorMessage.MUST_BE_LITERAL_CLASS, classVarType.toString(), this);
		}
		
		// TODO SMELL: do I really need to store this?  Can get it any time from the type
		cls = ((ClassType) classVarType).getDecl();
		
		return classVarType;
	}

	@Override
	public Value evaluate(Environment env) {
		//Value argVals = args.evaluate(env);
		// TODO: evaluate args
		
		
		Map<String, Value> argVals = new HashMap<String,Value>();
		
		//TODO vars
		for (Entry<String, TypedAST> elem : args.entrySet())
			argVals.put(elem.getKey(), elem.getValue().evaluate(env));
		
		ClassObject clsObject = cls.createObject();
		return new Obj(clsObject, argVals);
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
}