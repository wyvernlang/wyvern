package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

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
	protected Type doTypecheck(Environment env) {
		decl.typecheck(env);
		Environment newEnv = decl.extendWithDecls(env);
		Type bodyType = body.typecheck(newEnv);
		return bodyType;
	}

	@Override
	public Value evaluate(Environment env) {
		Environment newEnv = decl.evalDecls(env);
		return body.evaluate(newEnv);
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
}
