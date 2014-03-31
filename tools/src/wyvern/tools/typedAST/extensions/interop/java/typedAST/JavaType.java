package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JavaType implements EnvironmentExtender {
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
	public Value evaluate(Environment env) {
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
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Environment extend(Environment env) {
		return env
				.extend(new TypeBinding(name, equivType))
				.extend(new NameBindingImpl(name, equivType));
	}

	@Override
	public Environment extendType(Environment env) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return env;
	}

	@Override
	public Environment evalDecl(Environment env) {
		return env;
	}
}
