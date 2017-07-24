package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.metadata.IsTailCall;
import wyvern.target.corewyvernIL.metadata.Metadata;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.support.ViewExtension;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;

public class MethodCall extends Expression {

	private IExpr objectExpr;
	private String methodName;
	private List<? extends IExpr> args;
	private ValueType receiverType;

	public MethodCall(IExpr receiverExpr, String methodName,
			List<? extends IExpr> args2, HasLocation location) {
		super(location != null ? location.getLocation():null);
		//if (getLocation() == null || getLocation().line == -1)
		//	throw new RuntimeException("missing location");
		this.objectExpr = receiverExpr;
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
	
	private ValueType getReceiverType(TypeContext ctx) {
		if (receiverType == null) {
			receiverType = objectExpr.typeCheck(ctx);
		}
		return receiverType;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
	    // If calling on a dynamic receiver, it types to Dyn (provided the args typecheck)
		if (Util.isDynamicType(getReceiverType(ctx))) {
		    for (IExpr arg : args) {
		        arg.typeCheck(ctx);
		    }
		    return Util.dynType();
		}
		typeMethodDeclaration(ctx);
		return getExprType();
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Invokable receiver = (Invokable)objectExpr.interpret(ctx);
		List<Value> argValues = new ArrayList<Value>(args.size());
		for (int i = 0; i < args.size(); ++i) {
			IExpr e = args.get(i);
			argValues.add(e.interpret(ctx));
		}
		if (isTailCall()) {
			return new SuspendedTailCall(this.getExprType(), this.getLocation()) {

				@Override
				public Value interpret(EvalContext ignored) {
					return receiver.invoke(methodName, argValues);
				}
				
			};
		}
		return trampoline( receiver.invoke(methodName, argValues) );
	}

	static Value trampoline(Value v) {
		while (v instanceof SuspendedTailCall) {
			v = v.interpret(null);
		}
		return v;
	}
	
    public boolean isTailCall() {
        for (Metadata m : getMetadata()) {
            if (m instanceof IsTailCall) return true;
        }
        return false;
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
	 * Type the declaration for the method being invoked.
	 * @param ctx: ctx in which invocation happens.
	 * @return the declaration of the method.
	 */
	public DefDeclType typeMethodDeclaration(TypeContext ctx) {

		// Typecheck receiver.
		ValueType receiver = getReceiverType(ctx);
		StructuralType receiverType = receiver.getStructuralType(ctx);

		// Sanity check: make sure it has declarations.
		List<DeclType> declarationTypes = receiverType.findDecls(methodName, ctx);
		if (declarationTypes.isEmpty()) {
			ToolError.reportError(ErrorMessage.NO_SUCH_METHOD, this, methodName);
		}

		// Go through all declarations, typechecking against the actual types passed in...
		List<ValueType> actualArgTypes = getArgTypes(ctx);
		List<ValueType> formalArgTypes = null;
		
		// ...use this context to do that.
		TypeContext newCtx = null;
		for (DeclType declType : declarationTypes) {
		    formalArgTypes = new LinkedList<ValueType>();
		    
			// Ignore non-methods.
			newCtx = ctx;
			if (!(declType instanceof DefDeclType)) continue;
			DefDeclType defDeclType = (DefDeclType) declType;

			// Check it has correct number of arguments.
			List<FormalArg> formalArgs = defDeclType.getFormalArgs();
			if (args.size() != formalArgs.size()) continue;

			// Typecheck actual args against formal args of this declaration.
			boolean argsTypechecked = true;
			View v = View.from(objectExpr, newCtx);
			for (int i = 0; i < args.size(); ++i) {

				// Get info about the formal arguments.
				FormalArg formalArg = formalArgs.get(i);
				ValueType formalArgType = formalArg.getType().adapt(v);
				TypeContext thisCtx = newCtx.extend(receiverType.getSelfName(), receiverType);
                if (!(objectExpr instanceof Variable))
                    // adaptation for the receiver won't have worked, so try avoiding "this"
                    formalArgType = formalArgType.avoid(receiverType.getSelfName(), thisCtx);
                formalArgTypes.add(formalArgType);
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
					v = new ViewExtension(new Variable(defDeclType.getFormalArgs().get(i).getName()), (Variable) e, v);
				}
			}

			// We were able to typecheck; figure out the return type, and set the method declaration.
			if (argsTypechecked) {
				ctx = newCtx;
				ValueType resultType = defDeclType.getResultType(v);
				resultType = resultType.adapt(v);
				if (!(objectExpr instanceof Variable))
				    // adaptation for the receiver won't have worked, so try avoiding "this"
				    resultType = resultType.avoid(receiverType.getSelfName(), newCtx);
				for (int i = args.size()-1; i >= 0; --i) {
					resultType = resultType.avoid(formalArgs.get(i).getName(), ctx);
				}
				setExprType(resultType);
				return defDeclType;
			}
		}

		// Couldn't find an appropriate method declaration. Build up a nice error message.
		StringBuilder errMsg = new StringBuilder();
		//errMsg.append(methodName);
		//errMsg.append("(");
		for (int i = 0; i <= args.size() - 2; ++i) {
		    // add the argument only if it's not a generic
		    if (!((DefDeclType)declarationTypes.get(0)).getFormalArgs().get(i).getName().startsWith(DefDeclaration.GENERIC_PREFIX)) {
    			errMsg.append(actualArgTypes.get(i).desugar(ctx));
    			errMsg.append(", ");
		    }
		}
		if (args.size() > 0)
			errMsg.append(actualArgTypes.get(args.size() - 1).desugar(ctx));
		errMsg.append("; expected types ");
		DefDeclType ddt =(DefDeclType)declarationTypes.get(0); 
        for (int i = 0; i <= formalArgTypes.size() - 2; ++i) {
            // add the argument only if it's not a generic
            if (!ddt.getFormalArgs().get(i).getName().startsWith(DefDeclaration.GENERIC_PREFIX)) {
                errMsg.append(formalArgTypes.get(i).desugar(ctx));
                errMsg.append(", ");
            }
        }
        if (formalArgTypes.size() > 0)
            errMsg.append(formalArgTypes.get(formalArgTypes.size() - 1).desugar(ctx));
		//errMsg.append(")");
		ToolError.reportError(ErrorMessage.NO_METHOD_WITH_THESE_ARG_TYPES, this, errMsg.toString());
		return null;
	}

}
