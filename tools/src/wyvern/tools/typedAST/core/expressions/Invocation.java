package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InvocationExprGenerator;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Invocation extends AbstractExpressionAST implements CoreAST, Assignable {

    @Override
    public String toString() {
        String result = "";
        if (receiver != null) {
            result += receiver + ".";
        }
        result += operationName;
        if (argument != null) {
            result += "(" + argument + ')';
        }
        return result;
    }

    private String operationName;
    private ExpressionAST receiver;
    private ExpressionAST argument;
    private FileLocation location = FileLocation.UNKNOWN;

    /**
     * Invocation of an operation on two operands.
     *
     * @param op1 the first operand
     * @param op2 the second operand
     * @param operatorName the operator invoked.
     * @param fileLocation the location in the source where the operation occurs
     */
    public Invocation(TypedAST op1, String operatorName, TypedAST op2, FileLocation fileLocation) {
        this.receiver = (ExpressionAST) op1;
        this.argument = (ExpressionAST) op2;
        this.operationName = operatorName;
        this.location = fileLocation;
    }

    public TypedAST getArgument() {
        return argument;
    }

    public ExpressionAST getReceiver() {
        return receiver;
    }

    public String getOperationName() {
        return operationName;
    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public IExpr generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {

        CallableExprGenerator generator = getCallableExpr(ctx);

        List<IExpr> args = null;
        if (argument != null) {
            args = new ArrayList<IExpr>();
            IExpr arg = ((ExpressionAST) argument).generateIL(ctx, null, dependencies); 
            if (!(argument instanceof UnitVal)) { // TODO: This is hacky. Refactor me to avoid
                args.add(arg);
            }
        }
        
        // Invoking property of a dynamic object; don't bother validating things.
        if (generator.getDeclType(ctx) == null) {
            if (argument != null) {
                return new MethodCall(
                        receiver.generateIL(ctx, null, null),
                        operationName, args, this, false);
            } else {
                return new FieldGet(
                        receiver.generateIL(ctx, null, null),
                        operationName,
                        location);
            }
        }

        if (argument != null) {
            return generator.genExprWithArgs(args, this, false, ctx);
        } else {
            return generator.genExpr(this.getLocation());
        }
    }

    @Override
    public CallableExprGenerator getCallableExpr(GenContext genCtx) {
        return new InvocationExprGenerator(
                receiver.generateIL(genCtx, null, null),
                operationName,
                genCtx,
                getLocation()
                );
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Invocation(operationName=");
        sb.append(operationName);
        sb.append(", receiver=");
        if (receiver != null) {
            sb.append(receiver.prettyPrint());
        } else {
            sb.append("null");
        }
        sb.append(", argument=");
        if (argument != null) {
            sb.append(argument.prettyPrint());
        } else {
            sb.append("null");
        }
        sb.append(")");
        return sb;
    }
}

