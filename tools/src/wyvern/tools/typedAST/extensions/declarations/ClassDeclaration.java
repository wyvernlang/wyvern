package wyvern.tools.typedAST.extensions.declarations;

import java.util.Map;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.Obj;
import wyvern.tools.typedAST.extensions.values.ClassObject;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ObjectType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class ClassDeclaration extends Declaration implements CoreAST {
	private Declaration decls;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Environment declEvalEnv;
	
	public ClassDeclaration(String name, String implementsName, Declaration decls) {
		// TODO: Handle implements properly as list of names - what is the syntax though?
		
		this.decls = decls;
		Type objectType = new ObjectType(this);
		Type classType = objectType; // TODO set this to a class type that has the class members
		typeBinding = new TypeBinding(name, objectType);
		nameBinding = new NameBindingImpl(name, classType);
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
		
		env = env.extend(new NameBindingImpl("this", nameBinding.getType()));
		decl = decls;
		Environment eenv = decls.extend(env);
		while (decl != null) {
			decl.typecheckSelf(eenv);
			decl = decl.getNextDecl();
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

}
