package wyvern.tools.typedAST.extensions;

import java.util.HashMap;
import java.util.Map;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.values.ClassObject;
import wyvern.tools.typedAST.extensions.values.Obj;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ObjectType;
import wyvern.tools.util.TreeWriter;

public class New extends CachingTypedAST implements CoreAST {
	ClassDeclaration cls;
	Variable clsVar;
	Map<String, TypedAST> args = new HashMap<String, TypedAST>();

	public New(Variable clsVar, TypedAST args) {
		this.clsVar = clsVar;
		// TODO: parse args
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(cls, args);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		// TODO check arg types
		//Type argTypes = args.typecheck();
		
		Type classVarType = clsVar.typecheck(env);
		if (!(classVarType instanceof ObjectType))
			ToolError.reportError(ErrorMessage.MUST_BE_LITERAL_CLASS, clsVar.toString(), clsVar);
		// TODO SMELL: do I really need to store this?  Can get it any time from the type
		cls = ((ObjectType)classVarType).getDecl();
		return classVarType;
	}

	@Override
	public Value evaluate(Environment env) {
		//Value argVals = args.evaluate(env);
		// TODO: evaluate args
		Map<String, Value> argVals = null;
		
		ClassObject clsObject = (ClassObject) clsVar.evaluate(env);
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

}
