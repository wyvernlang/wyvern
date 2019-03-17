package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.Iterator;
import java.util.List;

public final class EffectAnnotationChecker {

    private EffectAnnotationChecker() {
        return;
    }

    public static void checkModule(GenContext ctx, ModuleDeclaration moduleDecl, List<TypedModuleSpec> dependencies) {
        TypedAST typedAST = moduleDecl.getInner();
        if (moduleDecl.isAnnotated()) {
            List<ImportDeclaration> imports = moduleDecl.getImports();
            for (ImportDeclaration decl : imports) {
                if (decl.isLifted()) {
                    continue;
                }
            }

            for (TypedModuleSpec spec : dependencies) {
                if (spec.getIsAnnotated() != null && !spec.getIsAnnotated()) {
                    ToolError.reportError(ErrorMessage.EFFECT_ANNOTATION_SEPARATION, FileLocation.UNKNOWN);
                }
            }

            // TODO: Check if all dependency is lifted

            // Annotate missing annotations with empty effect set
            annotateModule(ctx, typedAST);
            assert (isAnnotated(ctx, typedAST));
        }
    }

    /**
     * Make the module fully annotated
     * @param ctx
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
            if (def.getEffectSet(ctx) == null) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
}
