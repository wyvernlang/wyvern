package wyvern.tools.typedAST.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.DeclarationWriter;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class Sequence extends AbstractExpressionAST implements CoreAST, Iterable<TypedAST> {
	private LinkedList<TypedAST> exps = new LinkedList<TypedAST>();
	private Type retType = null;

	private TypedAST check(TypedAST e) {
		//if (e == null)
		//	throw new RuntimeException("no null values in Sequence");
		return e;
	}
	
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

	public static Sequence append(Sequence s, TypedAST e) {
		Iterator<TypedAST> innerIter = s.iterator();
		if (s instanceof DeclSequence && e instanceof Declaration) {
			return DeclSequence.simplify(new DeclSequence(s, e));
		}
		return new Sequence(() -> new Iterator<TypedAST>() {
			private boolean fetched = false;
			@Override
			public boolean hasNext() {
				if (innerIter.hasNext())
					return true;
				return !fetched;
			}

			@Override
			public TypedAST next() {
				if (innerIter.hasNext())
					return innerIter.next();
				if (!fetched) {
					fetched = true;
					return e;
				}
				throw new RuntimeException();
			}
		});
	}

	public Sequence(TypedAST first) {
		exps.add(check(first));
	}
	public Sequence(Iterable<TypedAST> first) {
		exps = new LinkedList<TypedAST>();
		for (TypedAST elem : first)
			exps.add(check(elem));
	}

	public Sequence(TypedAST first, TypedAST second) {
		if (first instanceof Sequence) {
			exps.addAll(((Sequence) first).exps);
		} else if (first != null) {
			exps.add(check(first));
		}
		if (second instanceof Sequence) {
			exps.addAll(((Sequence) second).exps);
		} else if (second != null) {
			exps.add(check(second));
		}
	}

	public Sequence() {
		// TODO Auto-generated constructor stub
	}
	public void append(TypedAST exp) {
		this.exps.add(check(exp));
	}
	
	@Override
	public Type getType() {
		if (retType == null)
			ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, this);
		return retType;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type lastType = new Unit();
		for (TypedAST t : exps) {
			if (t == null) continue;
			lastType = t.typecheck(env, (exps.getLast() == t)?expected:Optional.empty());
			if (t instanceof EnvironmentExtender)
				env = ((EnvironmentExtender) t).extend(env, env);
		}
		retType = lastType;
		return lastType;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		EvaluationEnvironment iEnv = env;
		Value lastVal = UnitVal.getInstance(this.getLocation());
		for (TypedAST exp : this) {
			if (exp == null) continue;
			if (exp instanceof EnvironmentExtender) {
				iEnv = ((EnvironmentExtender)exp).evalDecl(iEnv);
			} else {
				lastVal = exp.evaluate(iEnv);
			}
		}
		return lastVal;
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

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
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

		final Iterator<TypedAST> inner = flatten();
		return new Iterable<Declaration>() {

			@Override
			public Iterator<Declaration> iterator() {
				return new Iterator<Declaration>() {
					@Override
					public boolean hasNext() {
						return inner.hasNext();
					}

					@Override
					public Declaration next() {
						return (Declaration)inner.next();
					}

					@Override
					public void remove() {
						inner.remove();
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
			private Stack<Iterator<TypedAST>> iterstack = new Stack<>();
			{
				iterstack.push(internal);
			}
			private TypedAST lookahead;

			@Override
			public boolean hasNext() {
				if (!iterstack.empty()) {
					if (!iterstack.peek().hasNext()) {
						iterstack.pop();
						return hasNext();
					}
					lookahead = iterstack.peek().next();
					if (lookahead == null) {
						iterstack.peek().hasNext();
						throw new RuntimeException("null lookahead!");
					}
					if (lookahead instanceof Sequence) {
						iterstack.push(((Sequence) lookahead).iterator());
						lookahead = null;
						return hasNext();
					}
					return true;
				}
				return false;
			}

			@Override
			public TypedAST next() {
				if (lookahead != null) {
					TypedAST res = lookahead;
					lookahead = null;
					return res;
				}
				return iterstack.peek().next();
			}
		};
	}

	@Override
	public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
	    Sequence seqWithBlocks = combine();		
		TopLevelContext tlc = new TopLevelContext(ctx);
		seqWithBlocks.genTopLevel(tlc, expectedType);
		if (tlc.getDependencies().size()>0)
			dependencies.addAll(tlc.getDependencies());
		return tlc.getExpression();
	}
	
	public boolean hasVarDeclaration() {
		for (TypedAST e : exps) {
			if (e instanceof VarDeclaration) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generate IL expression for a top-level declaration sequence</br>
	 * @see GenUtil.doGenModuleIL
	 * 
	 * @param ctx the context
	 * @param isModule whether is is actually a module expression:
	 * 			true for the body of a module, false for the body of a script 
	 * @return the IL expression of a module
	 */
	public IExpr generateModuleIL(GenContext ctx, boolean isModule) {
		Sequence seqWithBlocks = combine();
		TopLevelContext tlc = new TopLevelContext(ctx);
		seqWithBlocks.genTopLevel(tlc);
		IExpr result = isModule? tlc.getModuleExpression() : tlc.getExpression();
		return result;
	}

	@Override
	public void genTopLevel(TopLevelContext tlc) {
		for (TypedAST ast : exps) {
			ast.genTopLevel(tlc);
			if (ast instanceof Declaration) {
				((Declaration)ast).addModuleDecl(tlc);
			}
		}
	}
	
	
	public void genTopLevel(TopLevelContext tlc, ValueType expectedType) {
		for (int i = 0; i < exps.size()-1; i++) {
			TypedAST ast =  exps.get(i);
			ast.genTopLevel(tlc);
			if (ast instanceof Declaration) {
				((Declaration)ast).addModuleDecl(tlc);
			}
		}
		
		TypedAST ast = exps.getLast();
		if (ast instanceof Fn) {
			((Fn)ast).genTopLevel(tlc, expectedType);			
		}
		// Add a unit value on the end, so the declaration evaluates to Unit.
		else if (ast instanceof Declaration) {
			Value v = UnitVal.getInstance(this.getLocation());
			ast.genTopLevel(tlc);
			v.genTopLevel(tlc);
		}
		else {
			ast.genTopLevel(tlc);
		}
		
		if (ast instanceof Declaration) {
			((Declaration)ast).addModuleDecl(tlc);
		}
	}

	/**
	 * A filter for the sequence </br>
	 * Combines the sequential type and method declarations into a block </br>
	 * @return return the sequence after combination.
	 */
	private Sequence combine() {
		boolean recBlock = false;
		Sequence normalSeq = new Sequence();
		Sequence recSequence = new DeclSequence();
		for (TypedAST ast : this.getIterator()) {
			
			if(ast instanceof TypeVarDecl || ast instanceof DefDeclaration || ast instanceof TypeAbbrevDeclaration) {
				Declaration d = (Declaration) ast;
				if(recBlock == false) {
					recBlock = true;
					recSequence = new DeclSequence();
				}
				recSequence = Sequence.append(recSequence, d);
			} else {
				if(recBlock == true) {
					normalSeq = Sequence.append(normalSeq, recSequence);
				}
				normalSeq = Sequence.append(normalSeq, ast);
			    recBlock = false;
			}
		}
		
		if (recBlock == true) {
			normalSeq = Sequence.append(normalSeq, recSequence);
		}
		return normalSeq;
	}

    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        String rtStr = "null";
        if (retType != null)
            rtStr = retType.toString();
        sb.append("Sequence(" + rtStr + ", [");
        String sep = "";
        for (TypedAST ast : exps) {
            sb.append(sep);
            sb.append(ast.prettyPrint());
            sep = ", ";
        }
        return sb;
    }
}
