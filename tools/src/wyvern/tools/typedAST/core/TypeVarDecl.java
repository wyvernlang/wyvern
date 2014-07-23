package wyvern.tools.typedAST.core;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TypeVarDecl extends Declaration {
	private final String name;
	private final EnvironmentExtender body;
	private final FileLocation fileLocation;
	private final Optional<TypedAST> metadata;
	/**
	 * Helper class to allow easy variation of bound types
	 */
	private static abstract class EnvironmentExtInner implements EnvironmentExtender {

		private final FileLocation loc;

		public EnvironmentExtInner(FileLocation loc) {
			this.loc = loc;
		}

		@Override
		public Environment extendName(Environment env, Environment against) {
			return env;
		}

		@Override
		public Environment extend(Environment env, Environment against) {
			return env;
		}

		@Override
		public Environment evalDecl(Environment env) {
			return env;
		}
		@Override
		public Type typecheck(Environment env, Optional<Type> expected) {
			return getType();
		}

		@Override
		public Value evaluate(Environment env) {
			return UnitVal.getInstance(loc);
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
		public FileLocation getLocation() {
			return loc;
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {

		}

	}


	public TypeVarDecl(String name, DeclSequence body, TypedAST metadata, FileLocation fileLocation) {
		this.name = name;
		this.body = new TypeDeclaration(name, body, metadata, fileLocation);
		this.fileLocation = fileLocation;
		this.metadata = Optional.ofNullable(metadata);
	}

	public TypeVarDecl(String name, Type body, FileLocation fileLocation) {
		this.name = name;
		this.body = new EnvironmentExtInner(fileLocation) {
			@Override
			public Environment extendType(Environment env, Environment against) {
				return env.extend(new TypeBinding(name, TypeResolver.resolve(body,against)));
			}

			@Override
			public Type getType() {
				return body;
			}
		};
		this.fileLocation = fileLocation;
		metadata = Optional.empty();
	}

	public TypeVarDecl(String name, EnvironmentExtender body, FileLocation fileLocation) {
		this.body = body;
		this.name = name;
		this.fileLocation = fileLocation;
		metadata = Optional.empty();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return body.typecheck(env, Optional.<Type>empty());
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return body.extend(old, against);
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return body.evalDecl(old);
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		return body.extendType(env, against);
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return body.extendName(env, against);
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String,TypedAST> out = new HashMap<>();
		out.put("body", body);
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TypeVarDecl(name, (EnvironmentExtender)newChildren.get("body"), fileLocation);
	}

	@Override
	public FileLocation getLocation() {
		return fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
