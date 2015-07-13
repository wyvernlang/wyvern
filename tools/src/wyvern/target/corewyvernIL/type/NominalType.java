package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class NominalType extends ValueType{
	
	private Path path;
	private String typeMember;
	
	
	public NominalType(Path path, String typeMember) {
		super();
		this.path = path;
		this.typeMember = typeMember;
	}

	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public String getTypeMember() {
		return typeMember;
	}
	
	public void setTypeMember(String typeMember) {
		this.typeMember = typeMember;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
