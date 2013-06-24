package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ClassDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	protected DeclSequence classDecls;
	
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private String implementsName; // FIXME: Should be bindings with proper equals implementation?
	private String implementsClassName;
	
	private NameBinding nameImplements;
	
	protected Environment declEvalEnv;
	
	public ClassDeclaration(String name, String implementsName, String implementsClassName, DeclSequence decls, FileLocation location) {
		this.decls = decls;
		Type objectType = getClassType();
		Type classType = objectType; // TODO set this to a class type that has the class members
		typeBinding = new TypeBinding(name, objectType);
		nameBinding = new NameBindingImpl(name, classType);
		this.implementsName = implementsName;
		this.implementsClassName = implementsClassName;
		this.location = location;
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
		
		Environment genv = env.extend(new TypeBinding("class", typeBinding.getType()));
		Environment oenv = genv.extend(new NameBindingImpl("this", nameBinding.getType()));

		if (decls != null)
			for (Declaration decl : decls.getDeclIterator()) {
				if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth())
					decl.typecheckSelf(genv);
				else
					decl.typecheckSelf(oenv);

			}
		
		// check the implements and class implements
		if (!this.implementsName.equals("")) {
			this.nameImplements = env.lookup(this.implementsName);
			if (nameImplements == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this.implementsName, this);
			}
			
			// since there is a valid implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
			TypeType implementsTT = (TypeType) nameImplements.getType();
			
			if (!currentCT.checkImplements(implementsTT)) {
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
			
			if (!currentCT.checkImplementsClass(implementsCT)) {
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
		ClassObject classObj = new ClassObject(this);
		
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(classObj);
	}
	
	public Environment evaluateDeclarations(Obj obj) {
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
		
		ValueBinding thisBinding = new ValueBinding("this", obj);
		Environment intEnv = declEvalEnv.extend(thisBinding);
		decls.bindDecls(intEnv, thisEnv);
		
		return thisEnv;
	}
	
	public Environment getClassEnv() {
		
		Environment classEnv = Environment.getEmptyEnvironment();
		
		for (Declaration decl : decls.getDeclIterator()) {
			if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
			decl = decl.getNextDecl();
		}

		TypeBinding thisBinding = new TypeBinding("class", typeBinding.getType());
		Environment evalEnv = classEnv.extend(thisBinding);
		
		for (Declaration decl : decls.getDeclIterator())
			if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth()){
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

	public ClassObject createObject() {
		return new ClassObject(this);
	}
}
