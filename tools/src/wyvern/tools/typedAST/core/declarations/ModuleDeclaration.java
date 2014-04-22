package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;

public class ModuleDeclaration extends Declaration implements CoreAST {
	private final String name;
	private final EnvironmentExtender inner;
	private FileLocation location;
	private final ClassType selfType;
	private Reference<Environment> importEnv = new Reference<>(Environment.getEmptyEnvironment());
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

	private Iterable<TypedAST> getInnerIterable() {
		if (inner instanceof Sequence) {
			return ((Sequence) inner).getIterator();
		}
		final Reference<Boolean> gotten = new Reference<>(false);
		return () -> new Iterator<TypedAST>() {
			@Override
			public boolean hasNext() {
				return !gotten.get();
			}

			@Override
			public EnvironmentExtender next() {
				gotten.set(true);
				return inner;
			}
		};
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
			for (TypedAST ast : getInnerIterable()) {
				if (ast instanceof ImportDeclaration) {
					importEnv.set(((ImportDeclaration) ast).extendType(importEnv.get(), Globals.getStandardEnv()));
				} else if (ast instanceof EnvironmentExtender) {
					dclEnv.set(((EnvironmentExtender) ast).extendType(dclEnv.get(), importEnv.get().extend(Globals.getStandardEnv())));
				}
			}
			typeGuard = true;
		}
		return extend;
	}

	boolean nameGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		if (!nameGuard) {
			for (TypedAST ast : getInnerIterable()) {
				if (ast instanceof ImportDeclaration) {
					importEnv.set(((ImportDeclaration) ast).extendName(importEnv.get(), Globals.getStandardEnv()));
				} else if (ast instanceof EnvironmentExtender) {
					dclEnv.set(((EnvironmentExtender) ast).extendName(dclEnv.get(), Globals.getStandardEnv().extend(importEnv.get())));
				}
			}
			nameGuard = true;
		}
		return env;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old.extend(new ValueBinding(name, selfType));
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		ValueBinding selfBinding = (ValueBinding) declEnv.lookup(name);
		Environment objEnv = Environment.getEmptyEnvironment();
		Value selfV = new Obj(inner.evalDecl(objEnv));
		selfBinding.setValue(selfV);
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

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
}
