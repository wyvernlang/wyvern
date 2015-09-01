package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;

public interface emitIL {
	public abstract String acceptEmitILVisitor (EmitILVisitor emitILVisitor, Environment env);
}
