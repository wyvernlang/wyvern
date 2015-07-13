package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


public class AbstractDeclType extends DeclType implements EmitOIR{

	private String typeName;
	
	public AbstractDeclType(String typeName) {
		super();
		this.typeName = typeName;
	}

	public String getTypeName ()
	{
		return typeName;
	}
	
	public void setTypeName (String _typeName)
	{
		typeName = _typeName;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
