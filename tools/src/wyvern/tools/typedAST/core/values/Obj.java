package wyvern.tools.typedAST.core.values;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.binding.AssignableValueBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Obj extends AbstractValue implements InvokableValue, Assignable {
	protected Reference<EvaluationEnvironment> intEnv;
	private TaggedInfo taggedInfo;
	private Environment typeEquivEnv;
	
	public Obj(EvaluationEnvironment declEnv, TaggedInfo taggedInfo) {
		this.taggedInfo = taggedInfo;
		this.intEnv = new Reference<>(declEnv);
	}

    public Obj(Reference<EvaluationEnvironment> declEnv, TaggedInfo taggedInfo) {
		intEnv = declEnv;
		this.taggedInfo = taggedInfo;
	}

    private void updateTee() {
        typeEquivEnv = TypeDeclUtils.getTypeEquivalentEnvironment(intEnv.get().toTypeEnv());
    }

	@Override
	public Type getType() {
		if (typeEquivEnv == null)
        	updateTee();
		return new ClassType(intEnv.map(EvaluationEnvironment::toTypeEnv), new Reference<>(typeEquivEnv), new LinkedList<String>(), taggedInfo, null);
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
        throw new WyvernException("Value conversion to IL not possible", this);
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Value evaluateInvocation(Invocation exp, EvaluationEnvironment env) {
		String operation = exp.getOperationName();
		return getIntEnv().lookup(operation).orElseThrow(() -> new RuntimeException("Cannot find class member"))
				.getValue(env.extend(new ValueBinding("this", this)));
	}
	
	public EvaluationEnvironment getIntEnv() {
		return intEnv.get();
	}

	@Override
	public void checkAssignment(Assignment ass, Environment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();
		intEnv.get().lookupValueBinding(operation, AssignableValueBinding.class)
				.orElseThrow(() -> new RuntimeException("Cannot set a non-existent or immutable var"));

		return;
	}

	@Override
	public Value evaluateAssignment(Assignment ass, EvaluationEnvironment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();

		Value newValue = ass.getValue().evaluate(env);

		intEnv.get().lookupValueBinding(operation, AssignableValueBinding.class)
				.orElseThrow(() -> new RuntimeException("Trying to assign a non-var"))
				.assign(newValue);

		return newValue;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	public TaggedInfo getTaggedInfo() {
		return taggedInfo;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}