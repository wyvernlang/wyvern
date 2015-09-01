package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public interface EmitOIR {
	public abstract <T> T acceptVisitor (ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv);
}
