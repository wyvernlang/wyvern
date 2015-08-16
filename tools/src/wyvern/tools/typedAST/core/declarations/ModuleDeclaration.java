package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.stream.Stream;

public class ModuleDeclaration extends Declaration implements CoreAST {
	private final String name;
	private final EnvironmentExtender inner;
	private ClassType subTypeType;
	private FileLocation location;
	private ClassType selfType;
	private Reference<Environment> importEnv = new Reference<>(Environment.getEmptyEnvironment());
	private Reference<Environment> dclEnv = new Reference<>(Environment.getEmptyEnvironment());
	private Reference<Environment> typeEnv = new Reference<>(Environment.getEmptyEnvironment());
	private boolean resourceFlag;

	public ModuleDeclaration(String name, EnvironmentExtender inner, FileLocation location, boolean isResource) {
		this.name = name;
		this.inner = inner;
		this.location = location;
		this.resourceFlag = isResource;
		selfType = new ClassType(dclEnv, new Reference<>(), new LinkedList<>(), null, name);
		subTypeType = new ClassType(typeEnv, new Reference<>(), new LinkedList<>(), null, name);
		if (isResource) {
			selfType.setAsResource();
			subTypeType.setAsResource();
		} else {
			selfType.setAsModule();
			subTypeType.setAsModule();
		}
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		inner.typecheck(env, Optional.empty());
		return new Unit();
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
	protected Environment doExtend(Environment old, Environment against) {
		if (!extGuard) {
			dclEnv.set(inner.extend(dclEnv.get(), old.extend(dclEnv.get())));
		}
		return old.extend(new NameBindingImpl(name, selfType)).extend(new TypeBinding(name, subTypeType));
	}

	boolean typeGuard = false;
	@Override
	public Environment extendType(Environment extend, Environment against) {
		if (!typeGuard) {
			for (TypedAST ast : getInnerIterable()) {
				if (ast instanceof ImportDeclaration) {
					importEnv.set(((ImportDeclaration) ast).extendType(importEnv.get(), Globals.getStandardEnv()));
				} else if (ast instanceof EnvironmentExtender) {
					Environment delta = ((EnvironmentExtender) ast).extendType(Environment.getEmptyEnvironment(), importEnv.get().extend(Globals.getStandardEnv()));
					dclEnv.set(dclEnv.get().extend(delta));
					delta.getBindings().stream()
							.flatMap(bndg -> (bndg instanceof TypeBinding)? Stream.of((TypeBinding)bndg) : Stream.empty())
							.forEach(bndg -> typeEnv.set(typeEnv.get().extend(bndg)));
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
					dclEnv.set(((EnvironmentExtender) ast).extendName(dclEnv.get(),
							Globals.getStandardEnv().extend(importEnv.get()).extend(dclEnv.get())));
				}
			}
			nameGuard = true;
		}
		return env.extend(new NameBindingImpl(name, selfType)).extend(new TypeBinding(name, subTypeType));
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		return old.extend(new ValueBinding(name, selfType));
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		ValueBinding selfBinding = declEnv.lookup(name).get();
		EvaluationEnvironment objEnv = EvaluationEnvironment.EMPTY;
		Value selfV = new Obj(inner.evalDecl(objEnv), null);
		selfBinding.setValue(selfV);
	}

	@Override
	public Type getType() {
		return new Unit();
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
		ModuleDeclaration newDecl = new ModuleDeclaration(name, (EnvironmentExtender) newChildren.get("body"), getLocation(), isResource());
		newDecl.selfType = selfType;
		newDecl.subTypeType = subTypeType;
		newDecl.importEnv = importEnv;
		newDecl.typeEnv = typeEnv;
		newDecl.dclEnv = dclEnv;
		return newDecl;
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
	
	public boolean isResource() {
		return this.resourceFlag;
	}
}
