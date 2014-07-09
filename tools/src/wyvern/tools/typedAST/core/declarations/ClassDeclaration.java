package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.binding.evaluation.LateValueBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.ClassBinding;
import wyvern.tools.typedAST.core.binding.objects.TypeDeclBinding;
import wyvern.tools.typedAST.core.binding.typechecking.LateTypeBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class ClassDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls = new DeclSequence(new LinkedList<Declaration>());
	private List<String> typeParams;
	// protected DeclSequence classDecls;
	
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private String implementsName;
	private String implementsClassName;
	
	private TypeBinding nameImplements;
	
	protected Environment declEvalEnv;

	private TypeType equivalentType = null;
	private TypeType equivalentClassType = null;
	private Reference<Environment> typeEquivalentEnvironmentRef;
	protected Reference<Environment> declEnvRef;

	private Reference<Environment> objEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Environment getObjEnvV() { return objEnv.get(); }
	protected void setObjEnv(Environment newEnv) { objEnv.set(newEnv); }

	private ClassType objType = new ClassType(objEnv, new Reference<>(), new LinkedList<>(), "");

	public ClassType getOType() {
		return new ClassType(objEnv, new Reference<>(), new LinkedList<>(), getName());
	}
	
	private TaggedInfo taggedInfo;
	
	public ClassDeclaration(String name,
							String implementsName,
							String implementsClassName,
							DeclSequence decls,
							Environment declEnv,
							List<String> typeParams,
							FileLocation location) {
        this(name, implementsName, implementsClassName, decls, typeParams, location);
		declEnvRef.set(declEnv);
    }

	public ClassDeclaration(String name,
			TaggedInfo taggedInfo,
			String implementsName,
			String implementsClassName,
			DeclSequence decls,
			Environment declEnv,
			List<String> typeParams,
			FileLocation location) {
		this(name, implementsName, implementsClassName, decls, typeParams, location);
		declEnvRef.set(declEnv);
		
		this.taggedInfo = taggedInfo;
    }
	
	public ClassDeclaration(String name,
			TaggedInfo taggedInfo,
			String implementsName,
			String implementsClassName,
			DeclSequence decls,
			FileLocation location) {
		this(name, implementsName, implementsClassName, decls, new LinkedList<String>(), location);
		
		this.taggedInfo = taggedInfo;
		
		//TODO: this will need to be replaced with proper type resolution for tags.
		this.taggedInfo.setTagName(name);
		this.taggedInfo.associateTag();
	}
	
	public ClassDeclaration(String name,
							String implementsName,
							String implementsClassName,
							DeclSequence decls,
							FileLocation location) {
		this(name, implementsName, implementsClassName, decls, new LinkedList<String>(), location);

	}
    public ClassDeclaration(String name,
							String implementsName,
							String implementsClassName,
							DeclSequence decls,
							List<String> typeParams,
							FileLocation location) {
		this.decls = decls;
		this.typeParams = typeParams;
		typeEquivalentEnvironmentRef = new Reference<>();
		declEnvRef = new Reference<>();
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, getObjType());
		nameBinding = new NameBindingImpl(name, getClassType());
		this.implementsName = implementsName;
		this.implementsClassName = implementsClassName;
		this.location = location;
	}

	protected ClassType getObjType() {
		return objType;
	}

	
	
	protected void updateEnv() {
		typeEquivalentEnvironmentRef.set(TypeDeclUtils.getTypeEquivalentEnvironment(getDecls(), false));
	}

	public TypeType getEquivalentType() {
		if (equivalentType == null)
			equivalentType = new TypeType(TypeDeclUtils.getTypeEquivalentEnvironment(getDecls(), false));
		return equivalentType;
	}

	protected Type getClassType() {
		return new ClassType(this);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//TODO: implement me
		writer.writeArgs(decls);
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
	public Type doTypecheck(Environment env) {
		// System.out.println("INSIDE doTYpecheck in CLass DECLAration");
		
		// System.out.println();
		// System.out.println(decls);
		// System.out.println();

		// FIXME: Currently allow this and class in both class and object methods. :(


		Environment genv = env.extend(new ClassBinding("class", this));
		Environment oenv = genv.extend(new NameBindingImpl("this", getObjectType()));



		if (decls != null) {
			if (this.typeEquivalentEnvironmentRef.get() == null)
				typeEquivalentEnvironmentRef.set(TypeDeclUtils.getTypeEquivalentEnvironment(decls,true));
			for (Declaration decl : decls.getDeclIterator()) {
				TypeBinding binding = new TypeBinding(nameBinding.getName(), getObjectType());
				if (decl.isClass()) {
					decl.typecheckSelf(genv.extend(binding));
				} else {
					decl.typecheckSelf(oenv.extend(binding));
				}
			}
		}
		
		// check the implements and class implements
		// FIXME: Should support multiple implements statements!
		if (!this.implementsName.equals("")) {
			this.nameImplements = env.lookupType(this.implementsName);
			if (nameImplements == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, this.implementsName);
			}
			
			// since there is a valid implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
            TypeType implementsTT = (TypeType)nameImplements.getType();
			
			if (!getEquivalentType().subtype(implementsTT)) {
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
						this,
                        this.nameBinding.getName(),
                        nameImplements.getName());
			}
		}
		
		if (!this.implementsClassName.equals("")) {
			NameBinding nameImplementsClass = env.lookup(this.implementsClassName);
			if (nameImplementsClass == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, this.implementsClassName);
			}

			// since there is a valid class implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
            TypeType implementsCT = (TypeType) (
					((ClassType)nameImplementsClass.getType())
							.getEnv()
							.lookupBinding("type", TypeDeclBinding.class)).get().getType();
			
			if (!getEquivalentClassType().subtype(implementsCT)) {
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
						this,
                        this.nameBinding.getName(),
                        nameImplementsClass.getName());
			}
		}
		
		// System.out.println("FINITA FINITA FINITA");

		return Unit.getInstance();
	}

	private Type getObjectType() {
		Environment declEnv = getObjEnv();
		Environment objTee = TypeDeclUtils.getTypeEquivalentEnvironment(declEnv);
		return new ClassType(objEnv, new Reference<Environment>(objTee) {
			@Override
			public Environment get() {
				return TypeDeclUtils.getTypeEquivalentEnvironment(objEnv.get());
			}

			@Override
			public void set(Environment e) {
				throw new RuntimeException();
			}
		}, new LinkedList<>(), this.getName());
	}
	
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		
		// FIXME: Currently allow this and class in both class and object methods. :(
		//newEnv = newEnv.extend(new TypeBinding("class", typeBinding.getType()));
		//newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType()));
		
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
		if (declEvalEnv == null)
			declEvalEnv = declEnv.extend(evalEnv);
		Obj classObj = new Obj(getClassEnv(evalEnv));
		
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(classObj);
		
		
		TaggedInfo info = getTaggedInfo();
		
		if (info != null) { // && info.getCaseOfTag() != null && info.getCaseOfTag().contains(".")) {
			//evaluating a dynamic tag
			
			//extract the two parts of dynamic tag name
			// String varName = info.getCaseOfTag().split(Pattern.quote("."))[0];
			// String tagName = info.getCaseOfTag().split(Pattern.quote("."))[1];
			
			//get the variable that the dynamic tag is created over
			// Value creationVar = evalEnv.getValue(varName);
			
			//create the dynamic tag
			TagBinding.printDebugState();
			
			TaggedInfo dynamicTag = TagBinding.createDynamicTag(info); // .getTagName(), info, creationVar);

			TagBinding.printDebugState();

			// System.out.println("create dynamic tag: " + String.format("%x", dynamicTag.hashCode()) + ", from var: " + varName + String.format("%x", creationVar.hashCode()));
			
			//System.out.println("evaluate decl: value1: " + varName + " used to create dynamic tag with val: " + String.format("%x", creationVar.hashCode()));
		}
		
	}
	
	public Environment evaluateDeclarations(Environment addtlEnv) {
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
		decls.bindDecls(declEvalEnv.extend(addtlEnv), thisEnv);
		
		return thisEnv;
	}
	
	public Environment getClassEnv(Environment extEvalEnv) {
		
		Environment classEnv = Environment.getEmptyEnvironment();

		if (decls == null)
			return classEnv;

		for (Declaration decl : decls.getDeclIterator()) {
			if (decl.isClass()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
		}
		
		ClassBinding thisBinding = new ClassBinding("class", this);
		Environment evalEnv = classEnv.extend(thisBinding);
		
		for (Declaration decl : decls.getDeclIterator())
			if (decl.isClass()){
				decl.bindDecl(extEvalEnv.extend(evalEnv),classEnv);
			}
		
		classEnv = classEnv.extend(new ClassBinding("claasdasdass", this));
		
		return classEnv;
	}

	public Environment getObjEnv() {
		return objEnv.get();
	}
	
	public DeclSequence getDecls() {
		return decls;
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	/**
	 * Returns if this class is tagged or not.
	 * 
	 * @return true if tagged, false otherwise
	 */
	public boolean isTagged() {
		return taggedInfo != null;
	}
	
	/**
	 * Returns the tag information associated with this class. 
	 * If this class isn't tagged this information will be null.
	 * 
	 * @return the tag info
	 */
	public TaggedInfo getTaggedInfo() {
		return taggedInfo;
	}
	
	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}

	public Environment getDeclEnv() {
		return declEnvRef.get();
	}

	public Reference<Environment> getTypeEquivalentEnvironmentReference() {
		return typeEquivalentEnvironmentRef;
	}

	public Reference<Environment> getDeclEnvRef() {
		return declEnvRef;
	}

	public Type getEquivalentClassType() {
		if (equivalentClassType == null) {
            List<Declaration> declsi = new LinkedList<>();
            for (Declaration d : decls.getDeclIterator()) {
                if (d.isClass())
                    declsi.add(d);
                if (d.isClass())
                    declsi.add(d);
            }
			equivalentClassType = new TypeType(TypeDeclUtils.getTypeEquivalentEnvironment(new DeclSequence(declsi), true));
        }
		return equivalentClassType;
	}

	public Environment getFilledBody(AtomicReference<Value> objRef) {
		return evaluateDeclarations(
				Environment
						.getEmptyEnvironment()
						.extend(new LateValueBinding("this", objRef, getType())));
	}


	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		int i = 0;
		for (TypedAST ast : decls) {
			children.put(i++ + "decl", ast);
		}
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		List<Declaration> decls = new ArrayList<Declaration>(nc.size());
		Iterable<String> keys = nc.keySet().stream().filter(key->key.endsWith("decl"))
				.map(key->new Pair<String,Integer>(key, Integer.parseInt(key.substring(0,key.length() - 4))))
				.<Pair<String,Integer>>sorted((a,b)->a.second-b.second)
				.map(pair->pair.first)::iterator;
		for (String key : keys) {
			if (!key.endsWith("decl"))
				continue;
			int idx = Integer.parseInt(key.substring(0,key.length() - 4));
			decls.add(idx, (Declaration)nc.get(key));
		}
		return new ClassDeclaration(nameBinding.getName(), implementsName, implementsClassName,
				new DeclSequence(decls), declEnvRef.get(), typeParams, location);
	}


	public List<String> getTypeParams() {
		return typeParams;
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		return env.extend(typeBinding);
	}

	boolean envGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		TypeBinding objBinding = new LateTypeBinding(nameBinding.getName(), this::getObjectType);

		if (!envGuard && decls != null) {
			declEnvRef.set(Environment.getEmptyEnvironment());
			for (Declaration decl : decls.getDeclIterator()) {
				if (decl.isClass())
					declEnvRef.set(decl.extendName(declEnvRef.get(), against.extend(objBinding)));
				else
					objEnv.set(decl.extendName(objEnv.get(), against.extend(objBinding)));
			}
			envGuard = true;
		}
		return env;
	}
}
