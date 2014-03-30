package wyvern.tools.typedAST.core;

import java.util.*;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Sequence implements CoreAST, Iterable<TypedAST> {
	private LinkedList<TypedAST> exps = new LinkedList<TypedAST>();
	private Type retType = null;

	public static interface MapCallback {
		public void map(TypedAST elem);
	}
	public static void tryMap(TypedAST potential, MapCallback callback) {
		if (!(potential instanceof Sequence))
			return;
		Sequence seq = (Sequence)potential;
		for (TypedAST elem : seq) {
			if (elem instanceof Sequence) {
				tryMap(elem, callback);
				continue;
			}
			callback.map(elem);
		}
	}
	
	public Sequence(TypedAST first) {
		exps.add(first);
	}
	public Sequence(Iterable<TypedAST> first) {
		exps = new LinkedList<TypedAST>();
		for (TypedAST elem : first)
			exps.add(elem);
	}
	public Sequence() {
		// TODO Auto-generated constructor stub
	}
	public void append(TypedAST exp) {
		this.exps.add(exp);
	}
	
	@Override
	public Type getType() {
		if (retType == null)
			ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, this);
		return retType;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type lastType = wyvern.tools.types.extensions.Unit.getInstance();
		for (TypedAST t : exps) {
			lastType = t.typecheck(env, Optional.empty());
			if (t instanceof EnvironmentExtender)
				env = ((EnvironmentExtender) t).extend(env);
		}
		retType = lastType;
		return lastType;
	}

	@Override
	public Value evaluate(Environment env) {
		Environment iEnv = env;
		Value lastVal = UnitVal.getInstance(this.getLocation());
		for (TypedAST exp : this) {
			if (exp instanceof EnvironmentExtender) {
				iEnv = ((EnvironmentExtender)exp).evalDecl(iEnv);
			} else {
				lastVal = exp.evaluate(iEnv);
			}
		}
		return lastVal;
	}

	@Override
	public LineParser getLineParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		int i = 0;
		for (TypedAST ast : exps) {
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
		return new Sequence(result);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return this.exps.toString();
	}

	@Override
	public Iterator<TypedAST> iterator() {
		return exps.iterator();
	}
	
	public static Sequence fromAST(Sequence s) {
		return s;
	}
	
	public static Sequence fromAST(TypedAST s) {
		return new Sequence(s);
	}
	
	// FIXME: Hack. Flattens decl sequence. NEED TO REFACTOR!
	public Iterable<Declaration> getDeclIterator() {
		final Iterator<TypedAST> inner = iterator();
		return new Iterable<Declaration>() {

			@Override
			public Iterator<Declaration> iterator() {
				return new Iterator<Declaration>() {
					
					Iterator<Declaration> ss = null;

					@Override
					public boolean hasNext() {
						if (ss != null && ss.hasNext()) {
							return ss.hasNext();
						} else {
							ss = null;
							return inner.hasNext();
						}
					}

					@Override
					public Declaration next() {
						if (ss != null && ss.hasNext()) {
							return ss.next();
						} else {
							ss = null;
							TypedAST next = inner.next();
							if (next instanceof Declaration) {
								return (Declaration) next;
							} else if (next instanceof DeclSequence) {
								ss = ((DeclSequence) next).getDeclIterator().iterator();
								return ss.next();
							} else {
								return (Declaration) inner.next(); // Will cause a cast error.
							}
						}
					}

					@Override
					public void remove() {
						if (ss != null && ss.hasNext()) {
							ss.remove();
						} else {
							ss = null;
							inner.remove();
						}
					}
				};
			}

		};
	}

	public Iterable<EnvironmentExtender> getEnvExts() {
		return getIterator();
	}

	public <T extends TypedAST> Iterable<T> getIterator() {
		final Iterator<TypedAST> inner = iterator();
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {

					@Override
					public boolean hasNext() {
						return inner.hasNext();
					}

					@Override
					public T next() {
						return (T)inner.next();
					}

					@Override
					public void remove() {
						inner.remove();

					}
				};
			}

		};
	}

	public TypedAST getLast() {
		return exps.getLast();
	}
	public int size() {
		return exps.size();
	}

	public Iterator<TypedAST> flatten() {
		final Iterator<TypedAST> internal = this.getIterator().iterator();
		return new Iterator<TypedAST>() {
			private Iterator<TypedAST> flattened = null;
			@Override
			public boolean hasNext() {
				return (flattened != null && flattened.hasNext()) || internal.hasNext();
			}

			@Override
			public TypedAST next() {
				if (flattened != null && flattened.hasNext()) {
					return flattened.next();
				}

				TypedAST newT = internal.next();
				if (newT instanceof Sequence) {
					flattened = ((Sequence) newT).flatten();
					if (flattened.hasNext())
						return flattened.next();
				}

				return newT;  //To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			public void remove() {
				throw new RuntimeException();
			}
		};
	}
}