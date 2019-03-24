/**
 * Typed AST of an effect; parses the definition of the effect
 * from a String into a set of effects.
 *
 * @author vzhao
 */

package wyvern.tools.typedAST.core.declarations;

import java.io.IOException;
import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class EffectDeclaration extends Declaration {
    private String name;
    private EffectSet effectSet;
    private FileLocation loc;
    private boolean adapted = false;

    public EffectDeclaration(String name, String effects, FileLocation fileLocation) {
        this.name = name;
        this.loc = fileLocation;

        /* Parses the String effects into a set. If it was not defined:
         * effectSet==null if in type signature, else error is reported */
        this.effectSet = EffectSet.parseEffects(name, effects, true, fileLocation);
    }

    public Effect getEffect() {
        return new Effect(null, getName(), getLocation());
    }

    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("effect ").append(getName()).append(" = ");
        if (effectSet != null) {
            dest.append(effectSet.toString());
        }
        dest.append('\n');
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public FileLocation getLocation() {
        return loc;
    }

    @Override
    public String getName() {
        return name;
    }

    public EffectSet getEffectSetInContext(GenContext ctx) {
        if (!adapted && effectSet != null) {
            effectSet.contextualize(ctx);
            adapted = true;
        }
        return effectSet;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        return new EffectDeclType(getName(), getEffectSetInContext(ctx), getLocation());
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        EffectSet effectSetInContext = getEffectSetInContext(ctx);
        if (effectSetInContext != null) {
            effectSetInContext.verifyInType(ctx);
        }
        return new wyvern.target.corewyvernIL.decl.EffectDeclaration(getName(), effectSetInContext, getLocation());
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        return generateDecl(ctx, ctx); // like in DefDeclaration
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        wyvern.target.corewyvernIL.decl.Declaration decl = topLevelGen(tlc.getContext(), null);
        DeclType dt = genILType(tlc.getContext());
        tlc.addModuleDecl(decl, dt);
    }
}