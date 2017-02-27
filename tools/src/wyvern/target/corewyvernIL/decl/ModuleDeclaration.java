package wyvern.target.corewyvernIL.decl;

import java.util.ArrayList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.util.Pair;


public class ModuleDeclaration extends DefDeclaration {
    private List<ImportDeclaration> dependencies; // The list of platform-dependent modules we depend on

    public ModuleDeclaration(String moduleName, List<FormalArg> formalArgs, ValueType type, IExpr body,
                             List<ImportDeclaration> dependencies, FileLocation loc) {
        super(moduleName, formalArgs, type, body, loc);
        this.dependencies = dependencies;
    }

    public Declaration specialize(String platform, GenContext ctx) {
        ModuleResolver interpResolver = ModuleResolver.getLocal();
        ModuleResolver resolver = new ModuleResolver(platform, interpResolver.getRootDir(), interpResolver.getLibDir());

        IExpr body = getBody();
        for (ImportDeclaration decl : dependencies) {
            List<TypedModuleSpec> recursiveDependencies = new ArrayList<>();
            Pair<VarBinding, GenContext> pair = decl.genBinding(resolver, ctx, recursiveDependencies);
            body = new Let(pair.first, body);
            body = resolver.wrap(body, recursiveDependencies);
        }

        if (getFormalArgs().isEmpty())
            return new ValDeclaration(getName(), getType(), body, getLocation());
        else
            return new DefDeclaration(getName(), getFormalArgs(), getType(), body, getLocation());
    }
}
