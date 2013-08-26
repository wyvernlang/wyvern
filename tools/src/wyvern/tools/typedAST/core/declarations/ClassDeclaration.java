package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

import java.util.concurrent.atomic.AtomicReference;

public class ClassDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	// protected DeclSequence classDecls;
	
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private String implementsName; // FIXME: Should be bindings with proper equals implementation?
	private String implementsClassName;
	
	private NameBinding nameImplements;
	
	protected Environment declEvalEnv;

	private TypeType equivalentType = null;
	private TypeType equivalentClassType = null;
	private AtomicReference<Environment> typeEquivalentEnvironmentRef;
	protected AtomicReference<Environment> declEnvRef;

	public ClassDeclaration(String name, String implementsName, String implementsClassName, DeclSequence decls, Environment declEnv, FileLocation location) {
        this(name, implementsName, implementsClassName, decls, location);
		declEnvRef.set(declEnv);
    }

    public ClassDeclaration(String name, String implementsName, String implementsClassName, DeclSequence decls, FileLocation location) {
		this.decls = decls;
		typeEquivalentEnvironmentRef = new AtomicReference<>();
		declEnvRef = new AtomicReference<>();
		nameBinding = new NameBindingImpl(name, null);
		Type objectType = getClassType();
		Type classType = objectType; // TODO set this to a class type that has the class members
		typeBinding = new TypeBinding(name, objectType);
		nameBinding = new NameBindingImpl(name, classType);
		this.implementsName = implementsName;
		this.implementsClassName = implementsClassName;
		this.location = location;
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
	
	public TypeType getTypeType() { // FIXME: Should this be really what getType returns?
		if (this.nameImplements != null) {
			return (TypeType) this.nameImplements.getType();
		} else { return null; }
	}

	@Override
	public Type doTypecheck(Environment env) {


		// FIXME: Currently allow this and class in both class and object methods. :(


		Environment genv = env.extend(new ClassBinding("class", this));
		Environment oenv = genv.extend(new NameBindingImpl("this", nameBinding.getType()));

		if (decls != null)
			for (Declaration decl : decls.getDeclIterator()) {
				if (decl instanceof DefDeclaration && ((DefDeclaration) decl).isClass())
					decl.typecheckSelf(genv);
				else
					decl.typecheckSelf(oenv);

			}
		
		// check the implements and class implements
		// FIXME: Should support multiple implements statements!
		if (!this.implementsName.equals("")) {
			this.nameImplements = env.lookup(this.implementsName);
			if (nameImplements == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this.implementsName, this);
			}
			
			// since there is a valid implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
			TypeType implementsTT = (TypeType) nameImplements.getType();
			
			if (!getEquivalentType().subtype(implementsTT)) {
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
						this.nameBinding.getName(),
						nameImplements.getName(),
						this); 
			}
		}
		
		if (!this.implementsClassName.equals("")) {
			NameBinding nameImplementsClass = env.lookup(this.implementsClassName);
			if (nameImplementsClass == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this.implementsClassName, this);
			}

			// since there is a valid class implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
			TypeType implementsCT = (TypeType) nameImplementsClass.getType();
			
			if (!getEquivalentClassType().subtype(implementsCT)) {
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
						this.nameBinding.getName(),
						nameImplementsClass.getName(),
						this); 
			}
		}

		return Unit.getInstance();
	}	
	
	@Override
	protected Environment doExtend(Environment old) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		
		// FIXME: Currently allow this and class in both class and object methods. :(
		//newEnv = newEnv.extend(new TypeBinding("class", typeBinding.getType()));
		//newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType()));
		
		return newEnv;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		declEvalEnv = declEnv.extend(evalEnv);
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
		Obj classObj = new Obj(getClassEnv());
		
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(classObj);
	}
	
	public Environment evaluateDeclarations(Environment addtlEnv) {
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
		decls.bindDecls(declEvalEnv.extend(addtlEnv), thisEnv);
		
		return thisEnv;
	}
	
	public Environment getClassEnv() {
		
		Environment classEnv = Environment.getEmptyEnvironment();
		
		for (Declaration decl : decls.getDeclIterator()) {
			if (decl instanceof DefDeclaration && ((DefDeclaration) decl).isClass()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
			decl = decl.getNextDecl();
		}

		ClassBinding thisBinding = new ClassBinding("class", this);
		Environment evalEnv = classEnv.extend(thisBinding);
		
		for (Declaration decl : decls.getDeclIterator())
			if (decl instanceof DefDeclaration && ((DefDeclaration) decl).isClass()){
				decl.bindDecl(evalEnv,classEnv);
			}
		
		return classEnv;
	}

	public Declaration getDecl(String opName) {
		for (Declaration d : decls.getDeclIterator()) {
			// TODO: handle fields too
			if (d.getName().equals(opName))
				return d;
			d = d.getNextDecl();
		}
		return null;	// can't find it
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

    public NameBinding lookupDecl(String name) {
        return declEnvRef.get().lookup(name);
    }

	public Environment getDeclEnv() {
		return declEnvRef.get();
	}

	public TypeType getImplementsType() {
		return getTypeType();
	}

	public AtomicReference<Environment> getTypeEquivalentEnvironmentReference() {
		return typeEquivalentEnvironmentRef;
	}

	public AtomicReference<Environment> getDeclEnvRef() {
		return declEnvRef;
	}

	public Type getEquivalentClassType() {
		if (equivalentClassType == null)
			equivalentClassType = new TypeType(TypeDeclUtils.getTypeEquivalentEnvironment(decls, true));
		return equivalentClassType;
	}

	public Environment getFilledBody(AtomicReference<Value> objRef) {
		return evaluateDeclarations(
				Environment
						.getEmptyEnvironment()
						.extend(new LateValueBinding("this", objRef, getType())));
	}
}
