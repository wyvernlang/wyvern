package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.binding.AssignableValueBinding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;


public class Variable extends AbstractExpressionAST implements CoreAST, Assignable {
	private NameBinding binding;
	
	public Variable(NameBinding binding, FileLocation location) {
		this.binding = binding;
		this.location = location;
	}

	public String getName() {
		return this.binding.getName();
	}
	
	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName());		
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		// System.out.println("In variable: " + binding.getName() + ":" + getType());
		
		Type type = getType();
		
		if (type instanceof TypeType) {
			TypeType tt = (TypeType) type;
			// System.out.println("tt = " + tt.getName());
		}
		
		if (type == null) {
			String name = binding.getName();
			binding = env.lookup(name);
			if (binding == null)
				reportError(VARIABLE_NOT_DECLARED, this, name);
			else
				type = binding.getType();
		}
		return TypeResolver.resolve(type,env);
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		//Value value = binding.getValue(env);
		Value value = env.lookup(binding.getName()).orElseThrow(() -> new RuntimeException("Invalid variable name ")).getValue(env);

		if (value instanceof VarValue) {
			return ((VarValue)value).getValue();
		}
		return value;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void checkAssignment(Assignment ass, Environment env) {
		AssignableNameBinding vb =
				env.lookupBinding(binding.getName(), AssignableNameBinding.class)
					.orElseThrow(() -> new RuntimeException("Cannot set a non-existent or immutable var"));
	}

	@Override
	public Value evaluateAssignment(Assignment ass, EvaluationEnvironment env) {
		Value value = ass.getValue().evaluate(env);
		env.lookupValueBinding(binding.getName(), AssignableValueBinding.class)
				.orElseThrow(() -> new RuntimeException("Invalid assignment"))
				.assign(value);

		return value;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new Hashtable<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new Variable(binding, location);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        writer.write(environment.lookupVar(binding.getName(), writer));
    }

    private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		return ctx.lookupExp(getName());
	}
	
	@Override
	public CallableExprGenerator getCallableExpr(GenContext ctx) {
		return ctx.getCallableExpr(getName());
	}
	
	@Override
	public void genTopLevel (TopLevelContext tlc) {
		
		// TODO: only perform the below if the variable in question was declared with Var.
		// Otherwise, perform the default genTopLevel.
		
		// Figure out name of anonymous object.
		Optional<Variable> anonReferenceOpt = tlc.anonymousObjectReference(this.getName());
		if (!anonReferenceOpt.isPresent())
			ToolError.reportError(ErrorMessage.VARIABLE_NOT_DECLARED, this);
		Variable anonReference = anonReferenceOpt.get();
		
		// Replace with an invocation to anonymous object's getter method.
		Invocation anonInvocation = new Invocation(anonReference, TopLevelContext.anonymousGetterName(), null, null);
		Application anonMethodCall = new Application(anonInvocation, UnitVal.getInstance(null), null);
		anonMethodCall.genTopLevel(tlc);
		
	}
	
}