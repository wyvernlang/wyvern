package wyvern.tools.typedAST.core.declarations;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.generics.GenericKind;
import wyvern.tools.generics.GenericParameter;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.GetterAndSetterGeneration;
import wyvern.tools.util.TreeWritable;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends DeclarationWithGenerics implements CoreAST, BoundCode, TreeWritable {
    private ExpressionAST body; // HACK // FIXME:
    private String name;
    private Type returnType;
    private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.
    private List<FormalArg> argILTypes = new LinkedList<FormalArg>(); // store to preserve IL arguments types and return types
    private wyvern.target.corewyvernIL.type.ValueType returnILType = null;
    private EffectSet effectSet;
    private boolean adapted = false;

    public DefDeclaration(String name, Type returnType, List<GenericParameter> generics, List<NameBinding> argNames,
                          TypedAST body, boolean isClassDef, FileLocation location, String effects) {
        if (argNames == null) {
            argNames = new LinkedList<NameBinding>();
        }
        this.returnType = returnType;
        this.name = name;

        if (body == null || body instanceof ExpressionAST) {
            this.body = (ExpressionAST) body;
        } else {
            // FIXME:
            ToolError.reportError(ErrorMessage.PARSE_ERROR, location, "Body of a method declaration is not expression!");
        }

        this.argNames = argNames;
        this.isClass = isClassDef;
        this.location = location;
        this.effectSet = EffectSet.parseEffects(name, effects, false, location);
        this.generics = (generics != null) ? generics : new LinkedList<>();
    }

    public DefDeclaration(String name, Type returnType, List<NameBinding> argNames,
            TypedAST body, boolean isClassDef, FileLocation location) {
        this(name, returnType, null, argNames, body, isClassDef, location, null);
    }

    /*public static Arrow getMethodType(List<NameBinding> args, Type returnType, boolean isResource) {
        List<Type> argTypes = new LinkedList<Type>();
        for (int i = 0; i < args.size(); i++) {
            argTypes.add(args.get(i).getType());
        }
        return new Arrow(argTypes, returnType, isResource, effectSet, this.getLocation());
    }*/


    private boolean isClass;
    public boolean isClassMember() {
        return isClass;
    }

    /**
     * Set the effectset to be empty set
     * This function is used to make modules fully annotated
     */
    public void setEmptyEffectSet() {
        if (effectSet == null) {
            effectSet = new EffectSet(new HashSet<>());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public EffectSet getEffectSet(GenContext ctx) {
        if (!adapted && effectSet != null) {
            effectSet.contextualize(ctx);
            adapted = true;
        }

        return effectSet;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public List<NameBinding> getArgBindings() {
        return argNames;
    }

    @Override
    public TypedAST getBody() {
        return body;
    }

    private FileLocation location = FileLocation.UNKNOWN;

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        List<FormalArg> args = new LinkedList<FormalArg>();

        ctx = this.serializeArguments(args, ctx);

        DefDeclType ret = new DefDeclType(getName(), getResultILType(ctx), args, getEffectSet(ctx));
        return ret;
    }

    private GenContext serializeArguments(List<FormalArg> args, GenContext ctx) {
        if (isGeneric()) {
            GenContext[] contexts = new GenContext[1];
            contexts[0] = ctx;
            addGenericParameters(contexts, args);
            ctx = contexts[0];
        }

        for (NameBinding b : argNames) {
            String bName = b.getName();
            Type t =  b.getType();
            ValueType type = t.getILType(ctx);
            FormalArg fa = new FormalArg(bName, type);
            args.add(fa);
            ctx = ctx.extend(bName, new Variable(bName), type);
        }
        return ctx;
    }

    private boolean isGeneric() {
        return !this.generics.isEmpty();
    }

    public static boolean isGeneric(FormalArg a) {
        return a.getName().startsWith(GENERIC_PREFIX);
    }

    private ValueType getResultILType(GenContext ctx) {
        return returnType.getILType(ctx);
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        List<FormalArg> args = new LinkedList<FormalArg>();
        GenContext methodContext = thisContext;

        if (isGeneric()) {
            GenContext[] contexts = new GenContext[2];
            contexts[0] = methodContext;
            contexts[1] = thisContext;
            addGenericParameters(contexts, args);
            methodContext = contexts[0];
            thisContext = contexts[1];
        }

        for (NameBinding b : argNames) {
            ValueType argType = b.getType().getILType(thisContext);
            args.add(new FormalArg(b.getName(), argType));
            methodContext = methodContext.extend(b.getName(), new Variable(b.getName()), argType);
            thisContext = thisContext.extend(b.getName(), new Variable(b.getName()), argType);
        }
        this.returnILType = this.getResultILType(thisContext);
        this.argILTypes = args;

        EffectSet effectSet2 = getEffectSet(thisContext);
        if (effectSet2 != null) {
            effectSet2.verifyInType(thisContext);
        }
        return new wyvern.target.corewyvernIL.decl.DefDeclaration(
                getName(), args, getResultILType(thisContext), body.generateIL(methodContext, this.returnILType, null), getLocation(), effectSet2);
    }


    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        return generateDecl(ctx, ctx);
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        /*List<Expression> args = new LinkedList<Expression>();
        for (NameBinding arg : argNames) {
            args.add(new Variable(arg.getName()));
        }*/
        List<Expression> args = getArgILTypes().stream().map(a -> new Variable(a.getName())).collect(Collectors.toList());
        if (tlc.getReceiverName() == null) {
            throw new RuntimeException("must set receiver name before addModuleDecl on a def");
        }
        Expression body = new MethodCall(new Variable(tlc.getReceiverName()), name, args, this);

        if (argILTypes == null) {
            throw new NullPointerException("need to call topLevelGen/generateDecl before addModuleDecl");
        }
        wyvern.target.corewyvernIL.decl.DefDeclaration decl =
                new wyvern.target.corewyvernIL.decl.DefDeclaration(name,
                                                                   getArgILTypes(),
                                                                   getReturnILType(),
                                                                   body,
                                                                   getLocation(),
                                                                   getEffectSet(tlc.getContext()));

        DeclType dt = genILType(tlc.getContext());
        tlc.addModuleDecl(decl, dt);
    }

    public List<FormalArg> getArgILTypes() {
        return argILTypes;
    }

    public wyvern.target.corewyvernIL.type.ValueType getReturnILType() {
        return returnILType;
    }


    /**
     * Generate a getter method declaration for the field of an object.
     * @param ctx: context to evaluate in.
     * @param receiver: the object for which to make the getter.
     * @param varName: the name of the field.
     * @param varType: the type of the field.
     * @return a declaration for an appropriate getter method.
     */
    public static DefDeclaration generateGetter(GenContext ctx, wyvern.tools.typedAST.core.expressions.Variable receiver,
            String varName, Type varType) {

        // The body of the getter is an invocation of the form: receiver.varName
        String getterName = GetterAndSetterGeneration.varNameToGetter(varName);
        Invocation getterBody = new Invocation(receiver, varName, null, null);

        // Make and return the declaration.
        wyvern.tools.typedAST.core.declarations.DefDeclaration getterDecl;
        getterDecl = new wyvern.tools.typedAST.core.declarations.DefDeclaration(getterName, varType, new LinkedList<>(),
                getterBody, false, null); // may need to add effectSet?
        return getterDecl;

    }

    /**
     * Generate a setter method declaration for the field of an object.
     * @param ctx: context to evaluate in.
     * @param receiver: the object for which to make the setter.
     * @param varName: the name of the field.
     * @param varType: the type of the field.
     * @return a declaration for an appropriate setter method.
     */
    public static DefDeclaration generateSetter(GenContext ctx, wyvern.tools.typedAST.core.expressions.Variable receiver,
            String varName, Type varType) {

        // The body of the setter is an assignment of the form: receiver.varName = x
        String setterName = GetterAndSetterGeneration.varNameToSetter(varName);
        Invocation fieldGet = new Invocation(receiver, varName, null, null);
        wyvern.tools.typedAST.core.expressions.Variable valueToAssign;
        valueToAssign = new wyvern.tools.typedAST.core.expressions.Variable("x", null);
        Assignment setterBody = new Assignment(fieldGet, valueToAssign, null);

        // The setter takes one argument x : varType; its signature is varType -> Unit
        LinkedList<NameBinding> setterArgs = new LinkedList<>();
        setterArgs.add(new NameBindingImpl("x", varType));
        Type unitType = new UnresolvedType("Unit", receiver.getLocation());
        //Arrow setterArrType = new Arrow(varType, unitType);

        // Make and return the declaration.
        DefDeclaration setterDecl;
        setterDecl = new DefDeclaration(setterName, unitType, setterArgs, setterBody, false, null); // may need to add effectSet?
        return setterDecl;

    }

    public static StructuralType genericStructuralType(String genericName, BindingSite genericSite, GenericKind kind) {
        List<DeclType> bodyDecl = new LinkedList<>(); // these are the declarations internal to the struct

        // the body contains only a abstract type or effect member representing the generic type
        final DeclType member;
        switch (kind) {
            case TYPE:
                member = new AbstractTypeMember(genericName);
                break;
            case EFFECT:
                member = new EffectDeclType(genericName, null, null);
                break;
            default:
                throw new RuntimeException("Unhandled generic parameter kind: " + kind);
        }
        bodyDecl.add(member);

        return new StructuralType(genericSite, bodyDecl);
    }

    public static StructuralType boundedStructuralType(String genericName, BindingSite genericSite, EffectSet lb, EffectSet ub) {
        List<DeclType> bodyDecl = new LinkedList<>(); // these are the declarations internal to the struct

        // the body contains only a abstract type or effect member representing the generic type
        final DeclType member;
        member = new EffectDeclType(genericName, null, null, lb, ub);
        bodyDecl.add(member);

        return new StructuralType(genericSite, bodyDecl);
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("def " + name + " : (");
        String sep = "";
        for (FormalArg arg : argILTypes) {
            sb.append(sep); sep = ", ";
            try {
                sb.append(arg.prettyPrint());
            } catch (IOException e) {
                sb.append("Unknown");
            }
        }
        sb.append(") -> ");
        sb.append(returnType.toString());
        sb.append(" = ");
        sb.append(body.prettyPrint());
        if (effectSet != null) {
            sb.append(" with "); // just for distinction
            sb.append(effectSet.toString());
        }
        return sb;
    }
}
