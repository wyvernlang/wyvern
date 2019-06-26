package wyvern.tools.parsing.coreparser;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.generics.GenericArgument;
import wyvern.tools.generics.GenericKind;
import wyvern.tools.generics.GenericParameter;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.core.Script;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
//import wyvern.tools.typedAST.core.declarations.ConstructDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ForwardDeclaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.Instantiation;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assertion;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Case;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.Match;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.CharacterConstant;
import wyvern.tools.typedAST.core.values.FloatConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.NamedType;
import wyvern.tools.types.QualifiedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.TypeExtension;
import wyvern.tools.typedAST.core.declarations.RecConstructDeclaration;
import wyvern.tools.typedAST.core.declarations.RecDeclaration;

public class WyvernASTBuilder implements ASTBuilder<TypedAST, Type> {

    @Override
    public TypedAST recDecl(TypedAST body) {
      return new RecDeclaration(body);
    }

    @Override
    public TypedAST recConstructDecl(String name, Type type, TypedAST body, FileLocation loc) {
      return new RecConstructDeclaration(name, type, body, loc);
    }

    /* Weirdness: DeclSequence typechecks everything simultaneously without
     * extending the environment, unless it's inside a module.  So if we're
     * not inside a module, use Sequence instead.
     */
    @Override
    public TypedAST sequence(TypedAST t1, TypedAST t2, boolean inModule) {
        if (inModule) {
            return DeclSequence.simplify(new DeclSequence(t1, t2));
        } else {
            return new Sequence(t1, t2);
        }
    }


    @Override
    public TypedAST moduleDecl(String name, List<TypedAST> imports, List<GenericParameter> generics, List args,
                               TypedAST ast, Type type, FileLocation loc, boolean isResource, boolean isAnnotated, String effects) {
        return new ModuleDeclaration(name, imports, generics, args, ast, (NamedType) type, loc, isResource, isAnnotated, effects);
    }

    @Override
    public TypedAST importDecl(URI uri, FileLocation loc, Token name, boolean isRequire, boolean isMetadata, boolean isLifted) {
        return new ImportDeclaration(uri, loc, (name == null) ? null : name.image, isRequire, isMetadata, isLifted);
    }

    @Override
    public TypedAST valDecl(String name, Type type, TypedAST exp, FileLocation loc) {
        return new ValDeclaration(name, type, exp, loc);
    }

    @Override
    public TypedAST varDecl(String name, Type type, TypedAST exp, FileLocation loc) {
        return new VarDeclaration(name, type, exp, loc);
    }

    @Override
    public TypedAST defDecl(String name, Type type, List<GenericParameter> generics, List args,
                            TypedAST body, boolean isClassDef, FileLocation loc, String effects) {
        return new DefDeclaration(name, type, generics, args, body, isClassDef, loc, effects);
    }

    @Override
    public TypedAST effectDecl(String name, String effects, FileLocation loc) {
        return new EffectDeclaration(name, effects, loc);
    }

    @Override
    public TypedAST defDeclType(String name, Type type, List<GenericParameter> generics, List args, FileLocation loc, String effects) {
        return new DefDeclaration(name, type, generics, args, null, false, loc, effects);
    }

    @Override
    public TypedAST valDeclType(String name, Type type, FileLocation loc) {
        return new ValDeclaration(name, type, null, loc);
    }

    @Override
    public TypedAST varDeclType(String name, Type type, FileLocation loc) {
        return new VarDeclaration(name, type, null, loc);
    }

    @Override
    public TypedAST effectDeclType(String name, String effects, FileLocation loc) {
        return new EffectDeclaration(name, effects, loc);
    }

    @Override
    public TypedAST constructDeclType(String name, List<GenericParameter> generics, List args, FileLocation loc) {
        TypedAST body;
        if (args == null) {
            args = new LinkedList<>();
        }
        List<NameBinding> argNames = args;
        LinkedList<TypedAST> exps = new LinkedList<>();

        for (NameBinding b : argNames) {
            String nameCons = b.getName();
            Type t = b.getType();
            TypedAST constructArgs = valDeclType(nameCons, t, null);
            exps.add(constructArgs);
        }
        if (generics != null) {
            for (GenericParameter gp : generics) {
                String parameterName = gp.getName();
                final TypedAST parameter;
                switch (gp.getKind()) {
                    case TYPE:
                        parameter = typeAbbrevDecl(parameterName, null, null, loc);
                        break;
                    case EFFECT:
                        parameter = effectDecl(parameterName, null, loc);
                        break;
                    default:
                        throw new RuntimeException("Unhandled generic parameter kind: " + gp.getKind());
                }
                exps.add(parameter);
            }
        }
        body = new DeclSequence(exps);
        return new TypeVarDecl(name, (DeclSequence) body, null, null, null, loc, true, null);
    }

    @Override
    public TypedAST typeDecl(String name, TypedAST body, Object tagInfo, TypedAST metadata, FileLocation loc, boolean isResource, String selfName) {
        if (body == null) {
            body = new DeclSequence();
        }
        if (!(body instanceof DeclSequence)) {
            body = new DeclSequence(Arrays.asList(body));
        }

        if (((DeclSequence) body).hasVarDeclaration() && !isResource) {
            ToolError.reportError(ErrorMessage.MUST_BE_A_RESOURCE, loc, name);
        }

        //Reference<Value> meta = (metadata==null)?null:new Reference<Value>((Value)metadata);
        //return new TypeDeclaration(name, (DeclSequence) body, null, (TaggedInfo) tagInfo, loc);
        return new TypeVarDecl(name, (DeclSequence) body, (TaggedInfo) tagInfo, null, metadata, loc, isResource, selfName);
    }

    @Override
    public TypedAST datatypeDecl(String name,
                                 List<GenericParameter> generics,
                                 TypedAST body,
                                 Object tagInfo,
                                 TypedAST metadata,
                                 FileLocation loc,
                                 boolean isResource,
                                 String selfName) {

        LinkedList<TypedAST> exps = new LinkedList<>();
        LinkedList<TypedAST> ctors = new LinkedList<>();

        /* Produce the declaration of the top-level datatype */
        LinkedList<Type> compriseList = new LinkedList<>();
        if (!(body instanceof DeclSequence)) {
            body = new DeclSequence(Arrays.asList(body));
        }
        for (TypedAST elem : ((DeclSequence) body).getIterator()) {
            if (!(elem instanceof TypeVarDecl)) {
                ToolError.reportError(ErrorMessage.PARSE_ERROR, elem, "Datatype declarations may only have datatype case declarations");
            }
            String nameCons = ((TypeVarDecl) elem).getName();
            Type t = nominalType(nameCons, null);
            compriseList.add(t);
        }
        Object tagInfoComprise = tagInfo(null, compriseList);
        DeclSequence datatypeBody = new DeclSequence();
        if (generics != null) {
            // act as if these generics were added to the body of this constructor
            LinkedList<TypedAST> newExps = makeGenericDecls(generics, loc);
            datatypeBody = new DeclSequence(newExps);
        }
        TypedAST dt = new TypeVarDecl(name, datatypeBody, (TaggedInfo) tagInfoComprise, null, metadata, loc, isResource, selfName);
        exps.add(dt);

        /* Produce declarations for the type constructors and value constructors */
        Type extended = nominalType(name, null);
        Object tagInfoExtend = tagInfo(extended, null);
        for (TypedAST elem : ((DeclSequence) body).getIterator()) {
            /* The value constructor */
            String nameCons = ((TypeVarDecl) elem).getName();
            DeclSequence bodyCons = ((TypeVarDecl) elem).getBody();
            LinkedList<Object> args = new LinkedList<>();
            LinkedList<GenericParameter> constructorGenerics = new LinkedList<>();
            Type constructorType = this.nominalType(nameCons, loc);
            if (generics != null) {
                // act as if these generics were added to the body of this constructor
                LinkedList<TypedAST> newExps = makeGenericDecls(generics, loc);
                bodyCons.forEach(d -> newExps.add(d));
                bodyCons = new DeclSequence(newExps);

                LinkedList<GenericArgument> genericArguments = new LinkedList<>();
                for (GenericParameter gp : generics) {
                    String parameterName = gp.getName();
                    switch (gp.getKind()) {
                        case TYPE:
                            genericArguments.add(new GenericArgument(nominalType(parameterName, loc)));
                            break;
                        case EFFECT:
                            genericArguments.add(new GenericArgument(parameterName));
                            break;
                        default:
                            throw new RuntimeException("Unhandled generic parameter kind: " + gp.getKind());
                    }
                }
                constructorType = parameterizedType(constructorType, genericArguments, loc);
            }
            TypedAST newBody = null;
            for (TypedAST ast : bodyCons) {
                if (ast instanceof ValDeclaration) {
                    ValDeclaration vd = (ValDeclaration) ast;
                    args.add(this.formalArg(vd.getName(), vd.getType()));
                    TypedAST decl = this.valDecl(vd.getName(), vd.getType(), this.var(vd.getName(), loc), loc);
                    newBody = (newBody == null) ? decl : this.sequence(newBody, decl, true);
                } else if (ast instanceof TypeAbbrevDeclaration) {
                    TypeAbbrevDeclaration td = (TypeAbbrevDeclaration) ast;
                    constructorGenerics.add(new GenericParameter(GenericKind.TYPE, td.getName()));
                    TypedAST decl = typeAbbrevDecl(td.getName(), nominalType(td.getName(), td.getLocation()), null, td.getLocation());
                    newBody = (newBody == null) ? decl : this.sequence(newBody, decl, true);
                } else {
                    throw new RuntimeException("should be impossible");
                }
            }
            TypedAST defBody = this.newObj(loc, nameCons);
            this.setNewBody(defBody, newBody);
            TypedAST valueConstructor = this.defDecl(nameCons, constructorType, constructorGenerics, args, defBody, false, loc, null);
            ctors.add(valueConstructor);

            /* The type constructor */
            TypedAST cons = new TypeVarDecl(nameCons, bodyCons, (TaggedInfo) tagInfoExtend, constructorGenerics, null, loc, isResource, null);
            exps.add(cons);

        }

        exps.addAll(ctors);
        body = new DeclSequence(exps);

        return body;
    }

    private LinkedList<TypedAST> makeGenericDecls(List<GenericParameter> generics, FileLocation loc) {
        LinkedList<TypedAST> newExps = new LinkedList<>();
        for (GenericParameter gp : generics) {
            String name = gp.getName();
            final TypedAST decl;
            switch (gp.getKind()) {
                case TYPE:
                    decl = typeAbbrevDecl(name, null, null, loc);
                    break;
                case EFFECT:
                    decl = effectDecl(name, null, loc);
                    break;
                default:
                    throw new RuntimeException("Unhandled generic parameter kind: " + gp.getKind());
            }
            newExps.add(decl);
        }
        return newExps;
    }

    @Override
    public Object formalArg(String name, Type type) {
        return new NameBindingImpl(name, type);
    }

    @Override
    public TypedAST fn(List args, TypedAST body, FileLocation loc) {
        return new Fn(args, body, loc);
    }

    @Override
    public Type nominalType(String name, FileLocation loc) {
        return new UnresolvedType(name, loc);
    }

    @Override
    public Type arrowType(List<Type> arguments, Type result, boolean isResource, String effects, FileLocation location) {
        return new Arrow(arguments, result, isResource, effects, location);
    }

    @Override
    public Type parameterizedType(Type base, List<GenericArgument> genericArguments, FileLocation loc) {
        return new TypeExtension(base, genericArguments, loc);
    }

    @Override
    public Type qualifiedType(TypedAST base, String name) {
        return new QualifiedType(base, name);
    }


    @Override
    public TypedAST var(String name, FileLocation loc) {
        return new Variable(name, loc);
    }

    @Override
    public TypedAST stringLit(String value, FileLocation loc) {
        return new StringConstant(value, loc);
    }

    @Override
    public TypedAST characterLit(char value, FileLocation loc) {
        return new CharacterConstant(value, loc);
    }


    @Override
    public TypedAST integerLit(BigInteger value, FileLocation loc) {
        return new IntegerConstant(value, loc);
    }

    @Override
    public TypedAST booleanLit(boolean value, FileLocation loc) {
        return new BooleanConstant(value);
    }

    @Override
    public TypedAST invocation(TypedAST receiver, String name,
            TypedAST argument, FileLocation loc) {
        return new Invocation(receiver, name, argument, loc);
    }

    @Override
    public TypedAST application(TypedAST function, List<TypedAST> arguments,
            FileLocation loc, List<GenericArgument> genericArguments, boolean recur) {
        return new Application(function, arguments, loc != null ? loc : function.getLocation(), genericArguments, recur);
    }

    @Override
    public void addArgument(TypedAST application, TypedAST argument) throws ParseException {
        if (!(application instanceof Application)) {
            ToolError.reportError(ErrorMessage.PARSE_ERROR,
                    argument.getLocation(), "Added an additional TSL argument to something that was not an application");
        }
        Application app = (Application) application;
        app.addArgument(argument);
    }

    @Override
    public TypedAST addArguments(TypedAST application, List<String> names, List<TypedAST> arguments) throws ParseException {
        if (!(application instanceof Application)) {
            ToolError.reportError(ErrorMessage.ILLEGAL_JUXTAPOSITION, application);
        }
        Application app = (Application) application;
        List<GenericArgument> generics = app.getGenerics();
        List<TypedAST> args = new LinkedList<TypedAST>(app.getArguments());
        TypedAST function = app.getFunction();
        StringBuilder name = new StringBuilder();
        args.addAll(arguments);

        // what to do about function?
        if (function instanceof Variable) {
            Variable var = (Variable) function;
            name.append(var.getName());
            names.forEach(name::append);
            function = new Variable(name.toString(), var.getLocation());
        } else if (function instanceof Invocation) {
            Invocation inv = (Invocation) function;
            if (inv.getArgument() != null) {
                ToolError.reportError(ErrorMessage.ILLEGAL_BINARY_JUXTAPOSITION, application);
            }
            name.append(inv.getOperationName());
            names.forEach(name::append);
            function = new Invocation(inv.getReceiver(), name.toString(), null, inv.getLocation());
        } else {
            throw new RuntimeException();
        }

        return new Application(function, args, app.getLocation(), generics, app.isMarkedAsTailCall());
    }

    @Override
    public TypedAST unitValue(FileLocation loc) {
        return UnitVal.getInstance(loc);
    }

    @Override
    public TypedAST newObj(FileLocation loc, String selfName) {
        New n = new New(new HashMap<String, TypedAST>(), loc);
        n.setSelfName(selfName);
        return n;
    }

    @Override
    public void setNewBody(TypedAST newExp, TypedAST decls) {
        if (decls == null) {
            decls = new DeclSequence(Arrays.asList());
        }
        if (!(decls instanceof DeclSequence)) {
            decls = new DeclSequence(Arrays.asList(decls));
        }
        ((New) newExp).setBody((DeclSequence) decls);
    }

    @Override
    public TypedAST assignment(TypedAST lhs, TypedAST rhs, FileLocation loc) {
        return new Assignment(lhs, rhs, loc);
    }

    @Override
    public TypedAST forwardDecl(Type type, TypedAST exp, FileLocation loc) {
        return new ForwardDeclaration(type, exp, loc);
    }

    @Override
    public TypedAST match(TypedAST exp, List cases, FileLocation loc) {
        return new Match(exp, cases, loc);
    }

    @Override
    public Object caseArm(String name, Type type, TypedAST exp,
            FileLocation loc) {
        return name.equals("default") ? new Case(exp) : new Case(name, type, exp);
    }

    @Override
    public Object tagInfo(Type type, List<Type> comprises) {
        return new TaggedInfo(type, comprises);
    }

    @Override
    public TypedAST instantiation(URI uri, List<TypedAST> args, Token name, FileLocation loc) {
        return new Instantiation(uri, args, name.image, loc);
    }

    @Override
    public TypedAST parseExpr(String source, FileLocation loc) {
        ExpressionAST ast;
        try {
            String withoutLeading = wyvern.stdlib.support.AST.utils.stripLeadingWhitespace(source, false);
            int oldNewlineIndex = source.indexOf('\n');
            int newlineIndex = withoutLeading.indexOf('\n');
            String leading = source.substring(0, oldNewlineIndex - newlineIndex);
            FileLocation locAfterStrip = new FileLocation(loc.getFilename(),
                                                          loc.getLine(),
                                                          loc.getCharacter() + wyvern.stdlib.support.AST.utils.charCount(leading));
            ast = (ExpressionAST) TestUtil.getNewAST(withoutLeading + "\n", locAfterStrip);
            if (ast instanceof Script) {
                Script s = (Script) ast;
                if (!s.getImports().isEmpty() || !s.getRequires().isEmpty()) {
                    ToolError.reportError(ErrorMessage.PARSE_ERROR,
                                          s.getImports().isEmpty() ? s.getRequires().get(0).getLocation() : s.getImports().get(0).getLocation(),
                                          "may not have import or requires here");
                }
            }
        } catch (ParseException e) {
            Token errorLocToken = e.getCurrentToken();
            FileLocation errorLoc = getLocation(loc, errorLocToken);
            ToolError.reportError(ErrorMessage.PARSE_ERROR, errorLoc, e.getMessage());
            throw new RuntimeException("weird, shouldn't get here");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("weird, shouldn't get here");
        }
        return ast;
    }

    /*private FileLocation adjustLocation(FileLocation baseLocation, Token errorLocToken) {
        int line = errorLocToken.beginLine;
        int character = errorLocToken.beginColumn;
        FileLocation newLoc = new FileLocation(baseLocation.getFilename(), baseLocation.getLine() + line - 1, baseLocation.getCharacter() + character - 1);
        return newLoc;
    }*/

    private FileLocation getLocation(FileLocation baseLocation, Token errorLocToken) {
        int line = errorLocToken.beginLine;
        int character = errorLocToken.beginColumn + 1;
        FileLocation newLoc = new FileLocation(baseLocation.getFilename(), line, character);
        return newLoc;
    }

    @Override
    public TypedAST typeAbbrevDecl(String alias, Type reference, TypedAST metadata, FileLocation loc) {
        return new TypeAbbrevDeclaration(alias, reference, metadata, loc);
    }

    @Override
    public TypedAST dsl(FileLocation loc) {
        return new DSLLit(Optional.empty(), loc);
    }

    @Override
    public void setDSLBody(TypedAST dslExp, String text) {
        ((DSLLit) dslExp).setText(text);
    }

    @Override
    public TypedAST script(List<TypedAST> requires, List<TypedAST> imports, TypedAST body) {
        return new Script(requires, imports, body);
    }

    @Override
    public TypedAST assertion(String description, TypedAST exp, FileLocation loc) {
        return new Assertion(description, exp, loc);
    }

    public TypedAST floatLit(float value, FileLocation loc) {
      return new FloatConstant(value, loc);
    }

}
