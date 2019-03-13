package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.tools.errors.HasLocation;

public final class EffectAnnotationChecker {

    private EffectAnnotationChecker() {
        return;
    }

    public static boolean isAnnotated(final GenContext ctx, final IExpr expression) {

        New expNew = (New) expression;
        Declaration decl = expNew.getDecls().get(0);
        DefDeclaration defDecl = (DefDeclaration) decl;

        final SeqExpr body = (SeqExpr) defDecl.getBody();
        for (HasLocation element : body.getElements()) {
            if (element instanceof New) {
                New elementNew = (New) element;
                for (Declaration declaration : elementNew.getDecls()) {
                    if (!isAnnotated(ctx, declaration)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean isAnnotated(GenContext ctx, Declaration declaration) {
        if (declaration instanceof DefDeclaration) {
            DefDeclaration defDecl = (DefDeclaration) declaration;
            DefDeclType declType = defDecl.getDeclType();
            if (declType.getEffectSet() == null) {
                return false;
            }
        }
        return true;
    }
}
