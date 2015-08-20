package wyvern.tools.typedAST.core.declarations;

//import wyvern.targets.java.annotations.Val;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.DeclarationWriter;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;

import java.util.*;

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
        writer.wrap(e->new Let(varname, new New(outputDecls, "this", null), (Expression)e));
    }

	public DeclSequence filterRequires() {
		
		DeclSequence reqSeq = new DeclSequence();
		for (Declaration d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && ((ImportDeclaration) d).isRequire()) {
				Sequence.append(reqSeq, d);
			}
		}
		return reqSeq;
	}

	public DeclSequence filterImportInstantiates() {
		DeclSequence impInstSeq = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && !((ImportDeclaration) d).isRequire()
					|| d instanceof Instantiation) {
				Sequence.append(impInstSeq, d);
			}
		}
		return impInstSeq;
	}

	public DeclSequence filterNormal() {
		DeclSequence normalSeq = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(!(d instanceof ImportDeclaration) && !(d instanceof Instantiation)) {
				Sequence.append(normalSeq, d);
			}
		}
		return normalSeq;
	}
	
}
