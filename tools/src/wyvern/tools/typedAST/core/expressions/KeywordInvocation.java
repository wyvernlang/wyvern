package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeywordInvocation implements CoreAST {
	private final TypedAST tgt;
	private final String id;
	private final TypedAST lit;
	private FileLocation fileLocation = FileLocation.UNKNOWN;
	private Type type = null;

	public KeywordInvocation(TypedAST l, String id, TypedAST lit, FileLocation fileLocation) {
		this.tgt = l;
		this.id = id;
		this.lit = lit;
		this.fileLocation = fileLocation;
		if (this.lit instanceof DSLLit)
			((DSLLit) this.lit).setIsKwDSL();
		else 
			System.err.println("[ERROR] DSLLit in keyword type incorrect.");
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		System.out.println("Env: " + env.toString());
		System.out.println("target type:" + ((Variable)this.tgt).typecheck(env, Optional.empty()));
		System.out.println("Expect? " + expected);
		
		Type resolved = null;
		resolved = TypeResolver.resolve(((Variable)this.tgt).typecheck(env, Optional.empty()), env);
		System.out.println("Resolved? + " + resolved);
		Type dslType = ((DSLLit)this.lit).typecheck(env, Optional.ofNullable(resolved));

		System.out.println("From lit: " + dslType);
		type = dslType;
		return dslType;
	}

	@Override
	public Value evaluate(Environment env) {
		System.out.println("Evaluated from kw: ");
		System.out.println(env);
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String, TypedAST> out = new HashMap<>();
		out.put("tgt", this.tgt);
		out.put("lit", this.lit);
		//System.out.println(":-) This is the kw!!! " + this.tgt + " -- " + id + " -- " + ((DSLLit)lit).getText());
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new KeywordInvocation(newChildren.get("tgt"), id, lit, fileLocation);
	}

	@Override
	public FileLocation getLocation() {
		return this.fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(tgt, id, lit);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);	
	}
}
