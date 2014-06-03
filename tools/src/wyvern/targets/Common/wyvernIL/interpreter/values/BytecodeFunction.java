package wyvern.targets.Common.wyvernIL.interpreter.values;

import wyvern.targets.Common.wyvernIL.IL.Def.Def.Param;
import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContext;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContextImpl;
import wyvern.targets.Common.wyvernIL.interpreter.core.Interpreter;

import java.util.ArrayList;
import java.util.List;

public class BytecodeFunction implements BytecodeValue {

	private final BytecodeContext coreContext;
	private final List<String> params;
	private final List<Statement> body;
	private final boolean isInit;

	/**
	 * instantiated a new function value
	 * 
	 * @param parameters
	 *            a list of parameters for this function
	 * @param bodyStmts
	 *            a list of statements representing the body of the function
	 * @param context
	 *            the current context of the program before defining the
	 *            function
	 * @param name
	 *            the name of the function
	 */
	public BytecodeFunction(List<Param> parameters, List<Statement> bodyStmts,
			BytecodeContext context, String name) {
		body = bodyStmts;
		params = new ArrayList<String>();
		for (Param param : parameters) {
			params.add(param.getName());
		}
		coreContext = new BytecodeContextImpl(context);
		if(! name.equals("$init")) {
			isInit = false;
			coreContext.addToContext(name, this);
			coreContext.addToContext("this",new BytecodeRef(null));
		} else {
			isInit = true;
		}
	}

	/**
	 * executes the function
	 * 
	 * @param args
	 *            the arguments associated with the call to the function
	 * @return the final result of the function's execution
	 */
	public BytecodeValue run(List<BytecodeValue> args) {
		BytecodeContext context = new BytecodeContextImpl(coreContext);
		return run(args,context);
	}
	
	public BytecodeValue run(List<BytecodeValue> args, BytecodeContext ctx) {
		BytecodeContext context;
		if(isInit) {
			context = ctx;
		} else {
			context = new BytecodeContextImpl(coreContext);
		}
		for (int i = 0; i < args.size(); i++) {
			BytecodeValue val = args.get(i);
			String name = params.get(i);
			context.addToContext(name, val);
		}
		Interpreter interperter = new Interpreter(body, context);
		BytecodeValue res = interperter.execute();
		return res;
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		throw new RuntimeException("trying to do math with functions");
	}

	@Override
	public BytecodeValue dereference() {
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i));
			if (i == params.size() - 1) {
				continue;
			}
			sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	public void setThis(BytecodeValue thisClass) {
		if(isInit) {
			return;
		}
		coreContext.setThis(thisClass);
	}
}
