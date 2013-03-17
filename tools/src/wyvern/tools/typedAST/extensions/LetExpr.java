package wyvern.tools.typedAST.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class LetExpr extends CachingTypedAST implements CoreAST {
	private Declaration decl;
	private TypedAST body;
	
	public LetExpr(Declaration decl, TypedAST body) {
		this.decl = decl;
		this.body = body;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(decl, body);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		decl.typecheckAll(env);
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
	
	public TypedAST getBody() {
		return body;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
