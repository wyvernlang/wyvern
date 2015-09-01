package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class LetExpr extends CachingTypedAST implements CoreAST {
	private DeclSequence decl;
	private TypedAST body;
	
	public LetExpr(DeclSequence decl, TypedAST body) {
		this.decl = decl;
		this.body = body;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(decl, body);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		decl.typecheck(env, Optional.empty());
		Environment newEnv = decl.extend(env,env);
		Type bodyType = body.typecheck(newEnv, Optional.empty());
		return bodyType;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		EvaluationEnvironment newEnv = decl.evalDecls(env);
		return body.evaluate(newEnv);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("decl", decl);
		childMap.put("body", body);
		return childMap;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new RuntimeException("Let expression translation not implemented");
    }

    @Override
	public TypedAST doClone(Map<String, TypedAST> newChildren) {
		return new LetExpr((DeclSequence)newChildren.get("decl"), newChildren.get("body"));
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	/*
	// TODO: SMELL: maybe should have a list of decls here? but reuse between letexpr and class?
	// depends on declToAdd adding the returned decl to itself
	public Declaration addDecl(Declaration declToAdd) {
		Declaration old = decl;
		decl = declToAdd;
		return old;
	}
	public Declaration getDecl() {
		return decl;
	}	
	*/
	
	public TypedAST getBody() {
		return body;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		final Iterator<Declaration> declIter = decl.getDeclIterator().iterator();
		Iterator<TypedAST> myIter = new Iterator<TypedAST>() {
			boolean returnedBody = false;
			
			@Override
			public boolean hasNext() {
				return !returnedBody;
			}

			@Override
			public TypedAST next() {
				if (declIter.hasNext())
					return declIter.next();
				returnedBody = true;
				return body;
			}			
		};
		return GenUtil.doGenIL(ctx, myIter);
		
		/*if (!declIter.hasNext())
			throw new RuntimeException("oops, no decls in the let");
		Declaration d = declIter.next();
		if (declIter.hasNext())
			throw new RuntimeException("only handle lets with one decl for now");
		if (d instanceof ValDeclaration) {
			ValDeclaration vd = (ValDeclaration) d;
			String name = vd.getName();
			return new Let(name, vd.getDefinition().generateIL(ctx), body.generateIL(ctx.extend(name, new wyvern.target.corewyvernIL.expression.Variable(name))));
		} else {
			throw new RuntimeException("only handle val decls for now");			
		}
		*/
	}
}
