package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class ConstructDeclaration extends Declaration implements CoreAST {
    private NameBinding binding;
    private List<NameBinding> argNames;

    public ConstructDeclaration(String name, List<NameBinding> argNames, FileLocation location) {
        binding = new NameBindingImpl(name, new UnresolvedType(name, FileLocation.UNKNOWN));
        this.argNames = argNames;
        this.location = location;
    }

    // @Override
    // public List<NameBinding> getArgBindings() {
    //     return argNames;
    // }

    @Override
    public Type getType() {
        return binding.getType();
    }

    @Override
    public String getName() {
        return binding.getName();
    }

    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        ValueType vt = getILValueType(ctx);
        return new ValDeclType(getName(), vt);
    }

    private ValueType getILValueType(GenContext ctx) {
        ValueType vt;
        final Type type = new UnresolvedType("Unit", FileLocation.UNKNOWN);
        vt = type.getILType(ctx);
        return vt;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {

        ValueType expectedType = getILValueType(thisContext);
        /* uses ctx for generating the definition, as the selfName is not in scope */

        List<FormalArg> args = new LinkedList<FormalArg>();
        GenContext methodContext = thisContext;
        for (NameBinding b : argNames) {
            ValueType argType = b.getType().getILType(thisContext);
            args.add(new FormalArg(b.getName(), argType));
            methodContext = methodContext.extend(b.getName(), new Variable(b.getName()), argType);
            thisContext = thisContext.extend(b.getName(), new Variable(b.getName()), argType);
        }

        //need to create ConstructDeclaration which accepts args
        //return new wyvern.target.corewyvernIL.decl.DefDeclaration(
        //        getName(), args, this.resultType, body.generateIL(methodContext, this.resultType, null), location, null);

        return new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(), expectedType, null, location);
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        wyvern.target.corewyvernIL.decl.Declaration decl =
                new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(),
                        getILValueType(tlc.getContext()),
                        new wyvern.target.corewyvernIL.expression.Variable(getName()), location);
        DeclType dt = genILType(tlc.getContext());

        tlc.addModuleDecl(decl, dt);
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("constructor ");
        sb.append(" : ");
        sb.append("null");
        return sb;
    }
}
