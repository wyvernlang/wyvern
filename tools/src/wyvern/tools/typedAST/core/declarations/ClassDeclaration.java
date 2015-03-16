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
import wyvern.tools.types.extensions.TypeInv;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClassDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls = new DeclSequence(new LinkedList<Declaration>());
	private List<String> typeParams;
	// protected DeclSequence classDecls;
	
	private NameBinding nameBinding;
	protected TypeBinding typeBinding;
	
	private String implementsName;
	private String implementsClassName;
	
	private TypeBinding nameImplements;
	
	protected Environment declEvalEnv;

	private TypeType equivalentType = null;
	private TypeType equivalentClassType = null;
	private Reference<Environment> typeEquivalentEnvironmentRef;
	protected Reference<Environment> classMembersEnv;

	private Reference<Environment> instanceMembersEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Environment getObjEnvV() { return instanceMembersEnv.get(); }
	protected void setInstanceMembersEnv(Environment newEnv) { instanceMembersEnv.set(newEnv); }

	private ClassType objType = new ClassType(instanceMembersEnv, new Reference<>(), new LinkedList<>(), "");

	public ClassType getOType() {
		return new ClassType(instanceMembersEnv, new Reference<>(), new LinkedList<>(), getName());
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
		classMembersEnv.set(declEnv);
    }

	public ClassDeclaration(String name,
			TaggedInfo taggedInfo,
			String implementsName,
			String implementsClassName,
			DeclSequence decls,
			FileLocation location) {
		this(name, implementsName, implementsClassName, decls, new LinkedList<String>(), location);
		
		// System.out.println("Creating class declaration for: " + name + " with decls " + decls);
		
		this.taggedInfo = taggedInfo;
		this.taggedInfo.setTagName(name);
		this.taggedInfo.associateWithClassOrType(this.typeBinding);
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
		
    	//System.out.println("Made class: " + name);
    	
    	this.decls = decls;
		this.typeParams = typeParams;
		typeEquivalentEnvironmentRef = new Reference<>();
		classMembersEnv = new Reference<>();
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
		// FIXME: Currently allow this and class in both class and object methods. :(

		Environment genv = env.extend(new ClassBinding("class", this));
		Environment oenv = genv.extend(new NameBindingImpl("this", getObjectType()));

		if (decls != null) {
			if (this.typeEquivalentEnvironmentRef.get() == null)
				typeEquivalentEnvironmentRef.set(TypeDeclUtils.getTypeEquivalentEnvironment(decls,true));
			for (Declaration decl : decls.getDeclIterator()) {
				TypeBinding binding = new TypeBinding(nameBinding.getName(), getObjectType());
				if (decl.isClassMember()) {
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
		
		if (isTagged()) typecheckTags(env);

		return Unit.getInstance();
	}
	
	private void typecheckTags(Environment env) {
		taggedInfo.resolve(env, this);
		
		// System.out.println("CURRENT ti = " + taggedInfo);
		
		Type myTagType = taggedInfo.getTagType();
		
		if (taggedInfo.hasCaseOf()) {
			Type caseOfType = taggedInfo.getCaseOfTag();
			
			// System.out.println("caseOfType = " + caseOfType);
			// System.out.println("TaggedInfo Global Store Current State = " + TaggedInfo.getGlobalTagStore());
			
			//check the type is tagged
			if (!(caseOfType instanceof TypeInv)) { // If it is TypeInv - we won't know till runtime! 
				TaggedInfo info = TaggedInfo.lookupTagByType(caseOfType);
				//System.out.println("Looked up: " + info);
				
				if (info == null) {
					ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, this, caseOfType.toString());
				}
				
				//now check circular relationship has not been created
				if (taggedInfo.isCircular()) {
					ToolError.reportError(ErrorMessage.CIRCULAR_TAGGED_RELATION, this, taggedInfo.getTagName(), caseOfType.toString());
				}
			}
		}
		
		if (taggedInfo.hasComprises()) {
			List<Type> comprisesTags = taggedInfo.getComprisesTags();
			
			//first check that every comprises tag actually is a case-of of this
			for (Type s : comprisesTags) {				
				TaggedInfo info = TaggedInfo.lookupTagByType(s); // FIXME:
				
				//check it exists
				if (info == null) {
					ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, this, s.toString());
				}
				
				//then check it is a case-of
				Type comprisesCaseOfName = info.getCaseOfTag();
				if (!myTagType.equals(comprisesCaseOfName)) {
					ToolError.reportError(ErrorMessage.COMPRISES_RELATION_NOT_RECIPROCATED, this);
				}
			}

			//now check every other case-of does not case-of this tag
			for (TaggedInfo info : TaggedInfo.getGlobalTagStoreList()) {
				Type othersType = info.getTagType();
				Type caseOf = info.getCaseOfTag();
				
				//if tag is ourselves, or one of our comprises, skip it
				if (othersType.equals(myTagType) || comprisesTags.contains(othersType)) {
					continue;
				}
				
				//now if the other tag 'case-of's is this, it is an error
				if (myTagType.equals(caseOf)) {
					ToolError.reportError(ErrorMessage.COMPRISES_EXCLUDES_TAG, this, myTagType.toString(), info.getTagType().toString());
				}
			}
		}
	}

	private Type getObjectType() {
		Environment declEnv = getInstanceMembersEnv();
		Environment objTee = TypeDeclUtils.getTypeEquivalentEnvironment(declEnv);
		return new ClassType(instanceMembersEnv, new Reference<Environment>(objTee) {
			@Override
			public Environment get() {
				return TypeDeclUtils.getTypeEquivalentEnvironment(instanceMembersEnv.get());
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
		
		return newEnv;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		
		//newEnv = newEnv.extend(taggedBinding);
		
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		if (declEvalEnv == null)
			declEvalEnv = declEnv.extend(evalEnv);
		Obj classObj = new Obj(getClassEnv(evalEnv));
		
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(classObj);
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
			if (decl.isClassMember()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
		}
		
		ClassBinding thisBinding = new ClassBinding("class", this);
		Environment evalEnv = classEnv.extend(thisBinding);
		
		for (Declaration decl : decls.getDeclIterator())
			if (decl.isClassMember()){
				decl.bindDecl(extEvalEnv.extend(evalEnv),classEnv);
			}
		
		classEnv = classEnv.extend(new ClassBinding("claasdasdass", this));
		
		return classEnv;
	}

	public Environment getInstanceMembersEnv() {
		return instanceMembersEnv.get();
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
		return classMembersEnv.get();
	}

	public Reference<Environment> getTypeEquivalentEnvironmentReference() {
		return typeEquivalentEnvironmentRef;
	}

	public Reference<Environment> getClassMembersEnv() {
		return classMembersEnv;
	}

	public Type getEquivalentClassType() {
		if (equivalentClassType == null) {
            List<Declaration> declsi = new LinkedList<>();
            for (Declaration d : decls.getDeclIterator()) {
                if (d.isClassMember())
                    declsi.add(d);
                if (d.isClassMember())
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
				new DeclSequence(decls), classMembersEnv.get(), typeParams, location);
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
			classMembersEnv.set(Environment.getEmptyEnvironment());
			for (Declaration decl : decls.getDeclIterator()) {
				instanceMembersEnv.set(decl.extendType(instanceMembersEnv.get(), against.extend(objBinding)));
				if (decl.isClassMember())
					classMembersEnv.set(decl.extendName(classMembersEnv.get(), against.extend(objBinding)));
				else
					instanceMembersEnv.set(decl.extendName(instanceMembersEnv.get(), against.extend(objBinding)));
			}
			envGuard = true;
		}
		return env.extend(nameBinding);
	}
}
