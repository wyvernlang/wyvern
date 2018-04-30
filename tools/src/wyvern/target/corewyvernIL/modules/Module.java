package wyvern.target.corewyvernIL.modules;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;

public class Module {
    private final TypedModuleSpec spec;
    private final IExpr expr;
    private final List<TypedModuleSpec> dependencies;
    private Value cachedValue;

    public Module(TypedModuleSpec spec, IExpr program, List<TypedModuleSpec> dependencies) {
        this.spec = spec;
        this.expr = program;
        this.dependencies = dependencies;
    }

    public TypedModuleSpec getSpec() {
        return spec;
    }

    public Expression getExpression() {
        return (Expression) expr;
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
}
