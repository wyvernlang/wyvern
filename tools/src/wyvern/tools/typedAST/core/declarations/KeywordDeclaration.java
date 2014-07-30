package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.compiler.MetadataInnerBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.MetadataWrapper;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWritable;
import wyvern.tools.util.TreeWriter;

public class KeywordDeclaration extends Declaration implements TreeWritable {

	// TODO: Implement white box keywords
	protected TypedAST body;
	private String name;
	private Type type;
	private FileLocation location = FileLocation.UNKNOWN;
	private final Reference<Optional<TypedAST>> kwMetadata;
	private final Reference<Value> kwMetadataObj;
	private MetadataWrapper metaType = null;
	
	public KeywordDeclaration(String name, Type type, TypedAST body, FileLocation location) {
		this.name = name;
		this.body = body;
		this.type = type;
		this.location = location;
		this.kwMetadata = new Reference<Optional<TypedAST>>(Optional.ofNullable(body));
		this.kwMetadataObj = new Reference<>();
	}
	
	public TypedAST getBody() {
		return body;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public Type getMetaType() {
		return metaType;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		if (body != null)
			childMap.put("body", body);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new KeywordDeclaration(name, type, newChildren.get(0), location);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(name, type, body);
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		// Do I need to build a "keyword environment"?
		return env;
	}

	Type resolvedType = null;
	@Override
	public Environment extendName(Environment env, Environment against) {
		// Same question: Do I need to extend keyword name here?
		if (resolvedType == null)
			resolvedType = TypeResolver.resolve(type, against);
		return env; //env.extend(new KeywordBinding());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	// black box keyword, we need to do a type checking
	// White box keyword to be implemented.
	protected Type doTypecheck(Environment env) {
		if (body != null) {
			type = TypeResolver.resolve(type, env);
			
			Type bodyType = body.typecheck(env, Optional.of(type)); // Can be null for def inside type!
			
			// Body should always be of HasPareser Type
			Type hasParserType = TypeResolver.resolve(new UnresolvedType("HasParser"), env);
			if (bodyType != null && !bodyType.subtype(hasParserType))
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, bodyType.toString(), type.toString());
		}
		return null;
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return extendName(old, against);
	}

	@Override
	public Environment extendWithValue(Environment old) {
		System.out.println(old);
		// Not sure if this kind of keyword is a value?
		// TODO: Change to keywordBinding
		Environment newEnv = old.extend(new ValueBinding(name, type));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		if (kwMetadataObj.get() == null)
			kwMetadataObj.set(kwMetadata.get().orElseGet(() -> new New(new DeclSequence(), FileLocation.UNKNOWN)).evaluate(evalEnv));
		metaType = new MetadataWrapper(type, kwMetadataObj);
	}
	
	public void evalMeta(Environment evalEnv) {
		Environment extMetaEnv = evalEnv
				.lookupBinding("metaEnv", MetadataInnerBinding.class)
				.map(MetadataInnerBinding::getInnerEnv).orElse(Environment.getEmptyEnvironment());

		Environment metaEnv = Globals.getStandardEnv().extend(TypeDeclaration.attrEvalEnv).extend(extMetaEnv);
		kwMetadata.get().map(obj->obj.typecheck(metaEnv, Optional.<Type>empty()));
		kwMetadataObj.set(kwMetadata.get().map(obj -> obj.evaluate(metaEnv)).orElse(new Obj(Environment.getEmptyEnvironment())));
	}
}
