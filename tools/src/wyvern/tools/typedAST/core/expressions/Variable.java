package wyvern.tools.typedAST.core.expressions;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.types.Type;


public class Variable extends AbstractExpressionAST implements CoreAST, Assignable {

    private NameBinding binding;
    private FileLocation location = FileLocation.UNKNOWN;

    public Variable(NameBinding binding, FileLocation location) {
        this.binding = binding;
        this.location = location;
    }

    public String getName() {
        return this.binding.getName();
    }

    @Override
    public String toString() {
        return binding.getName();
    }
    
    @Override
    public Type getType() {
        return binding.getType();
    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public IExpr generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {
        return ctx.lookupExp(getName(), location);
    }

    @Override
    public CallableExprGenerator getCallableExpr(GenContext ctx) {
        try {
            return ctx.getCallableExpr(getName());
        } catch (RuntimeException e) {
            ToolError.reportError(VARIABLE_NOT_DECLARED, location, getName());
            throw new RuntimeException("impossible");
        }
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Variable(\"");
        sb.append(binding.getName());
        sb.append("\" : ");
        if (binding.getType() != null) {
            sb.append(binding.getType().toString());
        } else {
            sb.append("null");
        }
        sb.append(")");
        return sb;
    }
}
