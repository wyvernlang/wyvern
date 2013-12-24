package wyvern.tools.bytecode.values;

import java.util.ArrayList;
import java.util.List;

import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.core.Interperter;

public class BytecodeFunction implements BytecodeValue {

	private final BytecodeContext coreContext;
	private final List<String> params;
	private final List<Statement> body;

	public BytecodeFunction(List<Param> parameters, List<Statement> b,
			BytecodeContext c, String name) {
		body = b;
		params = new ArrayList<String>();
		for (Param param : parameters) {
			params.add(param.getName());
		}
		coreContext = new BytecodeContextImpl(this,name,c.clone());
	}

	public List<String> getParams() {
		return params;
	}

	public List<Statement> getBody() {
		return body;
	}

	public BytecodeContext getContext() {
		return coreContext;
	}
	
	public BytecodeValue run(List<BytecodeValue> args) {
		BytecodeContext context = coreContext.clone();
		for(int i = 0 ; i < args.size(); i++) {
			BytecodeValue val = args.get(i);
			String name = params.get(i);
			context = new BytecodeContextImpl(val,name,context);
		}
		Interperter interperter = new Interperter(body,context);
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
		return params.toString();
	}
}
