package wyvern.target.corewyvernIL.decl;

import java.util.ArrayList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.util.Pair;


public class ModuleDeclaration extends DefDeclaration {
    private List<Pair<ImportDeclaration, ValueType>> dependencies; // The list of platform-dependent modules we depend on

    public ModuleDeclaration(String moduleName, List<FormalArg> formalArgs, ValueType type, IExpr body,
            List<Pair<ImportDeclaration, ValueType>> dependencies, FileLocation loc) {
        super(moduleName, formalArgs, type, body, loc);
        this.dependencies = dependencies;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    public List<Pair<ImportDeclaration, ValueType>> getDependencies() {
        return dependencies;
    }

    public Pair<Declaration, List<TypedModuleSpec>> specialize(String platform, GenContext ctx) {
        ModuleResolver interpResolver = ModuleResolver.getLocal();
        ModuleResolver resolver = interpResolver; //new ModuleResolver(platform, interpResolver.getRootDir(), interpResolver.getLibDir());
        resolver.setInterpreterState(InterpreterState.getLocalThreadInterpreter());

        List<TypedModuleSpec> recursiveDependencies = new ArrayList<>();

        IExpr body = getBody();
        for (Pair<ImportDeclaration, ValueType> decl : dependencies) {
            Pair<VarBinding, GenContext> pair = decl.getFirst().genBinding(resolver, ctx, recursiveDependencies);
            body = new Let(pair.getFirst(), body);
        }

        if (getFormalArgs().isEmpty()) {
            return new Pair<Declaration, List<TypedModuleSpec>>(
                    new ValDeclaration(getName(), getType(), body, getLocation()), recursiveDependencies);
        } else {
            return new Pair<Declaration, List<TypedModuleSpec>>(
                    new DefDeclaration(getName(), getFormalArgs(), getType(), body, getLocation()), recursiveDependencies);
        }
    }

    @Override
    public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
        ModuleResolver resolver = InterpreterState.getLocalThreadInterpreter().getResolver();
        for (Pair<ImportDeclaration, ValueType> pair: dependencies) {
            Module module = resolver.resolveModule(pair.getFirst().getUri().getSchemeSpecificPart());
            //String internalName = module.getSpec().getInternalName();
            thisCtx = thisCtx.extend(module.getSpec().getSite(), pair.getSecond());
        }
        return super.typeCheck(ctx, thisCtx);
    }
}
