package wyvern.target.corewyvernIL.modules;

import java.util.List;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;

public class Module {
    private final TypedModuleSpec spec;
    private final IExpr expr;
    private final List<TypedModuleSpec> dependencies;
    private final boolean isPure;
    private Value cachedValue;
    private BindingSite site;

    public Module(TypedModuleSpec spec, IExpr program, Value valueForCache, List<TypedModuleSpec> dependencies) {
        this.spec = spec;
        this.expr = program;
        this.dependencies = dependencies;
        if (valueForCache != null && !ModuleResolver.isFunctionType(spec.getType())) {
            this.cachedValue = valueForCache;
            this.isPure = true;
        } else {
            this.isPure = false;
        }
        spec.setModule(this);
    }

    public TypedModuleSpec getSpec() {
        return spec;
    }
    
    public boolean isPure() {
        return isPure;
    }

    /** Returns an expression representing this module or module def.  The expresison will evalute to an object.
     * The object will be the module itself, or will have a single apply() method in the case that this is really
     * a module def.
     * 
     * @return
     */
    public Expression getExpression() {
        return (Expression) expr;
    }

    public IExpr getExprWithCache() {
        if (isPure()) {
            return cachedValue;
        }
        return expr;
    }
    
    /** Returns the binding site related to this module.
     * 
     * @return
     */
    public BindingSite getBindingSite() {
        if (site == null) {
            site = new BindingSite(spec.getQualifiedName());
        }
        
        return site;
    }

    /** Returns a binding representing this module or module def.  This is the same as getExpression(), except
     * it returns a SeqExpr that binds the expression to the module's name.
     * 
     * @return
     */
    public SeqExpr getBinding() {
        SeqExpr result = new SeqExpr();
        result.addBindingLast(new VarBinding(getBindingSite(), spec.getType(), expr));
        return result;
    }

    /** Returns a transitive, but not necessarily carefully ordered,
     *  list of the dependencies of this module */
    public List<TypedModuleSpec> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Module(" + spec.getQualifiedName() + ")";
    }

    public boolean dependsOn(TypedModuleSpec o2) {
        return dependencies.contains(o2);
    }

    /** Reduces the module to a value using the context passed in, which should
     * match the context the module was typechecked against.  This value is
     * cached so the second time the method is called, the cached value will be
     * returned.
     *
     * @param ctx
     * @return
     */
    public Value getAsValue(EvalContext ctx) {
        if (cachedValue == null) {
            cachedValue = expr.interpret(ctx);
        }
        return cachedValue;
    }

    /** Reduces the module to a value using the passed-in ModuleResolver.  This value is
     * cached so the second time the method is called, the cached value will be
     * returned.
     *
     * @return
     */
    public Value getAsValue(ModuleResolver resolver) {
        if (cachedValue == null) {
            cachedValue = resolver.wrapWithCtx(expr, dependencies, Globals.getStandardEvalContext()).interpret(Globals.getStandardEvalContext());
        }
        return cachedValue;
    }
}
