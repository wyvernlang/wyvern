package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.tools.errors.HasLocation;

/**
 * This abstraction allows more efficient code generation by
 * postponing generation of code that refers to a method
 * until we find out whether a method call is adjacent.  In
 * other words calling genExprWithArgs() generates more
 * efficient code than calling genExpr() followed by calling
 * "apply()" in the case where genExpr() would have to do
 * eta-expansion of the method.
 * 
 * @author aldrich
 */
public interface CallableExprGenerator {
    public IExpr genExpr();

    public IExpr genExprWithArgs(List<? extends IExpr> args, HasLocation loc);
    
    /* Returns null if no argument type is expected */
    public DefDeclType getDeclType(TypeContext ctx);

}
