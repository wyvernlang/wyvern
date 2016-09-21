package wyvern.tools.typedAST.extensions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.Executor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class ExternalFunction extends AbstractValue implements ApplyableValue, CoreAST {
	private Type type;
	private Executor exec;
	private FileLocation location = FileLocation.UNKNOWN;

	public ExternalFunction(Type type, Executor exec) {
		this.type = type;
		this.exec = exec;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<String, TypedAST>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Cannot generate IL for an external function", this);
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
		// no arguments at the moment; also not intended for serialization		
	}

	@Override
	public Value evaluateApplication(Application app, EvaluationEnvironment env) {
		Value argValue = app.getArgument().evaluate(env);
		return exec.execute(env, argValue);
	}

	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
