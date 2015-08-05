package wyvern.target.corewyvernIL.decltype;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


public class DefDeclType extends DeclTypeWithResult {

	private List<FormalArg> args;
	
	public DefDeclType(String method, ValueType returnType, List<FormalArg> args) {
		super(method, returnType);
		this.args = args;
	}

	public List<FormalArg> getFormalArgs ()
	{
		return args;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DeclType adapt(View v) {
		List<FormalArg> newArgs = new LinkedList<FormalArg>();
		for (FormalArg a : args) {
			newArgs.add(new FormalArg(a.getName(), a.getType().adapt(v)));
		}
		return new DefDeclType(this.getName(), this.getRawResultType().adapt(v), newArgs);
	}
}
