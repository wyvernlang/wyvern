package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class ModuleDeclaration extends Declaration {
	private final String name;
	private final EnvironmentExtender inner;
	private FileLocation location;
	private final ClassType selfType;
	private Reference<Environment> dclEnv = new Reference<>(Environment.getEmptyEnvironment());

	public ModuleDeclaration(String name, EnvironmentExtender inner, FileLocation location) {
		this.name = name;
		this.inner = inner;
		this.location = location;
		selfType = new ClassType(dclEnv, new Reference<>(), new LinkedList<>(), name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return inner.typecheck(env, Optional.empty());
	}

	boolean extGuard = false;
	@Override
	protected Environment doExtend(Environment old) {
		if (!extGuard) {
			dclEnv.set(inner.extend(dclEnv.get()));
		}
		return old.extend(new NameBindingImpl(name, selfType));
	}

	boolean typeGuard = false;
	@Override
	public Environment extendType(Environment extend, Environment against) {
		if (!typeGuard) {
			dclEnv.set(inner.extendType(dclEnv.get(), Globals.getStandardEnv()));
			typeGuard = true;
		}
		return extend;
	}

	boolean nameGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		if (!nameGuard) {
			dclEnv.set(inner.extendName(dclEnv.get(), Globals.getStandardEnv()));
			nameGuard = true;
		}
		return env;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		//TODO
		return null;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		//TODO
	}

	@Override
	public Type getType() {
		return Unit.getInstance();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		if (inner != null)
			childMap.put("body", inner);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new ModuleDeclaration(name, (EnvironmentExtender)newChildren.get("body"), getLocation());
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
