package wyvern.tools.typedAST.core.evaluation;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Closure extends AbstractValue implements ApplyableValue {
	private BoundCode function;
	private EvaluationEnvironment env;

	public Closure(BoundCode function, EvaluationEnvironment env) {
		this.function = function;
		this.env = env;
	}

	@Override
	public Type getType() {
		return function.getType();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Cannot generate IL for runtime closure", this);
    }

    public TypedAST getInner() {
		return function.getBody();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(function, env);
	}

	@Override
	public Value evaluateApplication(Application app, EvaluationEnvironment argEnv) {
		Value argValue = app.getArgument().evaluate(argEnv);
		EvaluationEnvironment bodyEnv = env;
		List<NameBinding> bindings = function.getArgBindings();
		if (bindings.size() == 1)
			bodyEnv = bodyEnv.extend(new ValueBinding(bindings.get(0).getName(), argValue));
		else if (bindings.size() > 1 && argValue instanceof TupleValue)
			for (int i = 0; i < bindings.size(); i++)
				bodyEnv = bodyEnv.extend(new ValueBinding(bindings.get(i).getName(), ((TupleValue)argValue).getValue(i)));
		else if (bindings.size() != 0)
			throw new RuntimeException("Something bad happened!");
		
		/*
		if (app.getFunction() instanceof Variable) {
			Variable v = (Variable) app.getFunction();
			if (v.getName().equals("screenCap")) {
				System.out.println("Processing closure with " + v);
				System.out.println("function = " + function);
				System.out.println("argValue = " + argValue);
			}
		}
		*/
		
		return function.getBody().evaluate(bodyEnv);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
