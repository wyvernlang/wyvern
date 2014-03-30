package wyvern.tools.typedAST.core.declarations;

import java.util.*;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern2.ast.decl.Decl;
import wyvern2.ast.decl.DeclSeq;

public class DeclSequence extends Sequence implements EnvironmentExtender {

	public DeclSequence() {
		this(Arrays.asList());
	}


	public static DeclSequence simplify(DeclSequence in) {
		return new DeclSequence(new Iterable() {
			@Override
			public Iterator iterator() {
				return new Iterator() {
					private Stack<Iterator> iterstack = new Stack<>();
					{
						iterstack.push(in.iterator());
					}
					private Object lookahead;

					@Override
					public boolean hasNext() {
						if (!iterstack.empty()) {
							if (!iterstack.peek().hasNext()) {
								iterstack.pop();
								return hasNext();
							}
							lookahead = iterstack.peek().next();
							if (lookahead instanceof DeclSequence) {
								iterstack.push(((DeclSequence) lookahead).iterator());
								lookahead = null;
								return hasNext();
							}
							return true;
						}
						return false;
					}

					@Override
					public Object next() {
						if (lookahead != null) {
							Object res = lookahead;
							lookahead = null;
							return res;
						}
						return iterstack.peek().next();
					}
				};
			}
		});
	}
	
	public DeclSequence(final Iterable first) {
		super(new Iterable<TypedAST>() {

			@Override
			public Iterator<TypedAST> iterator() {
				final Iterator<EnvironmentExtender> iter = first.iterator();
				return new Iterator<TypedAST>() {
					@Override
					public boolean hasNext() {
						return iter.hasNext();
					}

					@Override
					public TypedAST next() {
						return iter.next();
					}

					@Override
					public void remove() {
						iter.remove();
					}

				};
			}

		});
		
	}

	public DeclSequence(final Sequence declAST) {
		super(new Iterable<TypedAST>() {

			@Override
			public Iterator<TypedAST> iterator() {
				final Iterator<TypedAST> iter = declAST.iterator();
				return new Iterator<TypedAST>() {

					@Override
					public boolean hasNext() {
						return iter.hasNext();
					}

					@Override
					public TypedAST next() {
						return iter.next();
					}

					@Override
					public void remove() {
						iter.remove();
					}
					
				};
			}
			
		});
	}

	public DeclSequence(Declaration decl) {
		super(decl);
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Environment wtypes = extendType(env);
		env = extendName(wtypes, wtypes);
		for (Declaration d : this.getDeclIterator())
			env = d.extend(env);

		for (TypedAST d : this) {
			d.typecheck(env, Optional.empty());
		}
		
		return wyvern.tools.types.extensions.Unit.getInstance();
	}
	
	public static DeclSequence getDeclSeq(TypedAST ast) {
		if (ast instanceof Declaration)
			return new DeclSequence((Declaration)ast);
		if (ast instanceof Sequence)
			return new DeclSequence((Sequence)ast);
		ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ast);
		return null;
	}

	public final Environment extendWithDecls(Environment env) {
		Environment newEnv = env;
		for (Declaration d : this.getDeclIterator()) {
			newEnv = d.extendWithValue(newEnv);
		}
		return newEnv;
	}
	
	public final Environment evalDecls(Environment env) {
		return bindDecls(extendWithDecls(env));
	}
	
	public final Environment bindDecls(Environment env) {
		Environment newEnv = env;
		for (Declaration d : this.getDeclIterator()) {
			d.evalDecl(newEnv, newEnv);
		}
		return newEnv;
	}
	
	public final Environment bindDecls(Environment bodyEnv, Environment declEnv) {
		Environment newEnv = declEnv;
		for (Declaration d : this.getDeclIterator()) {
			d.evalDecl(bodyEnv, declEnv);
		}
		return newEnv;
	}

	@Override
	public Environment extendType(Environment env) {
		Environment nenv = env;
		for (Declaration d : this.getDeclIterator()) {
			nenv = d.extendType(nenv);
		}
		return nenv;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		Environment nenv = env;
		for (Declaration d : this.getDeclIterator()) {
			nenv = d.extendName(nenv, against);
		}
		return nenv;
	}

	public final Environment extend(Environment old) {
		Environment wtypes = extendType(old);
		Environment newEnv = extendName(wtypes, wtypes);
		for (EnvironmentExtender d : this.getEnvExts())
			newEnv = d.extend(newEnv);
		return newEnv;
	}

	@Override
	public Environment evalDecl(Environment env) {
		return evalDecls(env);
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		int i = 0;
		for (TypedAST ast : this) {
			childMap.put(i++ + "", ast);
		}
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		List<TypedAST> result = new ArrayList<>(newChildren.size());
		for (int i = 0; i < newChildren.size(); i++) {
			result.add(newChildren.get(i + ""));
		}
		return new DeclSequence(result);
	}
}
