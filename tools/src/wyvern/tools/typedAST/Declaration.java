package wyvern.tools.typedAST;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.extensions.LetExpr;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

// TODO: Consider adding a class "ListOfDeclarations" that only handles indents with decls and make
// Type and Class to be subtypes of that rather than this current Declaration which can be called DeclarationWithBody? (Alex)

// TODO SMELL: probably should have Declarations not be in an "evaluate" part of the AST
public abstract class Declaration extends AbstractTypedAST {
	protected Declaration nextDecl = null;

	/** 
	 * Most declarations simply evaluate to unit without any computation
	 */
	@Override
	public final Value evaluate(Environment env) {
		// code smell - can we eliminate this function?
		throw new RuntimeException("cannot evaluate a decl to a value - use evalDecls to get an updated environment");
		//return UnitVal.getInstance();
	}
	
	public final Type typecheckSelf(Environment env) {
		return doTypecheck(env);
	}
	
	public final void typecheckAll(Environment env) {
		Environment newEnv = env;
		for (Declaration d = this; d != null; d = d.nextDecl) {
			d.typecheck(newEnv);
			newEnv = d.doExtend(newEnv);
		}
	}
	
	@Override
	public final Type typecheck(Environment env) {
		Environment newEnv = extend(env);
		return typecheckSelf(newEnv);
	}

	public abstract String getName();
	protected abstract Type doTypecheck(Environment env);

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return new LineSequenceParser() {
			@Override
			public TypedAST parse(
					TypedAST first,
					LineSequence rest,
					Environment env) {
				
				Environment newEnv = extend(env);
				TypedAST body = rest.accept(CoreParser.getInstance(), newEnv);
				
				// build a let construct, or build/extend a decl block, depending on whether the next thing is a decl
				if (body instanceof LetExpr) {
					LetExpr let = (LetExpr) body;
					nextDecl = let.addDecl(Declaration.this);
					return body;
				} else if (body instanceof Declaration) {
					Declaration decl = (Declaration) body;
					nextDecl = decl;
					return Declaration.this;
				} else
					return new LetExpr(Declaration.this, body);
			}
			
		};
	}
	
	public final Environment extend(Environment old) {
		Environment newEnv = doExtend(old);
		if (nextDecl != null)
			newEnv = nextDecl.extend(newEnv);
		return newEnv;
	}

	protected abstract Environment doExtend(Environment old);
	protected abstract Environment extendWithValue(Environment old);
	protected abstract void evalDecl(Environment evalEnv, Environment declEnv);
	
	public final Environment extendWithDecls(Environment env) {
		Environment newEnv = env;
		for (Declaration d = this; d != null; d = d.nextDecl) {
			newEnv = d.extendWithValue(newEnv);
		}
		return newEnv;
	}
	
	public final Environment bindDecls(Environment env) {
		Environment newEnv = env;
		for (Declaration d = this; d != null; d = d.nextDecl) {
			d.evalDecl(newEnv, newEnv);
		}
		return newEnv;
	}
	
	public final Environment bindDecls(Environment bodyEnv, Environment declEnv) {
		Environment newEnv = bodyEnv;
		for (Declaration d = this; d != null; d = d.nextDecl) {
			d.evalDecl(bodyEnv, declEnv);
		}
		return newEnv;
	}

	public final Environment evalDecls(Environment env) {
		return bindDecls(extendWithDecls(env));
	}

	public Declaration getNextDecl() {
		return nextDecl;
	}
}