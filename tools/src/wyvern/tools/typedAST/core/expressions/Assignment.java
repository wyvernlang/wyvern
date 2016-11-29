package wyvern.tools.typedAST.core.expressions;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.GetterAndSetterGeneration;

public class Assignment extends CachingTypedAST implements CoreAST {

    private ExpressionAST target;
    private ExpressionAST value;
    private ExpressionAST nextExpr;
    private FileLocation location = FileLocation.UNKNOWN;

    /**
      * An assignment from a r-value (value) to an l-value (target).
      *
      * @param target the receiver of the assignment
      * @param value  the expression on the right hand side of the =
      * @param fileLocation the location in the source code where the assignment occurs
      */
    public Assignment(TypedAST target, TypedAST value, FileLocation fileLocation) {
        this.target = (ExpressionAST) target;
        this.value = (ExpressionAST) value;
        this.location = fileLocation;
    }

    @Override
    protected Type doTypecheck(Environment env, Optional<Type> expected) {
        if (nextExpr == null) {
            if (!(target instanceof Assignable)) {
                throw new RuntimeException("Invalid target");
            }
            ((Assignable)target).checkAssignment(this, env);
            Type tT = target.typecheck(env, Optional.empty());
            Type vT = value.typecheck(env, Optional.of(tT));
            if (!vT.subtype(tT)) {
                ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this);
            }
        } else {
            nextExpr.typecheck(env, Optional.empty());
        }
        return new Unit();
    }

    public TypedAST getTarget() {
        return target;
    }

    public TypedAST getValue() {
        return value;
    }

    public TypedAST getNext() {
        return nextExpr;
    }

    @Override
    public Value evaluate(EvaluationEnvironment env) {
        if (!(target instanceof Assignable)) {
            reportEvalError(VALUE_CANNOT_BE_APPLIED, target.toString(), this);
        }
        Value evaluated = ((Assignable) target).evaluateAssignment(this, env);
        if (nextExpr == null) {
            return evaluated;
        } else {
            return nextExpr.evaluate(env);
        }
    }

    @Override
    public Map<String, TypedAST> getChildren() {
        Hashtable<String, TypedAST> children = new Hashtable<>();
        children.put("target", target);
        children.put("value", value);
        return children;
    }

    @Override
    public ExpressionAST doClone(Map<String, TypedAST> nc) {
        return new Assignment(nc.get("target"), nc.get("value"), location);
    }

    public FileLocation getLocation() {
        return this.location;
    }

    private IExpr generateFieldGet(GenContext ctx, List<TypedModuleSpec> dependencies) {
    
    	// In most cases we can get a generator to do this for us.
    	CallableExprGenerator cegReceiver = target.getCallableExpr(ctx);
    	if (cegReceiver.getDeclType(ctx) != null)
    		return cegReceiver.genExpr();
    	
    	// If the receiver is dynamic (signified by getDeclType being null), we have to manually do this.
    	
    	if (target instanceof Invocation) {
        	Invocation invocation = (Invocation) target;
        	return new FieldGet(
        			invocation.getReceiver().generateIL(ctx, null, dependencies),
        			invocation.getOperationName(),
        			getLocation());
    	}
    	else if (target instanceof Variable) {
    		return ctx.lookupExp(((Variable)target).getName(), getLocation());
    	}
    	else {
    		throw new RuntimeException("Getting field of dynamic object, but dynamic object's AST is some unsupported type: " + target.getClass());
    	}
    }
    
    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {
        
        // Figure out expression being assigned and target it is being assigned to.
        IExpr exprToAssign = value.generateIL(ctx, expectedType, dependencies);
        ValueType exprType = exprToAssign.typeCheck(ctx); 
        IExpr exprFieldGet = generateFieldGet(ctx, dependencies);
        
        // Assigning to a top-level var.
        if (exprFieldGet instanceof MethodCall) {
            
            // Figure out the var being assigned and get the name of its setter.
            MethodCall methCall = (MethodCall) exprFieldGet;
            String methName     = methCall.getMethodName();
            String varName      = GetterAndSetterGeneration.getterToVarName(methName);
            String setterName   = GetterAndSetterGeneration.varNameToSetter(varName);
            
            // Return an invocation to the setter w/ appropriate argmuents supplied.
            IExpr receiver = methCall.getObjectExpr();
            List<IExpr> setterArgs = new LinkedList<>();
            setterArgs.add(exprToAssign);
            return new MethodCall(receiver, setterName, setterArgs, this);
            
        } else if (exprFieldGet instanceof FieldGet) {
            // Assigning to an object's field.
            // Return a FieldSet to the appropriate field.
            FieldGet fieldGet = (FieldGet) exprFieldGet;
            String fieldName = fieldGet.getName();
            IExpr objExpr = fieldGet.getObjectExpr();
            return new wyvern.target.corewyvernIL.expression.FieldSet(
                exprType,
                objExpr,
                fieldName,
                exprToAssign);
        } else {
            // Unknown what's going on.
            ToolError.reportError(ErrorMessage.NOT_ASSIGNABLE, this);
            return null;
        }
    }
}
