package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.decltype.DeclType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

//import wyvern.targets.java.annotations.Val;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.DeclarationWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;

public class DeclSequence extends Sequence implements EnvironmentExtender {

	public DeclSequence() {
		this(Arrays.asList());
	}

	public DeclSequence(TypedAST... init) {
		this(Arrays.asList(init));
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
		Environment ienv = env;
		Environment wtypes = extendType(env, env);
		env = extendName(wtypes, wtypes);
		for (Declaration d : this.getDeclIterator()) {
			Environment againstEnv = env;
			if ((d instanceof ValDeclaration) || (d instanceof VarDeclaration))
				againstEnv = ienv;
			env = d.extend(env, againstEnv);
		}

		for (TypedAST d : this) {
			d.typecheck(env, Optional.empty());
		}
		
		return new Unit();
	}
	
	public static DeclSequence getDeclSeq(TypedAST ast) {
		if (ast instanceof Declaration)
			return new DeclSequence((Declaration)ast);
		if (ast instanceof Sequence)
			return new DeclSequence((Sequence)ast);
		ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ast);
		return null;
	}

	public final EvaluationEnvironment extendWithDecls(EvaluationEnvironment env) {
		EvaluationEnvironment newEnv = env;
		for (Declaration d : this.getDeclIterator()) {
			newEnv = d.extendWithValue(newEnv);
		}
		return newEnv;
	}
	
	public final EvaluationEnvironment evalDecls(EvaluationEnvironment env) {
		return bindDecls(extendWithDecls(env));
	}
	
	public final EvaluationEnvironment bindDecls(EvaluationEnvironment env) {
		EvaluationEnvironment newEnv = env;
		for (Declaration d : this.getDeclIterator()) {
			d.evalDecl(newEnv, newEnv);
		}
		return newEnv;
	}
	
	public final EvaluationEnvironment bindDecls(EvaluationEnvironment bodyEnv, EvaluationEnvironment declEnv) {
		EvaluationEnvironment newEnv = declEnv;
		for (Declaration d : this.getDeclIterator()) {
			d.evalDecl(bodyEnv, declEnv);
		}
		return newEnv;
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		Environment nenv = env;
		for (Iterator<Declaration> iter = this.getDeclIterator().iterator(); iter.hasNext(); ) {
			Declaration d = iter.next();
			nenv = d.extendType(nenv, against);
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

	public final Environment extend(Environment old, Environment against) {
		Environment wtypes = extendType(old, against);
		Environment newEnv = extendName(wtypes, against);
		for (EnvironmentExtender d : this.getEnvExts())
			newEnv = d.extend(newEnv, against);
		return newEnv;
	}

	@Override
	public EvaluationEnvironment evalDecl(EvaluationEnvironment env) {
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
    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        List<wyvern.target.corewyvernIL.decl.Declaration> outputDecls = new LinkedList<>();
        String varname = GenerationEnvironment.generateVariableName();
        GenerationEnvironment innerEnv = new GenerationEnvironment(environment, varname);
        for (Declaration ast : this.getDeclIterator()) {
            outputDecls.addAll(DeclarationWriter.generate(writer, iw -> ast.codegenToIL(innerEnv, iw)));
        }
        writer.wrap(e->new Let(varname, null, new New(outputDecls, "this", null, getLocation()), (Expression)e));
    }

    /**
     * 
     * @return the sequence of require 
     */
	public Sequence filterRequires() {
		
		Sequence reqSeq = new DeclSequence();
		for (Declaration d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && ((ImportDeclaration) d).isRequire()) {
				reqSeq = Sequence.append(reqSeq, d);
			}
		}
		return reqSeq;
	}

	/**
	 * 
	 * @return the sequence of import/instantiate
	 */
	public Sequence filterImportInstantiates() {
		Sequence impInstSeq = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && !((ImportDeclaration) d).isRequire()
					|| d instanceof Instantiation) {
				impInstSeq = Sequence.append(impInstSeq, d);
			}
		}
		return impInstSeq;
	}

	/**
	 * 
	 * @return the sequence of simple declarations, not require/import/instantiate
	 */
	public Sequence filterNormal() {
		boolean recBlock = false;
		Sequence normalSeq = new Sequence();
		Sequence recSequence = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(d instanceof TypeVarDecl || d instanceof DefDeclaration) {
				if(recBlock == false) {
					recBlock = true;
					recSequence = new DeclSequence();
				}
				recSequence = Sequence.append(recSequence, d);
			} else if(!(d instanceof ImportDeclaration) && !(d instanceof Instantiation)) {
				if(recBlock == true) {
				    recBlock = false;
					normalSeq = Sequence.append(normalSeq, recSequence);
				}
				normalSeq = Sequence.append(normalSeq, d);
			}
		}
		
		if (recBlock == true) {
			normalSeq = Sequence.append(normalSeq, recSequence);
		}
		return normalSeq;
	}
	
	@Override
	public void genTopLevel(TopLevelContext tlc) {
		String newName = GenContext.generateName();
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls =
				new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
				new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
		
		GenContext newCtx = tlc.getContext();
		
		for(TypedAST seq_ast : getDeclIterator()) {
			Declaration d = (Declaration) seq_ast;
			// TODO: refactor to make rec a method of Declaration
			newCtx = newCtx.rec(newName, d); // extend the environment
		}
		
		for(TypedAST seq_ast : getDeclIterator()) {
			Declaration d = (Declaration) seq_ast;
			declts.add(d.genILType(newCtx));
		}
		
		ValueType type = new StructuralType(newName, declts);
		GenContext genCtx = newCtx.extend(newName, new Variable(newName), type);
		
		tlc.updateContext(genCtx);
		tlc.setReceiverName(newName);
		for(TypedAST seq_ast : getDeclIterator()) {
			Declaration d = (Declaration) seq_ast;
			wyvern.target.corewyvernIL.decl.Declaration decl = d.topLevelGen(genCtx, null);
			decls.add(decl);
			d.addModuleDecl(tlc);
		}
		tlc.setReceiverName(null);
	
		// determine if we need to be a resource type
		for (wyvern.target.corewyvernIL.decl.Declaration d: decls) {
			d.typeCheck(tlc.getContext(), tlc.getContext());
			if (d.containsResource(tlc.getContext())) {
				type = new StructuralType(newName, declts, true);
				break;
			}
		}
		
		/* wrap the declarations into an object */
		Expression newExp = new New(decls, newName, type, getLocation());
		tlc.addLet(newName, type, newExp, true);
	}
	
	/**
	 * Figure out the structural type represented by this sequence.
	 * @param ctx: context to evaluate in.
	 * @return structural type of this sequence.
	 */
	public StructuralType inferStructuralType (GenContext ctx, String selfName) {
		boolean isResource = false;
		
		// Fake an appropriate context.
		GenContext ctxTemp = ctx.extend(selfName, new Variable(selfName), null);
		
		// Store the types for each declaration in this list.
		List<DeclType> declTypes = new LinkedList<DeclType>();
		
		// Look at each declaration.
		wyvern.tools.typedAST.core.declarations.DelegateDeclaration delegateDecl = null;
		for (TypedAST d : this) {
			if (d instanceof wyvern.tools.typedAST.core.declarations.DelegateDeclaration) {
				delegateDecl = (wyvern.tools.typedAST.core.declarations.DelegateDeclaration)d;
			}
			else {
				DeclType t = ((Declaration) d).genILType(ctxTemp);
				declTypes.add(t);
			}
		}
		
		// Add delegate object's declaration which has not been overridden to the structural type.
		if (delegateDecl != null) {
			StructuralType delegateStructuralType = delegateDecl.getType().getILType(ctxTemp).getStructuralType(ctxTemp);
			for (DeclType declType : delegateStructuralType.getDeclTypes()) {
				if (!declTypes.stream().anyMatch(newDefDecl-> newDefDecl.isSubtypeOf(declType, ctxTemp))) {
					declTypes.add(declType);
				}
			}
		}
		
		return new StructuralType(selfName, declTypes, isResource);
	}
	
}
