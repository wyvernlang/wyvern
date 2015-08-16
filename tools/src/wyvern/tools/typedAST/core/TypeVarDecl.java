package wyvern.tools.typedAST.core;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.compiler.MetadataInnerBinding;
import wyvern.tools.typedAST.core.binding.typechecking.LateNameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeVarDecl extends Declaration {
	private final String name;
	private final EnvironmentExtender body;
	private final FileLocation fileLocation;
	private final Reference<Optional<TypedAST>> metadata;
	private final Reference<Value> metadataObj;
	private TaggedInfo taggedInfo = null;
	private boolean resourceFlag = false;

	/**
	 * Helper class to allow easy variation of bound types
	 */
	private abstract class EnvironmentExtInner implements EnvironmentExtender {

		private final FileLocation loc;

		public EnvironmentExtInner(FileLocation loc) {
			this.loc = loc;
		}

		@Override
		public Environment extendName(Environment env, Environment against) {
			return env;
		}

		@Override
		public Environment extend(Environment env, Environment against) {
			return env;
		}

		@Override
		public EvaluationEnvironment evalDecl(EvaluationEnvironment env) {
			return env;
		}
		@Override
		public Type typecheck(Environment env, Optional<Type> expected) {
			return getType();
		}

		@Override
		public Value evaluate(EvaluationEnvironment env) {
			return UnitVal.getInstance(loc);
		}

		@Override
		public Map<String, TypedAST> getChildren() {
			return new HashMap<>();
		}

		@Override
		public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
			return this;
		}

		@Override
		public FileLocation getLocation() {
			return loc;
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {

		}

        @Override
        public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
            throw new WyvernException("Cannot generate code for a placeholder", TypeVarDecl.this);
        }

	}


	public TypeVarDecl(String name, DeclSequence body, TypedAST metadata, FileLocation fileLocation) {
		this.metadata = new Reference<Optional<TypedAST>>(Optional.ofNullable(metadata));
		this.name = name;
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
		this.body = new TypeDeclaration(name, body, this.metadataObj, fileLocation);
		this.fileLocation = fileLocation;
	}

	public TypeVarDecl(String name, DeclSequence body, TaggedInfo taggedInfo, TypedAST metadata, FileLocation fileLocation, boolean isResource ){
		this.metadata = new Reference<Optional<TypedAST>>(Optional.ofNullable(metadata));
		this.name = name;
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
		this.body = new TypeDeclaration(name, body, this.metadataObj, taggedInfo, fileLocation);
		this.fileLocation = fileLocation;
		this.taggedInfo = taggedInfo;
		this.resourceFlag = isResource;
	}

	public TypeVarDecl(String name, DeclSequence body, TaggedInfo taggedInfo, TypedAST metadata, FileLocation fileLocation){
		this.metadata = new Reference<Optional<TypedAST>>(Optional.ofNullable(metadata));
		this.name = name;
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
		this.body = new TypeDeclaration(name, body, this.metadataObj, taggedInfo, fileLocation);
		this.fileLocation = fileLocation;
	}
	
	private TypeVarDecl(String name, EnvironmentExtender body, Reference<Optional<TypedAST>> metadata, Reference<Value> metadataObj, FileLocation location) {
		this.name = name;
		this.body = body;
		this.metadata = metadata;
		this.metadataObj = metadataObj;
		fileLocation = location;
	}

	public TypeVarDecl(String name, Type body, TypedAST metadata, FileLocation fileLocation) {
		this.name = name;
		this.body = new EnvironmentExtInner(fileLocation) {
			@Override
			public Environment extendType(Environment env, Environment against) {
				Type type = TypeResolver.resolve(body, against);
				return env.extend(new TypeBinding(name, type, metadataObj))
						.extend(new LateNameBinding(name, () -> metadataObj.get().getType()));
			}



			@Override
			public Type getType() {
				return body;
			}



			@Override
			public Expression generateIL(GenContext ctx) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		this.fileLocation = fileLocation;
		this.metadata = new Reference<>(Optional.ofNullable(metadata));
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
	}

	public TypeVarDecl(String name, EnvironmentExtender body, FileLocation fileLocation) {
		this.body = body;
		this.name = name;
		this.fileLocation = fileLocation;
		metadata = new Reference<Optional<TypedAST>>(Optional.empty());
		this.metadataObj = new Reference<>(new Obj(EvaluationEnvironment.EMPTY, null));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		evalMeta(env);
		return body.typecheck(env, Optional.<Type>empty());
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return body.extend(old, against);
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		return body.evalDecl(old);
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		body.evalDecl(declEnv);
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		return body.extendType(env, against);
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return body.extendName(env, against);
	}

	private void evalMeta(Environment evalEnv) {
		MetadataInnerBinding extMetaEnv = evalEnv
				.lookupBinding("metaEnv", MetadataInnerBinding.class).orElseGet(() -> MetadataInnerBinding.EMPTY);

		Environment metaTcEnv = Globals.getStandardEnv().extend(extMetaEnv.getInnerEnv());
		EvaluationEnvironment metaEnv = Globals.getStandardEvalEnv().extend(TypeDeclaration.attrEvalEnv).extend(extMetaEnv.getInnerEvalEnv());
		metadata.get().map(obj->obj.typecheck(metaTcEnv, Optional.<Type>empty()));

		metadataObj.set(metadata.get().map(obj -> obj.evaluate(metaEnv)).orElse(new Obj(EvaluationEnvironment.EMPTY, null)));
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String,TypedAST> out = new HashMap<>();
		out.put("body", body);
		if (metadata.get().isPresent())
			out.put("metadata", metadata.get().get());
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		metadata.set(Optional.ofNullable(newChildren.get("metadata")));
		return new TypeVarDecl(name, (EnvironmentExtender)newChildren.get("body"), metadata, metadataObj, fileLocation);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        writer.write(ExpressionWriter.generate(ow -> new wyvern.target.corewyvernIL.decl.TypeDeclaration(name, body.getType().generateILType()))); // TODO better tag support
    }

    @Override
	public FileLocation getLocation() {
		return fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isResource() {
		return this.resourceFlag;
	}
}
