package wyvern.target.corewyvernIL.type;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class StructuralType extends ValueType {

	private String selfName;
	private List<DeclType> declTypes;
	private boolean resourceFlag = false;
	
	public StructuralType(String selfName, List<DeclType> declTypes) {
		super();
		this.selfName = selfName;
		this.declTypes = declTypes;
	}

	public StructuralType(String selfName, List<DeclType> declTypes, boolean resourceFlag) {
		super();
		this.selfName = selfName;
		this.declTypes = declTypes;
		this.resourceFlag = resourceFlag;
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
			DeclType candidateDT = findDecl(dt.getName());
			if (candidateDT == null || !candidateDT.isSubtypeOf(dt, ctx)) {
				System.out.println(candidateDT);
				System.out.println(dt);
				return false;
			}
		}
		
		return true;
	}

	@Override
	public DeclType findDecl(String declName) {
		for (DeclType mdt : declTypes) {
			if (mdt.getName().equals(declName)) {
				return mdt;
			}
		}
		return null;
	}

	@Override
	public ValueType adapt(View v) {
		List<DeclType> newDTs = new LinkedList<DeclType>();
		for (DeclType dt : declTypes) {
			newDTs.add(dt.adapt(v));
		}
		return new StructuralType(selfName, newDTs);
	}
	
	@Override
	public String toString() {
		String ret = "st:" + selfName;
		//for(DeclType declt : declTypes) {
		//	ret += declt.toString();
		//	ret += ";";
		//}
		return ret;
	}
}
