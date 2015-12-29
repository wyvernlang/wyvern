package wyvern.tools.typedAST.core.expressions;

import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ToolError.reportEvalError;

public class Assignment extends CachingTypedAST implements CoreAST {
	
	private ExpressionAST target;
	private ExpressionAST value;
	
	private ExpressionAST nextExpr;

	public Assignment(TypedAST target, TypedAST value, FileLocation fileLocation) {
		this.target = (ExpressionAST) target;
		this.value = (ExpressionAST) value;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(target, value);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		if (nextExpr == null) {
			if (!(target instanceof Assignable))
				throw new RuntimeException("Invalid target");
			((Assignable)target).checkAssignment(this, env);
			Type tT = target.typecheck(env, Optional.empty());
			Type vT = value.typecheck(env, Optional.of(tT));
			if (!vT.subtype(tT))
				ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this);
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
		if (!(target instanceof Assignable))
			reportEvalError(VALUE_CANNOT_BE_APPLIED, target.toString(), this);
		Value evaluated = ((Assignable) target).evaluateAssignment(this, env);
		if (nextExpr == null)
			return evaluated;
		else
			return nextExpr.evaluate(env);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("target", target);
		children.put("value", value);
		return children;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        Expression objectExpr = null;
        String fieldName = null;
        if (target instanceof Invocation) {
            objectExpr = ExpressionWriter.generate(iwriter->((Invocation)target).getReceiver().codegenToIL(environment, iwriter));
            fieldName = ((Invocation) target).getOperationName();
        } else if (target instanceof wyvern.tools.typedAST.core.expressions.Variable) {
            objectExpr = new Variable("this");
            fieldName = ((wyvern.tools.typedAST.core.expressions.Variable) target).getName();
        } else {
            throw new RuntimeException("Invalid assignment for codegen");
        }
        writer.write(new FieldSet(value.getType().generateILType(), objectExpr, fieldName, ExpressionWriter.generate(iwriter->value.codegenToIL(environment, iwriter))));
    }

    @Override
	public ExpressionAST doClone(Map<String, TypedAST> nc) {
		return new Assignment(nc.get("target"), nc.get("value"), location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		
		// Figure out expression being assigned.
		CallableExprGenerator cegExpr = value.getCallableExpr(ctx);
		Expression exprToAssign = cegExpr.genExpr();
		ValueType exprType = exprToAssign.getExprType();
		
		// Figure out receiver and field.
		CallableExprGenerator cegReceiver = target.getCallableExpr(ctx);
		Expression exprFieldGet = cegReceiver.genExpr();
		if (!(exprFieldGet instanceof FieldGet))
			ToolError.reportError(ErrorMessage.NOT_ASSIGNABLE, this);
		FieldGet fieldGet = (FieldGet) exprFieldGet;
		String fieldName = fieldGet.getName();
		Expression objExpr = fieldGet.getObjectExpr();
		
		// Return FieldSet.
		return new wyvern.target.corewyvernIL.expression.FieldSet(exprType, objExpr, fieldName, exprToAssign);
	
	}
}
