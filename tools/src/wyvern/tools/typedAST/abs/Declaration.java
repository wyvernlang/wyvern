package wyvern.tools.typedAST.abs;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.AbstractTreeWritable;
import wyvern.tools.util.EvaluationEnvironment;

import java.util.Optional;

// TODO: Consider adding a class "ListOfDeclarations" that only handles indents with decls and make
// Type and Class to be subtypes of that rather than this current Declaration which can be called DeclarationWithBody? (Alex)

// TODO SMELL: probably should have Declarations not be in an "evaluate" part of the AST
public abstract class Declaration extends AbstractTreeWritable implements EnvironmentExtender {
	protected Declaration nextDecl = null;

	/** 
	 * Most declarations simply evaluate to unit without any computation
	 */
	@Override
	public final Value evaluate(EvaluationEnvironment env) {
		// code smell - can we eliminate this function?
		// throw new RuntimeException("cannot evaluate a decl to a value - use evalDecls to get an updated environment");
		return UnitVal.getInstance(this.getLocation());
	}
	
	public final Type typecheckSelf(Environment env) {
		return doTypecheck(env);
	}
	
	public final void typecheckAll(Environment env) {
		Environment newEnv = env;
		for (Declaration d = this; d != null; d = d.nextDecl) {
			d.typecheck(newEnv, Optional.empty());
			newEnv = d.doExtend(newEnv, newEnv);
		}
	}
	
	@Override
	public final Type typecheck(Environment env, Optional<Type> expected) {
		Environment tEnv = this.extendType(env, env);
		Environment nEnv = extendName(tEnv, tEnv);
		Environment newEnv = extend(nEnv, nEnv);
		return typecheckSelf(newEnv);
	}

	public abstract String getName();
	protected abstract Type doTypecheck(Environment env);

	public final Environment extend(Environment old, Environment against) {
		Environment newEnv = doExtend(old, against);
		if (nextDecl != null)
			newEnv = nextDecl.extend(newEnv, newEnv);
		return newEnv;
	}
	
	public final Environment extendWithSelf(Environment old) {
		return doExtend(old, old);
	}

	protected abstract Environment doExtend(Environment old, Environment against);
	public abstract EvaluationEnvironment extendWithValue(EvaluationEnvironment old);
	public abstract void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv);
	
	public final EvaluationEnvironment bindDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		evalDecl(evalEnv, declEnv);
		return evalEnv;
	}
	
	public final EvaluationEnvironment bindDecl(EvaluationEnvironment evalEnv) {
		return bindDecl(evalEnv, evalEnv);
	}
	
	public final EvaluationEnvironment evalDecl(EvaluationEnvironment env) {
		return bindDecl(doExtendWithValue(env));
	}

	public final EvaluationEnvironment doExtendWithValue(EvaluationEnvironment old) {
		return extendWithValue(old);
	}

	public Declaration getNextDecl() {
		return nextDecl;
	}

	public boolean isClassMember() { return false; }

	public abstract DeclType genILType(GenContext ctx);

	public abstract wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext);
	
	/**
	 * Generate IL declaration for top level Module System declaration </br>
	 * 
	 * The difference between topLevelGen and generateDecl is: there is no this context in top level declarations of a module.</br>
	 * Actually I think we can combine generateDecl and topLevelGen. </br>
	 * 
	 * @param ctx the context
	 * @return the declaration generated 
	 */
	public abstract wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx);

	//public abstract void addModuleDecl(TopLevelContext tlc);
	public void addModuleDecl(TopLevelContext tlc) {
		//throw new RuntimeException("not implemented");
	}
}