package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.binding.compiler.MetadataInnerBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.TypeDeclBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class TypeDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Environment declEvalEnv;
	protected Reference<Obj> attrObj = new Reference<>();
    protected Reference<Environment> declEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Reference<Environment> attrEnv = new Reference<>(Environment.getEmptyEnvironment());
	
	private TaggedInfo taggedInfo;
	
	public static Environment attrEvalEnv = Environment.getEmptyEnvironment(); // HACK
	private Reference<Value> metaValue = new Reference<>();

	public Reference<Obj> getAttrObjRef() {
		return attrObj;
	}

	public Environment getAttrEnv() {
		return attrEnv.get();
	}

	public Reference<Value> getMetaValue() {
		return metaValue;
	}

	// FIXME: I am not convinced typeGuard is required (alex).
	private boolean typeGuard = false;
	@Override
	public Environment extendType(Environment env, Environment against) {
		if (!typeGuard) {
			env = env.extend(typeBinding);
			for (Declaration decl : decls.getDeclIterator()) {
				if (decl instanceof EnvironmentExtender) {
					env = ((EnvironmentExtender) decl).extendType(env, against);
				}
			}
			typeGuard = true;
		}
		return env;
	}

	private boolean declGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		// System.out.println("Running extendName for TypeDeclaration: " + this.getName() + " with " + this.getDeclEnv());
		
		if (!declGuard) {
			for (Declaration decl : decls.getDeclIterator()) {
				
				// System.out.println("Processing inside " + this.getName() + " member " + decl.getName() + " and decl.getClass " + decl.getClass());
				
				declEnv.set(decl.extendName(declEnv.get(), against));
				if (decl instanceof AttributeDeclaration) {
					env = env.extend(new NameBindingImpl(getName(), decl.getType()));
					nameBinding = new NameBindingImpl(nameBinding.getName(), decl.getType());
				}
			}
			declGuard = true;
		}

		// System.out.println("Finished running extendName for TypeDeclaration: " + this.getName() + " with " + this.getDeclEnv());
		
		return env.extend(new NameBindingImpl(this.getName(), this.getType()));
	}

	public static class AttributeDeclaration extends Declaration {
		private final TypedAST body;
		private Type rType;

		public AttributeDeclaration(TypedAST body) {
			this.body = body;
		}
		public AttributeDeclaration(TypedAST body, Type result) {
			this.body = body;
			this.rType = result;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		protected Type doTypecheck(Environment env) {
			Type result = body.typecheck(env, Optional.ofNullable(rType));
			if (!result.subtype(rType))
				throw new RuntimeException("Invalid type for metadata");
			return result;
		}

		@Override
		protected Environment doExtend(Environment old, Environment against) {
			return old;
		}

		@Override
		public Environment extendWithValue(Environment old) {
			return old;
		}

		@Override
		public void evalDecl(Environment evalEnv, Environment declEnv) {
		}

		@Override
		public Type getType() {
			return rType;
		}

		@Override
		public Map<String, TypedAST> getChildren() {
			Map<String, TypedAST> childMap = new HashMap<>();
			childMap.put("body", body);
			return childMap;
		}

		@Override
		public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
			return new AttributeDeclaration(newChildren.get("body"), rType);
		}

		@Override
		public FileLocation getLocation() {
			return null;
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {
		}

		public TypedAST getBody() {
			return body;
		}

		@Override
		public Environment extendType(Environment env, Environment against) {
			return env;
		}

		@Override
		public Environment extendName(Environment env, Environment against) {
			rType = TypeResolver.resolve(rType, against);
			return env;
		}
	}

    public TypeDeclaration(String name, DeclSequence decls, TaggedInfo taggedInfo, FileLocation clsNameLine) {
    	this(name, decls, clsNameLine);
    	
    	this.taggedInfo = taggedInfo;
		this.taggedInfo.setTagName(name);
		this.taggedInfo.associateTag();
	}
	
    public TypeDeclaration(String name, DeclSequence decls, FileLocation clsNameLine) {
    	// System.out.println("Initialising TypeDeclaration ( " + name + "): decls" + decls);
    	
		this.decls = decls;
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, null);
		Type objectType = new TypeType(this);
		attrEnv.set(attrEnv.get().extend(new TypeDeclBinding("type", this)));
		
		Type classType = new ClassType(attrEnv, attrEnv, new LinkedList<String>(), getName()); // TODO set this to a class type that has the class members
		nameBinding = new NameBindingImpl(nameBinding.getName(), classType);

		typeBinding = new TypeBinding(nameBinding.getName(), objectType);
		
		// System.out.println("TypeDeclaration: " + nameBinding.getName() + " is now bound to type: " + objectType);
		
		this.location = clsNameLine;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//TODO: implement me
		//writer.writeArgs(definition);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Type getType() {
		return this.typeBinding.getType();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("decls", decls);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TypeDeclaration(nameBinding.getName(), (DeclSequence)newChildren.get("decls"), location);
	}

	@Override
	public Type doTypecheck(Environment env) {
		// env = env.extend(new NameBindingImpl("this", nameBinding.getType()));
		Environment eenv = decls.extend(env, env);
		
		// System.out.println("Doing doTypecheck for Type: " + this.getName());
		
		for (Declaration decl : decls.getDeclIterator()) {
			decl.typecheckSelf(eenv);
		}
		evalMeta(Globals.getStandardEnv().extend(env.lookupBinding("metaEnv", MetadataInnerBinding.class).orElse(new MetadataInnerBinding())));

		return this.typeBinding.getType();
	}	
	
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		// newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType())); // Why is there "this" in a type (not class)?

		//extend with tag information
		if (isTagged()) {
			//type-test the tag information
			
			//first get/ create the binding
			TagBinding tagBinding = TagBinding.getOrCreate(taggedInfo.getTagName());
			newEnv = newEnv.extend(tagBinding);
			
			//now handle case-of and comprises clauses
			if (taggedInfo.getCaseOfTag() != null) {
				String caseOf = taggedInfo.getCaseOfTag();
				
				//TODO: could case-of come before?
				Optional<TagBinding> caseOfBindingO = Optional.ofNullable(TagBinding.getOrCreate(caseOf));
				//TODO, change to real code: newEnv.lookupBinding(caseOf, TagBinding.class);
				
				if (caseOfBindingO.isPresent()) {
					 TagBinding caseOfBinding = caseOfBindingO.get();
					 
					 //set up relationship between two bindings
					 tagBinding.setCaseOfParent(caseOfBinding);
					 caseOfBinding.addCaseOfDirectChild(tagBinding);
				} else {
					ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, caseOf);
				}
			}
			
			if (!taggedInfo.getComprisesTags().isEmpty()) {
				//set up comprises tags
				for (String s : taggedInfo.getComprisesTags()) {
					// Because comprises refers to tags defined ahead of this, we use the associated tag values
					
					Optional<TagBinding> comprisesBindingO = Optional.of(TagBinding.getOrCreate(s));
					//TODO, change to real code: newEnv.lookupBinding(s, TagBinding.class);
					
					if (comprisesBindingO.isPresent()) {
						TagBinding comprisesBinding = comprisesBindingO.get();
						
						tagBinding.getComprisesOf().add(comprisesBinding);
					} else {
						//TODO throw proper error
						ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, s);
					}
				}
			}
		}
		
		return newEnv;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		declEvalEnv = declEnv;
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());

		Environment attrEnv = Environment.getEmptyEnvironment();
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		evalMeta(evalEnv);
		vb.setValue(metaValue.get());
	}

	private void evalMeta(Environment evalEnv) {
		for (Declaration decl : decls.getDeclIterator()) {
			if (decl instanceof AttributeDeclaration) {
				metaValue.set(((AttributeDeclaration) decl).getBody().evaluate( evalEnv.extend(attrEvalEnv.extend(
						evalEnv.lookupBinding("metaEnv", MetadataInnerBinding.class).map(mb -> mb.getInnerEnv()).orElse(Environment.getEmptyEnvironment())))));
			}
		}
	}

	public DeclSequence getDecls() {
		return decls;
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	/**
	 * Returns true if this Type Declaration is tagged.
	 * 
	 * 
	 * @return
	 */
	public boolean isTagged() {
		return taggedInfo != null;
	}
	
	/**
	 * Gets the Tag info for this TypeDeclaration.
	 * 
	 * @return
	 */
	public TaggedInfo getTaggedInfo() {
		return taggedInfo;
	}
	
	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

    public NameBinding lookupDecl(String name) {
        return declEnv.get().lookup(name);
    }


	public Reference<Environment> getDeclEnv() {
		return declEnv;
	}
}