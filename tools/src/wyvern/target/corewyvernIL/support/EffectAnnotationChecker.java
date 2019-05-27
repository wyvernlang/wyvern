package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.AnnotatedEffectVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class EffectAnnotationChecker {

    private EffectAnnotationChecker() { }

    /**
     * Check if the annotated module only depends on annotated or lifted module
     * Add empty annotations to where annotation is missing
     * @param resolver module resolver
     * @param ctx context
     * @param moduleDecl declaration of the module
     * @param dependencies dependencies of the module
     */
    static void checkModule(ModuleResolver resolver, GenContext ctx, ModuleDeclaration moduleDecl,
                                   List<TypedModuleSpec> dependencies) {
        TypedAST typedAST = moduleDecl.getInner();
        if (moduleDecl.isAnnotated()) {

            // Collect import lifted modules
            List<ImportDeclaration> imports = moduleDecl.getImports();
            Set<Module> liftedModules = new HashSet<>();
            for (ImportDeclaration decl : imports) {
                if (decl.isLifted()) {
                    String moduleName = decl.getUri().getSchemeSpecificPart();
                    Module m = resolver.resolveModule(moduleName, false, true);
                    liftedModules.add(m);
                }
            }

            // Check if dependencies are annotated or lifted
            for (TypedModuleSpec spec : dependencies) {
                if (!spec.getIsAnnotated()) {
                    if (!liftedModules.contains(spec.getModule())
                            && !spec.getModule().toString().contains("Module(wyvern")
                            && !spec.getModule().toString().contains("Module(platform")) {
                        System.out.println(spec.getModule());
                        ToolError.reportError(ErrorMessage.EFFECT_ANNOTATION_SEPARATION, FileLocation.UNKNOWN);
                    }
                }
            }

            // Annotate missing annotations with empty effect set
            typedAST.acceptVisitor(new AnnotatedEffectVisitor(), ctx);
        }
    }
}
