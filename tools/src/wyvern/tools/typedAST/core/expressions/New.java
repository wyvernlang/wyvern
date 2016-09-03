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
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
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

    private static int genericNum = 0;
    private static int uniqueCounter = 0;
    private static Map<String, Expression> variables = new HashMap<>();

    private FileLocation location = FileLocation.UNKNOWN;
    private ClassDeclaration cls;
    private Map<String, TypedAST> args = new HashMap<String, TypedAST>();
    private boolean isGeneric = false;
    private DeclSequence seq;

    private Type ct;
    private String selfName;

    /**
      * Makes a New expression with the provided mapping, file location, and self name.
      *
      * @param args The mapping from arg name to Expression.
      * @param fileLocation the location in the file where the New expression occurs 
      * @param selfName the name of the object created by this expression, like 'this' in Java
      */
    public New(Map<String, TypedAST> args, FileLocation fileLocation, String selfName) {
        this.args = args;
        this.location = fileLocation;
        this.selfName = selfName;
    }

    /**
      * Makes a New expression with the provided mapping and file location.
      *
      * @param args The mapping from arg name to Expression.
      * @param fileLocation the location in the file where the New expression occurs 
      */
    public New(Map<String, TypedAST> args, FileLocation fileLocation) {
        this.args = args;
        this.location = fileLocation;
        this.selfName = null;
    }

    /**
      * This constructor makes a New expression with the provided declaration sequence.
      *
      * @param seq the list of declaration internal to the object created by this expression
      * @param fileLocation the location in the file where the New expression occurs 
      */
    public New(DeclSequence seq, FileLocation fileLocation) {
        this.seq = seq;
        this.location = fileLocation;
    }

    public void setBody(DeclSequence seq) {
        this.seq = seq;
    }

    public DeclSequence getDecls() {
        return seq;
    }

    /**
     * Resets the count of generics.
     */
    public static void resetGenNum() {
        genericNum = 0;
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {
        writer.writeArgs(cls);
        // FIXME: Not sure if this is rigth (Alex).
        for (TypedAST a : this.args.values()) {
            writer.writeArgs(a);
        }
    }

    private String self() {
        return (this.selfName == null) ? "this" : this.selfName;
    }

    private ClassBinding fetchClassBinding(Environment env) {
        return env.lookupBinding(
            "class", ClassBinding.class
        ).orElse(null);
    }

    private Environment buildInnerEnv(Environment env, Environment declEnv, ClassBinding clsBind) {
        Environment innerEnv;
             
        innerEnv = seq.extendName(Environment.getEmptyEnvironment(), env);
        innerEnv = innerEnv.extend(declEnv);

        innerEnv = env.extend(
            new NameBindingImpl(this.self(),
            new ClassType(
                new Reference<>(innerEnv),
                new Reference<>(innerEnv),
                new LinkedList<>(),
                clsBind.getClassDecl().getTaggedInfo(),
                clsBind.getClassDecl().getName()
        )));
        return innerEnv;
    }

    private Type typecheckClassMethod(
            Environment env,
            Optional<Type> expected,
            ClassBinding clsBind) {

        ClassBinding classVarTypeBinding = clsBind;
        Type classVarType = buildClassVarType(clsBind, env);

        if (!(classVarType instanceof ClassType)) {
            ToolError.reportError(
                ErrorMessage.MUST_BE_LITERAL_CLASS,
                this,
                classVarType.toString()
            );
        }

        // Cache these guys for later use, since they're dependent on the Environment
        this.cls = classVarTypeBinding.getClassDecl();
        this.ct = classVarType;

        return classVarType;
    }

    private Type buildClassVarType(ClassBinding varType, Environment env) {
        Environment declEnv = varType.getClassDecl()
            .getInstanceMembersEnv();

        Environment innerEnv = buildInnerEnv(env, declEnv, varType);
        seq.typecheck(innerEnv, Optional.empty());

        Environment nnames = seq.extendType(declEnv, declEnv.extend(env));
        nnames = seq.extendName(nnames, nnames.extend(env));

        Environment objTee = TypeDeclUtils.getTypeEquivalentEnvironment(
                nnames.extend(declEnv)
        );

        return buildClassType(nnames.extend(declEnv), objTee, varType);
    }

    private Type buildClassType(Environment env, Environment objTee, ClassBinding varTypeBinding) {
        return new ClassType(
            new Reference<>(env),
            new Reference<>(objTee),
            new LinkedList<>(),
            varTypeBinding.getClassDecl().getTaggedInfo(),
            varTypeBinding.getClassDecl().getName()
        );
    }

    // compute tag info
    private TaggedInfo infoFromExpected(Optional<Type> expected) {
        TaggedInfo tagInfo = null;
        if (expected.isPresent()) {
            Type t = expected.get();
            if (t instanceof RecordType) {
                tagInfo = ((RecordType)t).getTaggedInfo();
            }
        }
        return tagInfo;
    }

    private Environment buildDeclEnv(
            Environment env, Environment innerEnv, Optional<Type> expected) {
        TaggedInfo tagInfo = infoFromExpected(expected);
        return env.extend(
            new NameBindingImpl(
                this.self(), 
                new ClassType(
                    new Reference<>(innerEnv), 
                    new Reference<>(innerEnv), 
                    new LinkedList<>(), 
                    tagInfo, null
                )
            )
        );
    }

    private Environment buildInnerDeclEnv(
            Environment declEnv,
            Environment savedInner) {
        return StreamSupport.stream(
            this.seq.getDeclIterator().spliterator(),
            false
        ).reduce(
            declEnv, 
            (oenv,decl) -> (decl instanceof ClassDeclaration)
                    ? decl.extend(oenv, savedInner)
                    : oenv,(a,b) -> a.extend(b)
        );
    }

    private void typecheckInnerDecl(Environment innerDeclEnv) {
        this.seq.getDeclIterator().forEach(
            decl -> decl.typecheck(
                innerDeclEnv, Optional.<Type>empty()
            )
        );
    }

    private Type typecheckStandaloneMethod(
            Environment env,
            Optional<Type> expected,
            ClassBinding clsBind) {

        ClassBinding classVarTypeBinding = clsBind;
        this.isGeneric = true;
        Environment innerEnv = seq.extendType(Environment.getEmptyEnvironment(), env);
        Environment savedInner = env.extend(innerEnv);
        innerEnv = seq.extendName(innerEnv, savedInner);

        final Environment declEnv  = buildDeclEnv(env, innerEnv, expected);
        final Environment ideclEnv = buildInnerDeclEnv(declEnv, savedInner);
        final Environment mockEnv  = Environment.getEmptyEnvironment();
        typecheckInnerDecl(ideclEnv);

        Environment nnames = seq.extendType(mockEnv, mockEnv.extend(env));
        nnames = seq.extendName(nnames, mockEnv.extend(env));
        ClassDeclaration classDeclaration = simpleClassDeclaration();

        this.cls = classDeclaration;
        this.ct = classTypeFromNames(nnames, expected);
        return this.ct;
    }

    private ClassDeclaration simpleClassDeclaration() {
        return new ClassDeclaration(
            "generic" + this.genericNum++,
            "",
            "",
            new DeclSequence(new LinkedList<Declaration>()),
            Environment.getEmptyEnvironment(),
            new LinkedList<String>(), 
            getLocation()
        );
    }

    private ClassType classTypeFromNames(Environment nnames, Optional<Type> expected) {
        Environment tee = TypeDeclUtils.getTypeEquivalentEnvironment(
            nnames.extend(Environment.getEmptyEnvironment())
        );

        return new ClassType(
            new Reference<>(
                nnames.extend(Environment.getEmptyEnvironment())
            ),
            new Reference<>(tee),
            new LinkedList<String>(),
            infoFromExpected(expected),
            null
        );
    }

    @Override
    protected Type doTypecheck(Environment env, Optional<Type> expected) {
        // TODO check arg types
        // Type argTypes = args.typecheck();

        ClassBinding classVarTypeBinding = fetchClassBinding(env);

        Type result = null;

        if (classVarTypeBinding != null) { // In a class method
            result = typecheckClassMethod(env, expected, classVarTypeBinding);
        } else { // Standalone
            result = typecheckStandaloneMethod(env, expected, classVarTypeBinding);
        }
        return result;
    }

    private EvaluationEnvironment getGenericDecls(
            EvaluationEnvironment env, 
            EvaluationEnvironment mockEnv, 
            LinkedList<Declaration> decls
    ) {
        return mockEnv;
    }

    @Override
    public Value evaluate(EvaluationEnvironment env) {
        EvaluationEnvironment argValEnv = EvaluationEnvironment.EMPTY;
        for (Entry<String, TypedAST> elem : args.entrySet()) {
            argValEnv = argValEnv.extend(
                    new ValueBinding(
                        elem.getKey(),
                        elem.getValue().evaluate(env)
                    )
            );
        }

        ClassBinding classVarTypeBinding = (ClassBinding) env.lookupValueBinding(
                "class", 
                ClassBinding.class
        ).orElse(null);
        ClassDeclaration classDecl;

        if (classVarTypeBinding != null) {
            classDecl = classVarTypeBinding.getClassDecl();
        } else {

            Environment mockEnv = Environment.getEmptyEnvironment();

            classDecl = new ClassDeclaration(
                    "generic" + genericNum++,
                    "",
                    "",
                    new DeclSequence(),
                    mockEnv,
                    new LinkedList<String>(),
                    getLocation()
            );
        }

        AtomicReference<Value> objRef = new AtomicReference<>();
        EvaluationEnvironment evalEnv = env.extend(
                new LateValueBinding(
                    this.self(),
                    objRef,
                    ct)
        );
        classDecl.evalDecl(
                evalEnv,
                classDecl.extendWithValue(EvaluationEnvironment.EMPTY)
        );
        final EvaluationEnvironment ideclEnv = StreamSupport.stream(
            seq.getDeclIterator().spliterator(), false)
            .reduce(evalEnv,
                    ((oenv,decl) -> 
                        (decl instanceof ClassDeclaration)
                        ? decl.evalDecl(oenv)
                        : oenv),
                    EvaluationEnvironment::extend
            );
        EvaluationEnvironment objenv = seq.bindDecls(
                ideclEnv,
                seq.extendWithDecls(classDecl.getFilledBody(objRef))
        );

        TaggedInfo goodTI = env.lookupBinding(
                this.self(),
                HackForArtifactTaggedInfoBinding.class
        )
            .map(binding -> binding.getTaggedInfo())
            .orElse(classDecl.getTaggedInfo());

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
        outMap.put(
                "seq",
                (seq == null) ? new DeclSequence(Arrays.asList()) : seq
        );
        return outMap;
    }

    /**
      * addNewFile evaluates the expression and adds that expression 
      * to the field generated by this New expression
      *
      * @param value the Expression which should be evaluated as a new field.
      */
    public static String addNewField(Expression value) {
        String name = "field " + uniqueCounter++;
        variables.put(name, value);
        return name;
    }

    @Override
    public void codegenToIL(
            GenerationEnvironment environment,
            ILWriter writer
    ) {
        //TODO: support new inside classes
        List<wyvern.target.corewyvernIL.decl.Declaration> genDecls = new LinkedList<>();
        for (Declaration decl : getDecls().getDeclIterator()) {
            genDecls.addAll(
                    DeclarationWriter.generate(
                        writer, 
                        dw -> decl.codegenToIL(environment, dw)
                    )
            );
        }
        wyvern.target.corewyvernIL.expression.New exn = 
            new wyvern.target.corewyvernIL.expression.New(
                genDecls,
                this.self(),
                null, getLocation()
            );
        Expression output = exn;
        for (String key : variables.keySet()) {
            output = new Let(key, null, variables.get(key), output);
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

    @Override
    public FileLocation getLocation() {
        return location;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies
    ) {

        ValueType type = seq.inferStructuralType(ctx, this.self());
        
        // Translate the declarations.
        GenContext thisContext = ctx.extend(
                this.self(),
                new wyvern.target.corewyvernIL.expression.Variable(this.self()),
                type
        );
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = 
            new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for (TypedAST d : seq) {            
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) d)
                .generateDecl(ctx, thisContext);
            if (decl == null) {
                throw new NullPointerException();
            }
            decls.add(decl);
            
            // A VarDeclaration also generates declarations for 
            // the getter and setter to the var field.
            // TODO: is the best place for this to happen?
            if (d instanceof VarDeclaration) {
                VarDeclaration varDecl = (VarDeclaration) d;
                String varName = varDecl.getName();
                Type varType = varDecl.getType();
                
                // Create references to "this" for the generated methods.
                wyvern.tools.typedAST.core.expressions.Variable receiver1;
                wyvern.tools.typedAST.core.expressions.Variable receiver2;

                receiver1 = new wyvern.tools.typedAST.core.expressions.Variable(
                        new NameBindingImpl(this.self(), null),
                        null
                );
                receiver2 = new wyvern.tools.typedAST.core.expressions.Variable(
                        new NameBindingImpl(this.self(), null),
                        null
                );
                
                // Generate getter and setter; add to the declarations.
                wyvern.target.corewyvernIL.decl.Declaration getter;
                wyvern.target.corewyvernIL.decl.Declaration setter;
                getter = DefDeclaration.generateGetter(ctx, receiver1, varName, varType)
                    .generateDecl(thisContext, thisContext);
                setter = DefDeclaration.generateSetter(ctx, receiver2, varName, varType)
                    .generateDecl(thisContext, thisContext);
                decls.add(getter);
                decls.add(setter);  
            }
        }
        // if type is not specified, infer
        return new wyvern.target.corewyvernIL.expression.New(
                decls,
                this.self(),
                type,
                getLocation()
        );
    }
    
    public void setSelfName(String n) {
        this.selfName = n;
    }
}
