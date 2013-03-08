package wyvern.tools.typedAST.extensions.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.values.ClassObject;
import wyvern.tools.typedAST.extensions.values.Obj;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ClassDeclaration extends Declaration implements CoreAST {
	protected Declaration decls;
	protected Declaration classDecls;
	
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private String implementsName; // FIXME: Should be bindings with proper equals implementation?
	private String implementsClassName;
	
	private Environment declEvalEnv;
	
	public ClassDeclaration(String name, String implementsName, String implementsClassName, Declaration decls, int line) {
		this.decls = decls;
		Type objectType = new ClassType(this);
		Type classType = objectType; // TODO set this to a class type that has the class members
		typeBinding = new TypeBinding(name, objectType);
		nameBinding = new NameBindingImpl(name, classType);
		this.implementsName = implementsName;
		this.implementsClassName = implementsClassName;
		this.line = line;
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
		// TODO what should the type of a class declaration be?
		return Unit.getInstance();
	}

	@Override
	public Type doTypecheck(Environment env) {
		Declaration decl = decls;

		Environment genv = env.extend(new TypeBinding("class", typeBinding.getType()));
		Environment oenv = genv.extend(new NameBindingImpl("this", nameBinding.getType()));
		while (decl != null) {
			if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth())
				decl.typecheckSelf(genv);
			else
				decl.typecheckSelf(oenv);
			
			decl = decl.getNextDecl();
		}
		
		// check the implements and class implements
		if (!this.implementsName.equals("")) {
			NameBinding nameImplements = env.lookup(this.implementsName);
			if (nameImplements == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this.implementsName, this);
			}
			
			// since there is a valid implements, check that all methods are indeed present
			ClassType currentCT = (ClassType) this.nameBinding.getType();
			TypeType implementsTT = (TypeType) nameImplements.getType();
			
			if (!currentCT.subtypeOf(implementsTT)) {
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
			
			// FIXME: Should check only class methods but they are not identifiable now! :-?
			if (!currentCT.subtypeOf(implementsCT)) {
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
		return newEnv;
	}

	@Override
	protected Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		return newEnv;
	}

	@Override
	protected void evalDecl(Environment evalEnv, Environment declEnv) {
		declEvalEnv = declEnv;
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
		Declaration decl = decls;
		
		Environment classEnv = Environment.getEmptyEnvironment();
		
		while (decl != null) {
			if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth()){
				classEnv = decl.doExtendWithValue(classEnv);
			}
			decl = decl.getNextDecl();
		}

		TypeBinding thisBinding = new TypeBinding("class", typeBinding.getType());
		Environment evalEnv = classEnv.extend(thisBinding);
		
		for (decl = decls; decl != null; decl = decl.getNextDecl())
			if (decl instanceof MethDeclaration && ((MethDeclaration) decl).isClassMeth()){
				decl.bindDecl(evalEnv,classEnv);
			}
		
		return classEnv;
	}

	public Declaration getDecl(String opName) {
		Declaration d = decls;
		while (d != null) {
			// TODO: handle fields too
			if (d.getName().equals(opName))
				return d;
			d = d.getNextDecl();
		}
		return null;	// can't find it
	}
	
	public Declaration getDecls() {
		return decls;
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	private int line;
	public int getLine() {
		return this.line;
	}
}
