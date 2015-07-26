package wyvern.target.corewyvernIL.type;

import java.util.Arrays;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class NominalType extends ValueType{
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] {
		           path,
		           typeMember,
		    });
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NominalType))
			return false;
		NominalType other = (NominalType)obj;
		return path.equals(other.path) && typeMember.equals(other.typeMember);
	}

	@Override
	public String toString() {
		return path.toString() + "." + typeMember.toString();
	}

	private Path path;
	private String typeMember;
	
	
	public NominalType(String pathVariable, String typeMember) {
		super();
		this.path = new Variable(pathVariable);
		this.typeMember = typeMember;
	}

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
