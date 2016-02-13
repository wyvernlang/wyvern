package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.AbstractTreeWritable;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class JavaType extends AbstractTreeWritable implements EnvironmentExtender {
	private final Type equivType;
	private final String name;

	public JavaType(String name, Type equivType) {
		this.name = name;
		this.equivType = equivType;
	}

	@Override
	public Type getType() {
		return equivType;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return equivType;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return null;
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
        throw new WyvernException("Cannot generate IL for a Java type", FileLocation.UNKNOWN);
    }

    @Override
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Environment extend(Environment env, Environment against) {
		return env
				.extend(new TypeBinding(name, equivType))
				.extend(new NameBindingImpl(name, equivType));
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return env;
	}

	@Override
	public EvaluationEnvironment evalDecl(EvaluationEnvironment env) {
		return env;
	}
}
