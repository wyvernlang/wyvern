package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.support.ViewExtension;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class MethodCall extends Expression {

	private IExpr objectExpr;
	private String methodName;
	private List<? extends IExpr> args;

	public MethodCall(IExpr e, String methodName,
			List<? extends IExpr> args2, HasLocation location) {
		super(location != null ? location.getLocation():null);
		//if (getLocation() == null || getLocation().line == -1)
		//	throw new RuntimeException("missing location");
		this.objectExpr = e;
		this.methodName = methodName;
		this.args = args2;
		// sanity check
		if (args2.size() > 0 && args2.get(0) == null)
			throw new NullPointerException("invariant: no null args");
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		objectExpr.doPrettyPrint(dest,indent);
		dest.append('.').append(methodName).append('(');
		boolean first = true;
		for (IExpr arg : args) {
			if (first)
				first = false;
			else
				dest.append(", ");
			arg.doPrettyPrint(dest, indent);
		}
		dest.append(')');
	}

	public IExpr getObjectExpr() {
		return objectExpr;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<? extends IExpr> getArgs() {
		return args;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		
		// Typecheck receiver.
		ValueType receiver = objectExpr.typeCheck(ctx);
		StructuralType receiverType = receiver.getStructuralType(ctx);

		// Sanity check: make sure it has declarations.
		List<DeclType> declarationTypes = receiverType.findDecls(methodName, ctx);
		if (declarationTypes.isEmpty()) {
			ToolError.reportError(ErrorMessage.NO_SUCH_METHOD, this, methodName);
		}

		// Go through all declarations, typechecking against the actual types passed in...
		List<ValueType> actualArgTypes = getArgTypes(ctx);
		
		// ...use this context to do that.
		TypeContext newCtx = null;
		for (DeclType declType : declarationTypes) {
			
			// Ignore non-methods.
			newCtx = ctx;
			if (!(declType instanceof DefDeclType)) continue;
			DefDeclType ddt = (DefDeclType) declType;

			// Check it has correct number of arguments.
			List<FormalArg> formalArgs = ddt.getFormalArgs();
			if (args.size() != formalArgs.size()) continue;

			// Typecheck actual args against formal args of this declaration.
			boolean argsTypechecked = true;
			View v = View.from(objectExpr, newCtx);
			for (int i = 0; i < args.size(); ++i) {
				
				// Get info about the formal arguments.
				FormalArg formalArg = formalArgs.get(i);
				ValueType formalArgType = formalArg.getType().adapt(v);
				String formalArgName = formalArg.getName();
				ValueType actualArgType = actualArgTypes.get(i);
				
				// Check actual argument type accords with formal argument type.
				if (!actualArgType.isSubtypeOf(formalArgType, newCtx)) {
					argsTypechecked = false;
					break;
				}
				
				// Update context and view.
				newCtx = newCtx.extend(formalArgName, actualArgType);
				IExpr e = args.get(i);
				if (e instanceof Variable) {
					v = new ViewExtension(new Variable(ddt.getFormalArgs().get(i).getName()), (Variable) e, v);
				}
			}

			// We were able to typecheck; figure out the return type.
			if (argsTypechecked) {
				ctx = newCtx;
				ValueType resultType = ddt.getResultType(v);
				resultType = resultType.adapt(v);
				this.setExprType(resultType);
				return getExprType();
			}
		}

		// Couldn't find an appropriate method declaration. Build up a nice error message.
		StringBuilder errMsg = new StringBuilder();
		errMsg.append(methodName);
		errMsg.append("(");
		for (int i = 0; i <= args.size() - 2; ++i) {
			errMsg.append(actualArgTypes.get(i).toString());
			errMsg.append(", ");
		}
		if (args.size() > 0)
			errMsg.append(actualArgTypes.get(args.size() - 1).toString());
		errMsg.append(")");
		ToolError.reportError(ErrorMessage.NO_METHOD_WITH_THESE_ARG_TYPES, this, errMsg.toString());
		return null;
	}
	
	@Override
	public <T, E> T acceptVisitor(ASTVisitor <T, E> emitILVisitor,
			E env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Invokable receiver = (Invokable)objectExpr.interpret(ctx);
		List<Value> argValues = new ArrayList<Value>(args.size());
		for (int i = 0; i < args.size(); ++i) {
			IExpr e = args.get(i);
			argValues.add(e.interpret(ctx));
		}
		return receiver.invoke(methodName, argValues);		
	}

	@Override
	public Set<String> getFreeVariables() {
		Set<String> freeVars = objectExpr.getFreeVariables();
		for (IExpr arg : args) {
			freeVars.addAll(arg.getFreeVariables());
		}
		return freeVars;
	}
	
	public List<ValueType> getArgTypes(TypeContext ctx) {
		List<? extends IExpr> args = getArgs();
		return args.stream()
			.map(arg -> arg.typeCheck(ctx))
			.collect(Collectors.toList());
	}
	
	/**
	 * Get the declaration for the method being invoked.
	 * @param ctx: ctx in which invocation happens.
	 * @return the declaration of the method.
	 */
	public DefDeclType getMethodDeclaration(TypeContext ctx) {

		// Figure out type of receiver, and formal argument types.
		IExpr receiver = getObjectExpr();
		StructuralType receiverType = receiver.typeCheck(ctx).getStructuralType(ctx);
		List<ValueType> argTypes = getArgTypes(ctx);
						
		// Get all declared methods matching name of method being invoked.
		List<DeclType> matchingMeths = receiverType.findDecls(getMethodName(), ctx);
		
		// Look through each method, attempting to find the one which typechecks with these arguments.
		for (DeclType dt : matchingMeths) {
			
			// Check argument compatibility.
			if (!(dt instanceof DefDeclType)) continue;
			DefDeclType ddt = (DefDeclType) dt;
			List<FormalArg> formalArgs = ddt.getFormalArgs();
			if (args.size() != formalArgs.size())
				continue;
			
			// Attempt to typecheck formal argument types with actual argument types.
			boolean argsTypechecked = true;
			View view = View.from(receiver, ctx);
			for (int i = 0; i < args.size(); i++) {
				
				// Adapt to the view and typecheck.
				ValueType argType = formalArgs.get(i).getType().adapt(view);
				ValueType actualType = argTypes.get(i);
				if (!actualType.isSubtypeOf(argType, ctx)) {
					argsTypechecked = false;
					break;
				}
				
			}
			
			// If we typechecked, we've found the appropriate method for the method call.
			if (argsTypechecked) {
				return ddt;
			}
			
		}
		
		return null;
	}

}
