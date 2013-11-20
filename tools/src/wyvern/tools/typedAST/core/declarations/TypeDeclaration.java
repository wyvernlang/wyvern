package wyvern.tools.typedAST.core.declarations;

import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.TreeWriter;

public class TypeDeclaration extends Declaration implements CoreAST {
	protected DeclSequence decls;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Environment declEvalEnv;
	protected AtomicReference<Obj> attrObj = new AtomicReference<>();
    protected AtomicReference<Environment> declEnv;
	protected AtomicReference<Environment> attrEnv = new AtomicReference<>(Environment.getEmptyEnvironment());

	public AtomicReference<Obj> getAttrObjRef() {
		return attrObj;
	}

	public Environment getAttrEnv() {
		return attrEnv.get();
	}

	public ClassType getAttrType() {
		return (ClassType) nameBinding.getType();
	}

	public static class AttributeDeclaration extends Declaration {
		private final TypedAST body;

		public AttributeDeclaration(TypedAST body) {
			this.body = body;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		protected Type doTypecheck(Environment env) {
			return body.typecheck(env);
		}

		@Override
		protected Environment doExtend(Environment old) {
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
			return null;
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
	}

    public TypeDeclaration(String name, DeclSequence decls, FileLocation clsNameLine) {
		this.decls = decls;
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, null);
		declEnv = new AtomicReference<>(null);
		Type objectType = new TypeType(this);
		attrEnv.set(attrEnv.get().extend(new TypeDeclBinding("type", this)));
		Type classType = new ClassType(attrEnv, attrEnv); // TODO set this to a class type that has the class members
		nameBinding = new NameBindingImpl(nameBinding.getName(), classType);
		typeBinding = new TypeBinding(nameBinding.getName(), objectType);
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
		}

		return this.typeBinding.getType();
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

		Environment attrEnv = evalEnv;
		for (Declaration decl : decls.getDeclIterator()) {
			if (decl instanceof AttributeDeclaration) {
				attrEnv = ((DeclSequence)((AttributeDeclaration) decl).getBody()).evalDecls(attrEnv);
			}
		}
		Obj typeAttrObj = new Obj(attrEnv);
		
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		attrObj.set(typeAttrObj);
		vb.setValue(typeAttrObj);
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

    public NameBinding lookupDecl(String name) {
        return declEnv.get().lookup(name);
    }


	public AtomicReference<Environment> getDeclEnv() {
		return declEnv;
	}
}