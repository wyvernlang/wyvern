package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class EffectAnnotationChecker {

    private EffectAnnotationChecker() { }

    /**
     * Check if the annotated module only depends on annotated or lifted module
     * Add empty annotations to where annotation is missing
     * @param resolver module resolver
     * @param ctx context
     * @param moduleDecl declaration of the module
     * @param dependencies dependencies of the module
     */
    public static void checkModule(ModuleResolver resolver, GenContext ctx, ModuleDeclaration moduleDecl,
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
                if (spec.getIsAnnotated() != null && !spec.getIsAnnotated()) {
                    if (!liftedModules.contains(spec.getModule())) {
                        ToolError.reportError(ErrorMessage.EFFECT_ANNOTATION_SEPARATION, FileLocation.UNKNOWN);
                    }
                }
            }

            // Annotate missing annotations with empty effect set
            annotateModule(ctx, typedAST);
            assert (isAnnotated(ctx, typedAST));
        }
    }

    /**
     * Make the module fully annotated
     * @param ctx context
     */
    private static void annotateModule(GenContext ctx, TypedAST ast) {
        if (ast instanceof DeclSequence) {
            DeclSequence seq = (DeclSequence) ast;
            Sequence normalSeq = seq.filterNormal();
            Iterator<TypedAST> astIterator = normalSeq.flatten();
            while (astIterator.hasNext()) {
                TypedAST nextAST = astIterator.next();
                annotateModule(ctx, nextAST);
            }
        } else if (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration) {
            wyvern.tools.typedAST.core.declarations.DefDeclaration def =
                    (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
            if (def.getEffectSet(ctx) == null) {
                def.setEmptyEffectSet();
            }
        }
    }

    /**
     * Check if the ast is fully annotated
     * @param ctx: context to evaluate in.
     * @param ast : the object we want to check
     * @return true if the ast is annotated
     */
    private static boolean isAnnotated(GenContext ctx, TypedAST ast) {
        if (ast instanceof DeclSequence) {
            DeclSequence seq = (DeclSequence) ast;
            Sequence normalSeq = seq.filterNormal();
            Iterator<TypedAST> astIterator = normalSeq.flatten();
            while (astIterator.hasNext()) {
                TypedAST nextAST = astIterator.next();
                if (!isAnnotated(ctx, nextAST)) {
                    return false;
                }
            }
            return true;
        } else if (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration) {
            wyvern.tools.typedAST.core.declarations.DefDeclaration def =
                    (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
            return def.getEffectSet(ctx) != null;
        } else {
            return true;
        }
    }
}
