package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.binding.typechecking.LateNameBinding;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.TypeDeclBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;


public class TypeDeclaration extends AbstractTypeDeclaration implements CoreAST {
	private String name;
	protected DeclSequence decls;
	private Reference<Optional<TypedAST>> metadata;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private EvaluationEnvironment declEvalEnv;
    protected Reference<Environment> declEnv = new Reference<>(Environment.getEmptyEnvironment());
	protected Reference<Environment> attrEnv = new Reference<>(Environment.getEmptyEnvironment());
	
	public static EvaluationEnvironment attrEvalEnv = EvaluationEnvironment.EMPTY; // HACK
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
			declEnv.set(decls.extendName(declEnv.get(), against.extend(typeBinding).extend(declEnv.get())));
			//declEnv.set(decls.extend(declEnv.get(), against.extend(typeBinding)));
			declGuard = true;
		}

		return env.extend(nameBinding);
	}
	
	public TypeDeclaration(String name, DeclSequence decls, Reference<Value> metadata, TaggedInfo taggedInfo, FileLocation clsNameLine) {
		// System.out.println("Initialising TypeDeclaration ( " + name + "): decls" + decls);
		this.name = name;
		this.decls = decls;
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, null, metadata);
		Type objectType = new TypeType(this);

		attrEnv.set(attrEnv.get().extend(new TypeDeclBinding("type", this)));


		nameBinding = new LateNameBinding(nameBinding.getName(), () ->
				metadata.get().getType());
		typeBinding = new TypeBinding(nameBinding.getName(), objectType, metadata);

		setupTags(name, typeBinding, taggedInfo);
		// System.out.println("TypeDeclaration: " + nameBinding.getName() + " is now bound to type: " + objectType);

		this.location = clsNameLine;
		this.metaValue = metadata;
	}
	
    public TypeDeclaration(String name, DeclSequence decls, Reference<Value> metadata, FileLocation clsNameLine) {
		this(name, decls, metadata, null, clsNameLine);
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
		TypeDeclaration decls1 = new TypeDeclaration(nameBinding.getName(), (DeclSequence) newChildren.get("decls"), metaValue, getTaggedInfo(), location);
		return decls1;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Unimplemented", this);
        //return new wyvern.target.corewyvernIL.decl.TypeDeclaration(getName(), null); //TODO
    }

    @Override
	public Type doTypecheck(Environment env) {
		Environment eenv = decls.extend(env, env);
		
		for (Declaration decl : decls.getDeclIterator()) {
			decl.typecheckSelf(eenv);
		}

		if (isTagged()) typecheckTags(env);
		
		return this.typeBinding.getType();
	}	
	
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		Environment newEnv = old.extend(nameBinding).extend(typeBinding);
		// newEnv = newEnv.extend(new NameBindingImpl("this", nameBinding.getType())); // Why is there "this" in a type (not class)?
		
		return newEnv;
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		EvaluationEnvironment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		return newEnv;
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		declEvalEnv = declEnv;
		if (metaValue.get() == null)
			metaValue.set(metadata.get().orElseGet(() -> new New(new DeclSequence(), FileLocation.UNKNOWN)).evaluate(evalEnv));
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName()).orElseThrow(() -> new RuntimeException("Internal Error - TypeDeclaration NameBinding broken"));
		vb.setValue(metaValue.get());
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


	public Reference<Environment> getDeclEnv() {
		return declEnv;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		wyvern.target.corewyvernIL.decl.Declaration typeDecl = this.generateDecl(ctx, null);
		List<wyvern.target.corewyvernIL.decl.Declaration> decls=
			new ArrayList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(typeDecl);
		return new wyvern.target.corewyvernIL.expression.New(decls, ctx.generateName(), null); // type to be implemented
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		String selfName = this.name;
		
		List<DeclType> declts = 
				new ArrayList<wyvern.target.corewyvernIL.decltype.DeclType>();
		for(Declaration d : decls.getDeclIterator()) {
			DeclType declt = ((Declaration) d).genILType(ctx);
			declts.add(declt);
		}
		
 		StructuralType type = new StructuralType(selfName, declts);
		wyvern.target.corewyvernIL.decl.TypeDeclaration decl =
				new wyvern.target.corewyvernIL.decl.TypeDeclaration(this.name, type);
		return decl;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
}