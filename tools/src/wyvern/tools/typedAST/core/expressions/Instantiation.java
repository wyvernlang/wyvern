package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.net.URI;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ToolError.reportEvalError;

public class Instantiation extends CachingTypedAST implements CoreAST {
	private URI uri;
	private TypedAST arg;
	private FileLocation location;
	private String name;
	
	public Instantiation(URI uri, TypedAST arg, String image, FileLocation loc) {
		this.uri = uri;
		this.arg = arg;
		this.location = loc;
		this.name = image;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(arg);
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		// TODO Auto-generated method stub
		return new Unit();
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("arg", arg);
		return children;
	}

	@Override
	protected TypedAST doClone(Map<String, TypedAST> nc) {
		return new Instantiation(uri, nc.get("arg"), name, location);
	}

	@Override
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public URI getUri() {
		return this.uri;
	}

	public TypedAST getArgs() {
		return this.arg;
	}

	public String getName() {
		return name;
	}

}
