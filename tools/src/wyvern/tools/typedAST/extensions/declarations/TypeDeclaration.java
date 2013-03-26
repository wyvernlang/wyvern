package wyvern.tools.typedAST.extensions.declarations;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.DeclSequence;
import wyvern.tools.typedAST.extensions.values.Obj;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class TypeDeclaration extends Declaration implements CoreAST {
	private DeclSequence decls;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Environment declEvalEnv;
	
	public TypeDeclaration(String name, DeclSequence decls, FileLocation clsNameLine) {
		this.decls = decls;
		Type objectType = new TypeType(this);
		Type classType = objectType; // TODO set this to a class type that has the class members
		typeBinding = new TypeBinding(name, objectType);
		nameBinding = new NameBindingImpl(name, classType);
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
	public Type doTypecheck(Environment env) {
		
		// env = env.extend(new NameBindingImpl("this", nameBinding.getType()));
		Environment eenv = decls.extend(env);
		for (Declaration decl : decls.getDeclIterator()) {
			decl.typecheckSelf(eenv);
			decl = decl.getNextDecl();
		}

		return Unit.getInstance();
	}	
	
	@Override
	protected Environment doExtend(Environment old) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		// newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType())); // Why is there "this" in a type (not class)?
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
		
		// TODO: 
		
		// ClassObject classObj = new ClassObject(this);
		
		// ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		// vb.setValue(classObj);
	}
	
	public Environment evaluateDeclarations(Obj obj) {
		Environment thisEnv = decls.extendWithDecls(Environment.getEmptyEnvironment());
		
		ValueBinding thisBinding = new ValueBinding("this", obj);
		Environment intEnv = declEvalEnv.extend(thisBinding);
		decls.bindDecls(intEnv, thisEnv);
		
		return thisEnv;
	}

	public Declaration getDecl(String opName) {

		for (Declaration d : decls.getDeclIterator()) {
			// TODO: handle fields too
			if (d.getName().equals(opName))
				return d;
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
		return location; 
	}
}