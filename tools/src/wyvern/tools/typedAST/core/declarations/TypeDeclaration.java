package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.expressions.New;
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
import java.util.function.Supplier;

public class TypeDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	private Reference<Optional<TypedAST>> metadata;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Environment declEvalEnv;
    protected Reference<Environment> declEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Reference<Environment> attrEnv = new Reference<>(Environment.getEmptyEnvironment());
	
	private TaggedInfo taggedInfo;
	
	public static Environment attrEvalEnv = Environment.getEmptyEnvironment(); // HACK
	private Reference<Value> metaValue = new Reference<>();

	// FIXME: I am not convinced typeGuard is required (alex).
	private boolean typeGuard = false;
	@Override
	public Environment extendType(Environment env, Environment against) {
		if (!typeGuard) {
			env = env.extend(typeBinding);
			declEnv.set(decls.extendType(declEnv.get(), against));
			typeGuard = true;
		}
		return env.extend(typeBinding);
	}

	private boolean declGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		if (!declGuard) {
			nameBinding = new NameBindingImpl(getName(), metadata.map(md->md.map(mdi->mdi.typecheck(against, Optional.<Type>empty()))).get().orElse(new ClassType()));
			declEnv.set(decls.extendName(declEnv.get(), against.extend(typeBinding)));
			declGuard = true;
		}

		return env.extend(nameBinding);
	}
    public TypeDeclaration(String name, DeclSequence decls, Reference<Optional<TypedAST>> metadata, TaggedInfo taggedInfo, FileLocation clsNameLine) {
    	this(name, decls, metadata, clsNameLine);
    	
    	this.taggedInfo = taggedInfo;
		this.taggedInfo.setTagName(name);
		this.taggedInfo.associateTag();
	}
	
    public TypeDeclaration(String name, DeclSequence decls, Reference<Optional<TypedAST>> metadata, FileLocation clsNameLine) {
    	// System.out.println("Initialising TypeDeclaration ( " + name + "): decls" + decls);
    	Supplier<TypedAST> metaOrElse = () -> new New(new DeclSequence(), clsNameLine);
		this.decls = decls;
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, null, metadata.map(mdi->mdi.orElseGet(metaOrElse)));
		Type objectType = new TypeType(this);
		attrEnv.set(attrEnv.get().extend(new TypeDeclBinding("type", this)));
		
		Type classType = new ClassType(attrEnv, attrEnv, new LinkedList<String>(), getName()); // TODO set this to a class type that has the class members
		nameBinding = new NameBindingImpl(nameBinding.getName(), classType);

		typeBinding = new TypeBinding(nameBinding.getName(), objectType, metadata.map(mdi->mdi.orElseGet(metaOrElse)));
		
		// System.out.println("TypeDeclaration: " + nameBinding.getName() + " is now bound to type: " + objectType);
		
		this.location = clsNameLine;
		this.metadata = metadata;
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
		return new TypeDeclaration(nameBinding.getName(), (DeclSequence)newChildren.get("decls"), metadata, location);
	}

	@Override
	public Type doTypecheck(Environment env) {
		Environment eenv = decls.extend(env, env);

		
		for (Declaration decl : decls.getDeclIterator()) {
			decl.typecheckSelf(eenv);
		}

		return this.typeBinding.getType();
	}	
	
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		// newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType())); // Why is there "this" in a type (not class)?
		
		//extend with tag information
		if (isTagged()) {
			//type-test the tag information
			
			//TODO: fix this
			
			//first get/ create the binding
			TagBinding tagBinding = TagBinding.getOrCreate(taggedInfo.getTagName());
			newEnv = newEnv.extend(tagBinding);
			
			//now handle case-of and comprises clauses
			if (taggedInfo.getCaseOfTag() != null) {
				String caseOf = taggedInfo.getCaseOfTag();
				
				//TODO: could case-of come before?
				Optional<TagBinding> caseOfBindingO = Optional.ofNullable(TagBinding.get(caseOf));
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
		if (metaValue.get() == null)
			metaValue.set(metadata.get().orElseGet(() -> new New(new DeclSequence(), FileLocation.UNKNOWN)).evaluate(evalEnv));
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(metaValue.get());
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
	 * @return
	 */
	public boolean isTagged() {
		return taggedInfo != null;
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