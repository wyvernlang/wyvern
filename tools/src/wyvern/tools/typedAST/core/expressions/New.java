package wyvern.tools.typedAST.core.expressions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.HackForArtifactTaggedInfoBinding;
import wyvern.tools.typedAST.core.binding.evaluation.LateValueBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.ClassBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.DeclarationWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class New extends CachingTypedAST implements CoreAST {
	ClassDeclaration cls;

	Map<String, TypedAST> args = new HashMap<String, TypedAST>();
	boolean isGeneric = false;

	private static int generic_num = 0;
	private DeclSequence seq;
	private Type ct;

	public void setBody(DeclSequence seq) {
		this.seq = seq;
	}

	public DeclSequence getDecls() { return seq; }

	public static void resetGenNum() {
		generic_num = 0;
	}

	public New(Map<String, TypedAST> args, FileLocation fileLocation) {
		this.args = args;
		this.location = fileLocation;
	}

	public New(DeclSequence seq, FileLocation fileLocation) {
		this.seq = seq;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(cls);
		// FIXME: Not sure if this is rigth (Alex).
		for (TypedAST a : this.args.values()) {
			writer.writeArgs(a);
		}
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		// TODO check arg types
		// Type argTypes = args.typecheck();

		ClassBinding classVarTypeBinding = (ClassBinding) env.lookupBinding("class", ClassBinding.class).orElse(null);


		if (classVarTypeBinding != null) { //In a class method
			Environment declEnv = classVarTypeBinding.getClassDecl().getInstanceMembersEnv();
			Environment innerEnv = seq.extendName(Environment.getEmptyEnvironment(), env).extend(declEnv);
			seq.typecheck(env.extend(new NameBindingImpl("this",
					new ClassType(new Reference<>(innerEnv), new Reference<>(innerEnv), new LinkedList<>(), classVarTypeBinding.getClassDecl().getTaggedInfo(),
							classVarTypeBinding.getClassDecl().getName()))), Optional.empty());


			Environment environment = seq.extendType(declEnv, declEnv.extend(env));
			environment = seq.extendName(environment, environment.extend(env));
			Environment nnames = environment;//seq.extend(environment, environment);

			Environment objTee = TypeDeclUtils.getTypeEquivalentEnvironment(nnames.extend(declEnv));
			Type classVarType = new ClassType(new Reference<>(nnames.extend(declEnv)), new Reference<>(objTee), new LinkedList<>(),
					classVarTypeBinding.getClassDecl().getTaggedInfo(), classVarTypeBinding.getClassDecl().getName());
			if (!(classVarType instanceof ClassType)) {
				// System.out.println("Type checking classVarType: " + classVarType + " and clsVar = " + clsVar);
				ToolError.reportError(ErrorMessage.MUST_BE_LITERAL_CLASS, this, classVarType.toString());
			}

			// TODO SMELL: do I really need to store this?  Can get it any time from the type
			cls = classVarTypeBinding.getClassDecl();
			ct = classVarType;

			return classVarType;
		} else { // Standalone

			isGeneric = true;
			Environment innerEnv = seq.extendType(Environment.getEmptyEnvironment(), env);
			Environment savedInner = env.extend(innerEnv);
			innerEnv = seq.extendName(innerEnv, savedInner);

			// compute tag info
			TaggedInfo tagInfo = null;
			if (expected.isPresent()) {
				Type t = expected.get();
				if (t instanceof RecordType)
					tagInfo = ((RecordType)t).getTaggedInfo();
			}
			
			Environment declEnv = env.extend(new NameBindingImpl("this", new ClassType(new Reference<>(innerEnv), new Reference<>(innerEnv), new LinkedList<>(), tagInfo, null)));
			final Environment ideclEnv = StreamSupport.stream(seq.getDeclIterator().spliterator(), false).
					reduce(declEnv, (oenv,decl)->(decl instanceof ClassDeclaration)?decl.extend(oenv, savedInner):oenv,(a,b)->a.extend(b));
			seq.getDeclIterator().forEach(decl -> decl.typecheck(ideclEnv, Optional.<Type>empty()));


			Environment mockEnv = Environment.getEmptyEnvironment();

			LinkedList<Declaration> decls = new LinkedList<>();

			Environment nnames = (seq.extendType(mockEnv, mockEnv.extend(env)));
			nnames = (seq.extendName(nnames,mockEnv.extend(env)));
			//nnames = seq.extend(nnames, mockEnv.extend(env));

			ClassDeclaration classDeclaration = new ClassDeclaration("generic" + generic_num++, "", "", new DeclSequence(decls), mockEnv, new LinkedList<String>(), getLocation());
			cls = classDeclaration;
			Environment tee = TypeDeclUtils.getTypeEquivalentEnvironment(nnames.extend(mockEnv));

			ct = new ClassType(new Reference<>(nnames.extend(mockEnv)), new Reference<>(tee), new LinkedList<String>(), tagInfo, null);
			return ct;
		}
	}

	private EvaluationEnvironment getGenericDecls(EvaluationEnvironment env, EvaluationEnvironment mockEnv, LinkedList<Declaration> decls) {
		return mockEnv;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		EvaluationEnvironment argValEnv = EvaluationEnvironment.EMPTY;
		for (Entry<String, TypedAST> elem : args.entrySet())
			argValEnv = argValEnv.extend(new ValueBinding(elem.getKey(), elem.getValue().evaluate(env)));



		ClassBinding classVarTypeBinding = (ClassBinding) env.lookupValueBinding("class", ClassBinding.class).orElse(null);
		ClassDeclaration classDecl;

		if (classVarTypeBinding != null) {
			classDecl = classVarTypeBinding.getClassDecl();
		} else {

			Environment mockEnv = Environment.getEmptyEnvironment();

			classDecl = new ClassDeclaration("generic" + generic_num++, "", "", new DeclSequence(), mockEnv, new LinkedList<String>(), getLocation());
		}

		AtomicReference<Value> objRef = new AtomicReference<>();
		EvaluationEnvironment evalEnv = env.extend(new LateValueBinding("this", objRef, ct));
		classDecl.evalDecl(evalEnv, classDecl.extendWithValue(EvaluationEnvironment.EMPTY));
		final EvaluationEnvironment ideclEnv = StreamSupport.stream(seq.getDeclIterator().spliterator(), false).
				reduce(evalEnv, (oenv,decl)->(decl instanceof ClassDeclaration)?decl.evalDecl(oenv):oenv, EvaluationEnvironment::extend);
		EvaluationEnvironment objenv = seq.bindDecls(ideclEnv, seq.extendWithDecls(classDecl.getFilledBody(objRef)));

		TaggedInfo goodTI = env.lookupBinding("this", HackForArtifactTaggedInfoBinding.class)
				.map(binding -> binding.getTaggedInfo()).orElse(classDecl.getTaggedInfo());

		Obj obj = new Obj(objenv.extend(argValEnv), goodTI);

		//FIXME: Record new tag!
		if (classDecl.isTagged()) {
			TaggedInfo ti = classDecl.getTaggedInfo();
			// System.out.println("Processing ti = " + ti);
			// System.out.println("obj.getType = " + obj.getType());
			ti.associateWithObject(obj);
		}

		objRef.set(obj);

		// System.out.println("Finished evaluating new: " + this);

		return objRef.get();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String,TypedAST> outMap = new HashMap<>();
		outMap.put("seq", (seq==null)? new DeclSequence(Arrays.asList()) : seq);
		return outMap;
	}

    private static int uniqueCounter = 0;
    private static Map<String, Expression> variables = new HashMap<>();
    public static String addNewField(Expression value) {
        String name = "field " + uniqueCounter++;
        variables.put(name, value);
        return name;
    }
    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) { //TODO: support new inside classes
        List<wyvern.target.corewyvernIL.decl.Declaration> genDecls = new LinkedList<>();
        for (Declaration decl : getDecls().getDeclIterator()) {
            genDecls.addAll(DeclarationWriter.generate(writer, dw -> decl.codegenToIL(environment, dw)));
        }
        wyvern.target.corewyvernIL.expression.New exn = new wyvern.target.corewyvernIL.expression.New(
                genDecls,
                "this",
        null);
        Expression output = exn;
        for (String key : variables.keySet()) {
            output = new Let(key, variables.get(key), output);
        }
        variables.clear();
        writer.write(output);
    }

    @Override
	public ExpressionAST doClone(Map<String, TypedAST> newChildren) {

		New aNew = new New(new HashMap<>(), location);
		aNew.setBody((DeclSequence) newChildren.get("seq"));
		aNew.cls = cls;
		return aNew;
	}

	public ClassDeclaration getClassDecl() {
		return cls;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		//TODO: fix args
		visitor.visit(this);
	}

	public Map<String, TypedAST> getArgs() {
		return args;
	}

	private FileLocation location = FileLocation.UNKNOWN;

	@Override
	public FileLocation getLocation() {
		return location;
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType) {

		// TODO see if another selfName is specified.
		// TODO translate the ascribed type, if any
		String selfName = "this";
		ValueType type = seq.inferStructuralType(ctx);
		
		// Translate the declarations.
		GenContext thisContext = ctx.extend(selfName, new wyvern.target.corewyvernIL.expression.Variable(selfName), type);
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		for (TypedAST d : seq) {
			
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) d).generateDecl(ctx, thisContext);
			if (decl == null) throw new NullPointerException();
			decls.add(decl);
			
			// A VarDeclaration also generates declarations for the getter and setter to the var field.
			// TODO: is the best place for this to happen?
			if (d instanceof VarDeclaration) {
				VarDeclaration varDecl = (VarDeclaration) d;
				String varName = varDecl.getName();
				Type varType = varDecl.getType();
				
				// Create references to "this" for the generated methods.
				wyvern.tools.typedAST.core.expressions.Variable receiver1, receiver2;
				receiver1 = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("this", null), null);
				receiver2 = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("this", null), null);
				
				// Generate getter and setter; add to the declarations.
				wyvern.target.corewyvernIL.decl.Declaration getter, setter;
				getter = DefDeclaration.generateGetter(ctx, receiver1, varName, varType).generateDecl(thisContext, thisContext);
				setter = DefDeclaration.generateSetter(ctx, receiver2, varName, varType).generateDecl(thisContext, thisContext);
				decls.add(getter);
				decls.add(setter);	
			}

		}
		// if type is not specified, infer
		if (expectedType == null) {
			return new wyvern.target.corewyvernIL.expression.New(decls, selfName, type);
		} else { // if type is specified, use that type
			return new wyvern.target.corewyvernIL.expression.New(decls, selfName, expectedType);
		}
//		return new wyvern.target.corewyvernIL.expression.New(decls, selfName, type);

	}
	
}