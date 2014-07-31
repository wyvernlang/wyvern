package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.MetadataWrapper;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeywordInvocation implements CoreAST {
	private final TypedAST tgt;
	private final String keyword;
	private final DSLLit lit;
	private FileLocation fileLocation = FileLocation.UNKNOWN;
	private Type type = null;

	public KeywordInvocation(TypedAST l, String id, DSLLit lit, FileLocation fileLocation) {
		this.tgt = l;
		this.keyword = id;
		this.lit = lit;
		this.fileLocation = fileLocation;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type tgtType = TypeResolver.resolve(((Variable)this.tgt).typecheck(env, Optional.empty()), env);
		
		Type metaWrapper = null;
		try {
			System.err.println("KWKWKWKW " + env);
			System.err.println("OOOOOOOO " + env.lookup("Hellolang").getType().getClass());
			metaWrapper = TypeResolver.generateKeywordWrapper(tgtType, env, keyword);
		} catch (Exception e) {
			System.err.println("No keyword available");
		}

		System.out.println(env);
		
		this.type = ((DSLLit)this.lit).typecheck(env,Optional.of(metaWrapper));

		return this.type;
	}

	@Override
	public Value evaluate(Environment env) {
		System.out.println("Evaluated from kw: ");
		System.out.println("tgt type: " + ((TypeType)((MetadataWrapper)tgt.getType()).getInner()).getDecl().getDecls());
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String, TypedAST> out = new HashMap<>();
		out.put("tgt", this.tgt);
		out.put("lit", this.lit);
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new KeywordInvocation(newChildren.get("tgt"), keyword, lit, fileLocation);
	}

	@Override
	public FileLocation getLocation() {
		return this.fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(tgt, keyword, lit);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);	
	}
}
