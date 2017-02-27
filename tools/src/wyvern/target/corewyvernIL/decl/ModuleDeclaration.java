package wyvern.target.corewyvernIL.decl;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;


public class ModuleDeclaration extends DefDeclaration {
    private List<TypedModuleSpec> dependencies; // The list of platform-dependent modules we depend on

    public ModuleDeclaration(String moduleName, List<FormalArg> formalArgs, ValueType type, IExpr body,
                             List<TypedModuleSpec> dependencies, FileLocation loc) {
        super(moduleName, formalArgs, type, body, loc);
        this.dependencies = dependencies;
    }

    public Declaration specialize(String platform) {
        ModuleResolver interpResolver = ModuleResolver.getLocal();
        ModuleResolver resolver = new ModuleResolver(platform, interpResolver.getRootDir(), interpResolver.getLibDir());
        IExpr body = resolver.wrap(getBody(), dependencies);

        if (getFormalArgs().isEmpty())
            return new ValDeclaration(getName(), getType(), body, getLocation());
        else
            return new DefDeclaration(getName(), getFormalArgs(), getType(), body, getLocation());
    }
}
