package wyvern.target.corewyvernIL.decl;

import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.FileLocation;

public abstract class Declaration extends ASTNode implements IASTNode {
    public boolean containsResource(TypeContext ctx) {
        return false;
    }

    public Declaration(FileLocation loc) {
        super(loc);
    }
    public abstract DeclType typeCheck(TypeContext ctx, TypeContext thisCtx);

    /**
     * Interprets val and var declarations down to a value binding.
     * For other declarations this just returns the receiver
     */
    public Declaration interpret(EvalContext ctx) {
        return this;
    }

    /** Returns the name of this declaration, or null if this declaration is not named */
    public abstract String getName();

    public BytecodeOuterClass.Declaration emitBytecode() {
        System.out.println("emitBytecode not implemented for " + this.getClass().getName());
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }

    public abstract Set<String> getFreeVariables();

    public boolean isTypeOrEffectDecl() {
        // defaults to false
        return false;
    }
}
