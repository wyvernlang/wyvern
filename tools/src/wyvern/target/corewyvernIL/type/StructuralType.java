package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class StructuralType extends ValueType {

	private String selfName;
	private List<DeclType> declTypes;
	
	public StructuralType(String selfName, List<DeclType> declTypes) {
		super();
		this.selfName = selfName;
		this.declTypes = declTypes;
	}

	public String getSelfName() {
		return selfName;
	}
	
	public void setSelfName(String selfName) {
		this.selfName = selfName;
	}
	
	public List<DeclType> getDeclTypes() {
		return declTypes;
	}
	
	public void setDeclTypes(List<DeclType> declTypes) {
		this.declTypes = declTypes;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public StructuralType getStructuralType() {
		return this;
	}
	
	@Override
	public boolean isSubtypeOf(Type t, TypeContext ctx) {
		if (t instanceof NominalType)
			// TODO: see if NominalType is equal to a known StructuralType first!
			return false;
		
		if (!(t instanceof StructuralType))
			return false;
		
		StructuralType st = (StructuralType) t;
		
		for (DeclType dt : st.declTypes) {
			boolean found = false;
			for (DeclType mdt : declTypes) {
				if (mdt.isSubtypeOf(dt, ctx)) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		
		return true;
	}

}
