package wyvern.tools.typedAST.extensions;

import java.util.Iterator;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Sequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

public class DeclSequence extends Sequence {
	
	public DeclSequence(final Iterable<Declaration> first) {
		super(new Iterable<TypedAST>() {

			@Override
			public Iterator<TypedAST> iterator() {
				final Iterator<Declaration> iter = first.iterator();
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
						return (Declaration)iter.next();
					}

					@Override
					public void remove() {
						iter.remove();
					}
					
				};
			}
			
		});
	}
	
	@Override
	public Type typecheck(Environment env) {
		for (Declaration d : this.getDeclIterator())
			env = ((Declaration) d).extend(env);
		for (Declaration d : this.getDeclIterator()) {
			d.typecheck(env);
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
		Environment newEnv = bodyEnv;
		for (Declaration d : this.getDeclIterator()) {
			d.evalDecl(bodyEnv, declEnv);
		}
		return newEnv;
	}

	public final Environment extend(Environment old) {
		Environment newEnv = old;
		for (Declaration d : this.getDeclIterator())
			newEnv = d.extend(newEnv);
		return newEnv;
	}
}
