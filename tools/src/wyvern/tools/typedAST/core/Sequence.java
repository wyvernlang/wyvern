package wyvern.tools.typedAST.core;

import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.DeclarationWriter;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.*;

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

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        for (TypedAST ast : exps) {
            if (ast instanceof ValDeclaration) {
                environment.register(((ValDeclaration)ast).getName(), ast.getType().generateILType());
                writer.wrap(e->new Let(((ValDeclaration)ast).getName(), ExpressionWriter.generate(iw -> ((ValDeclaration) ast).getDefinition().codegenToIL(environment, iw)), (Expression)e));
            } else if (ast instanceof Declaration) {
                String genName = GenerationEnvironment.generateVariableName();
                List<wyvern.target.corewyvernIL.decl.Declaration> generated =
                        DeclarationWriter.generate(writer, iw -> ast.codegenToIL(new GenerationEnvironment(environment, genName), iw));
                writer.wrap(e->new Let(genName, new New(generated, "this", null), (Expression)e));
            } else {
                ast.codegenToIL(environment, writer);
            }
        }
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(exps.toArray());
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
	public Expression generateIL(GenContext ctx) {
		return generateModuleIL(ctx, false);
	}
	
	public ValueType figureOutType (GenContext ctx) {
		
		// Set up a phony "this" in the context.
		String selfName = "this";
		GenContext tmpCtx = ctx.extend(selfName, new Variable(selfName), null);
		
		// Figure out structural type for the "this" variable.
		List<DeclType> declTypes = new LinkedList<>();

		wyvern.tools.typedAST.core.declarations.DelegateDeclaration delegateDecl = null;
		
		Iterator<TypedAST> iter = this.flatten();
		while (iter.hasNext()) {
			TypedAST ast = iter.next();
			if (ast instanceof wyvern.tools.typedAST.core.declarations.DelegateDeclaration) {
				delegateDecl = (wyvern.tools.typedAST.core.declarations.DelegateDeclaration)ast;
			}
			else if (ast instanceof Declaration) {
				DeclType t = ((Declaration) ast).genILType(tmpCtx);
				declTypes.add(t);
			}
			else {
				System.out.println("wot");
				continue; // some expression, skip it.
			}
		}

		// Add delegate object's declaration which not been overriden.
		if (delegateDecl != null) {
			StructuralType delegateStructuralType = delegateDecl.getType().getILType(tmpCtx).getStructuralType(tmpCtx);
			for (DeclType declType : delegateStructuralType.getDeclTypes()) {
				if (!declTypes.stream().anyMatch(newDefDecl-> newDefDecl.isSubtypeOf(declType, tmpCtx))) {
					declTypes.add(declType);
				}
			}
		}
		
		// Update context with the type of the "this" variable.
		return new StructuralType(selfName, declTypes);
		
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
	public Expression generateModuleIL(GenContext ctx, boolean isModule) {
		
		// Script preprocessing.
		if (!isModule) {

			// Figure out type of "this" to allow for self-referential statements.
			ValueType moduleType = this.figureOutType(ctx);
			ctx = ctx.extend("this", new Variable("this"), moduleType);
			
		}
		TopLevelContext tlc = new TopLevelContext(ctx);
		
		// Generate module IL by looking at everything in sequence.
		Sequence seqWithBlocks = combine();
		seqWithBlocks.genTopLevel(tlc);
		Expression result = isModule?tlc.getModuleExpression():tlc.getExpression();
		return result;
		
	}
	
	/**
	 * Check to see whether execution of this sequence will capture or modify state.
	 * @return boolean
	 */
	private boolean isStateful () {
		for (TypedAST ast : exps) {
			if (ast instanceof VarDeclaration || ast instanceof FieldSet) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Turn a sequence of code without a module into an anonymous module.
	 * @param ctx: ctx to evaluate in.
	 * @return a ModuleDeclaration.
	 */
	public ModuleDeclaration asHeadlessModule (GenContext ctx) {
		boolean isResourceModule = this.isStateful();
		String anonymousName = GenContext.generateName();
		ModuleDeclaration moduleDecl = new ModuleDeclaration(anonymousName, (EnvironmentExtender)this, null, isResourceModule);
		return moduleDecl;
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
}