package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.binding.evaluation.HackForArtifactTaggedInfoBinding;
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
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.types.extensions.TypeInv;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClassDeclaration extends AbstractTypeDeclaration implements CoreAST {
	protected DeclSequence decls = new DeclSequence(new LinkedList<Declaration>());
	private List<String> typeParams;
	// protected DeclSequence classDecls;

	private NameBinding nameBinding;
	protected TypeBinding typeBinding;

	private String implementsName;
	private String implementsClassName;

	private TypeBinding nameImplements;

	protected EvaluationEnvironment declEvalEnv;

	private TypeType equivalentType = null;
	private TypeType equivalentClassType = null;
	private Reference<Environment> typeEquivalentEnvironmentRef;
	protected Reference<Environment> classMembersEnv;

	private Reference<Environment> instanceMembersEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Environment getObjEnvV() { return instanceMembersEnv.get(); }
	protected void setInstanceMembersEnv(Environment newEnv) { instanceMembersEnv.set(newEnv); }

	private ClassType objType;

	public ClassType getOType() {
		return objType;
	}

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

		objType = new ClassType(instanceMembersEnv, new Reference<>(), new LinkedList<>(), taggedInfo, "");
		typeBinding = new TypeBinding(name, getObjType());
		setupTags(name, typeBinding, taggedInfo);
		nameBinding = new NameBindingImpl(name, getClassType());
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
		objType = new ClassType(instanceMembersEnv, new Reference<>(), new LinkedList<>(), null, "");
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
		//TODO: implement me`
		writer.writeArgs(decls); // FIXME: This can be recursive! Sometimes crashes with StackOverflow!!!
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

		return new Unit();
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
		}, new LinkedList<>(), getTaggedInfo(), this.getName());
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);

		return newEnv;
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		EvaluationEnvironment newEnv = old
				.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()))
				.extend(new HackForArtifactTaggedInfoBinding(nameBinding.getName()));

		//newEnv = newEnv.extend(taggedBinding);

		return newEnv;
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {

		// System.out.println("Inside evalDecl for something called: " + this.getName());
		TaggedInfo goodTI = this.getTaggedInfo();
		if (goodTI != null) {
			// FIXME: This is a right place to resolve the TaggedInfo case of for this tag for this class if it happens to use variables.
			if (goodTI.hasCaseOf()) {
				Type co = goodTI.getCaseOfTag();
				if (co instanceof TypeInv) {
					TypeInv ti = (TypeInv) co;
					Type ttti = ti.getInnerType();
					String mbr = ti.getInvName();
					if (ttti instanceof UnresolvedType) {
						Value objVal = evalEnv.lookup(((UnresolvedType) ttti).getName()).get().getValue(evalEnv);
						TaggedInfo caseTag = ((Obj) objVal).getIntEnv().lookupBinding(mbr, HackForArtifactTaggedInfoBinding.class)
								.map(b->b.getTaggedInfo()).orElseThrow(() -> new RuntimeException("Invalid tag invocation"));

						// FIX THIS TAG:
						goodTI = new TaggedInfo(caseTag, new ArrayList<TaggedInfo>());
					}
				}
			}
		}

		if (declEvalEnv == null)
			declEvalEnv = declEnv.extend(evalEnv);
		if (goodTI != null) {
			HackForArtifactTaggedInfoBinding hfatib = new HackForArtifactTaggedInfoBinding("this");
			hfatib.setTaggedInfo(goodTI);
			evalEnv = evalEnv.extend(hfatib);
		}
		Obj classObj = new Obj(getClassEnv(evalEnv), null); // FIXME: can be tagged too you know, not goodTI!! :)

		final TaggedInfo finalTi = goodTI;
		declEnv.lookupBinding(nameBinding.getName(), HackForArtifactTaggedInfoBinding.class).ifPresent(b->b.setTaggedInfo(finalTi));

		ValueBinding vb = declEnv.lookup(nameBinding.getName())
				.orElseThrow(() -> new RuntimeException("Internal error - Class NameBinding not initalized"));
		vb.setValue(classObj);
	}

	public EvaluationEnvironment evaluateDeclarations(EvaluationEnvironment addtlEnv) {
		EvaluationEnvironment thisEnv = decls.extendWithDecls(EvaluationEnvironment.EMPTY);
		decls.bindDecls(declEvalEnv.extend(addtlEnv), thisEnv);

		return thisEnv;
	}

	public EvaluationEnvironment getClassEnv(EvaluationEnvironment extEvalEnv) {

		EvaluationEnvironment classEnv = EvaluationEnvironment.EMPTY;

		if (decls == null)
			return classEnv;

		for (Declaration decl : decls.getDeclIterator()) {
			if (decl.isClassMember()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
		}

		ClassBinding thisBinding = new ClassBinding("class", this);
		EvaluationEnvironment evalEnv = classEnv.extend(thisBinding);

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

	public EvaluationEnvironment getFilledBody(AtomicReference<Value> objRef) {
		return evaluateDeclarations(
				EvaluationEnvironment.EMPTY
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
		ClassDeclaration classDeclaration = new ClassDeclaration(nameBinding.getName(), implementsName, implementsClassName,
				new DeclSequence(decls), classMembersEnv.get(), typeParams, location);
		classDeclaration.setupTags(nameBinding.getName(), classDeclaration.typeBinding, getTaggedInfo());
		return classDeclaration;
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
