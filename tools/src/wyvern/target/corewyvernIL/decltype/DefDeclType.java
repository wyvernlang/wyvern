package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
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
		if (!(dt instanceof DefDeclType)) {
			return false;
		}
		DefDeclType ddt = (DefDeclType) dt;
		if (args.size() != ddt.args.size() || !ddt.getName().equals(getName()))
			return false;
		for (int i = 0; i < args.size(); ++i) {
			if (! (ddt.args.get(i).getType().isSubtypeOf(args.get(i).getType(), ctx))) {
				return false;
			}
		}
		ValueType rawResultType = this.getRawResultType();
		ValueType otherRawResultType = ddt.getRawResultType();
		return rawResultType.isSubtypeOf(otherRawResultType, ctx);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getRawResultType() == null) ? 0 : getRawResultType().hashCode());
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefDeclType other = (DefDeclType) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getRawResultType() == null) {
			if (other.getRawResultType() != null)
				return false;
		} else if (!getRawResultType().equals(other.getRawResultType()))
			return false;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		return true;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("def ").append(getName()).append('(');
		boolean first = true;
		for (FormalArg arg: args) {
			if (first)
				first = false;
			else
				dest.append(", ");
			arg.doPrettyPrint(dest, indent);
		}
		String newIndent = indent+"    ";
		dest.append(") : ");
		getRawResultType().doPrettyPrint(dest, newIndent);
		dest.append('\n');
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
