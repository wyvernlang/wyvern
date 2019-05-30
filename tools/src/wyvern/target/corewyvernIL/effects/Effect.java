package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class Effect {
    private Path path;
    private final String name;
    private final FileLocation loc;

    public Effect(Path p, String n, FileLocation l) {
        path = p;
        name = n;
        loc = l;
    }

    public Path getPath() {
        return path;
    }

    /** For effects defined in the same signature (whose paths are null until typechecked) */
    public void setPath(Path p) {
        if (!(p instanceof Variable)) {
            ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, loc, name);
        }
        path = p;
    }

    public String getName() {
        return name;
    }

    public FileLocation getLocation() {
        return loc;
    }

    @Override
    public String toString() {
        return (path == null ? "" : getPath().toString() + ".") + getName();
    }

    public Effect adapt(View v) {
        if (path == null) {
            ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, loc, name);
        }
        return new Effect(path.adapt(v), name, loc);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Effect)) {
            return false;
        }

        final Effect eObj = (Effect) obj;
        if (eObj.getName().equals(getName()) && eObj.getPath().equals(getPath())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 67;
        int result = 1;
        result = prime * result + (getName() == null ? 0 : getName().hashCode());
        result = prime * result + (getPath() == null ? 0 : getPath().hashCode());
        return result;
    }

    /** Check that an effect exists in the context, returning its corresponding effect set at the end. */
    public EffectSet effectCheck(TypeContext ctx) {
        return findEffectDeclType(ctx).getEffectSet();
    }

    /** Find this effect's (effect)DeclType; report error if not found, else return effectDeclType. */
    public EffectDeclType findEffectDeclType(TypeContext ctx) {
        ValueType vt = null;

        // Without try/catch, this could result in a runtime exception due to EmptyGenContext
        // (which doesn't have FileLocation or HasLocation to call ToolError.reportError())
        try {
            // if path is null (due to failure of addPath() before) or typeCheck() fails
            if (getPath() == null) {
                // try to fill in the path
                if (ctx instanceof GenContext) {
                    adaptVariables((GenContext) ctx);
                }
                if (getPath() == null) {
                    ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
                }
            }

            vt = getPath().typeCheck(ctx, null);
        } catch (final RuntimeException ex) {
            ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
        }

        final DeclType eDT = vt.findMatchingDecl(getName(), cdt -> !cdt.isTypeOrEffectDecl(), ctx);
        if (eDT == null || !(eDT instanceof EffectDeclType)) {
            ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
        }
        return (EffectDeclType) eDT;
    }

    public Set<Effect> doAvoid(String varName, TypeContext ctx, int count) {
        if (path != null && path.getFreeVariables().contains(varName)) {
            final EffectDeclType dt = findEffectDeclType(ctx);
            if (dt.getEffectSet() != null) {
                if (dt.getEffectSet().getEffects().size() == 1
                        && dt.getEffectSet().getEffects().iterator().next().equals(this)) {
                    // avoid infinite loops, just in case
                    // TODO: make this more principled
                    final Set<Effect> s = new HashSet<Effect>();
                    s.add(this);
                    return s;
                }
                // different effects, so call recursively
                final Set<Effect> s = new HashSet<Effect>();
                for (final Effect e : dt.getEffectSet().getEffects()) {
                    s.addAll(e.doAvoid(varName, ctx, count + 1));
                }
                return s;
            }
        }

        // was best effort anyway
        // TODO: be more principled
        final Set<Effect> s = new HashSet<Effect>();
        s.add(this);
        return s;
    }

    public void adaptVariables(GenContext ctx) {
        if (path == null) {
            path = ctx.getContainerForTypeAbbrev(name);
        }
        if (path == null) {
            ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, loc, name);
        }
        path = path.adaptVariables(ctx);
    }
}