package wyvern.target.corewyvernIL.type;

import java.io.IOException;
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
		this(selfName, declTypes, false);
	}

	public StructuralType(String selfName, List<DeclType> declTypes, boolean resourceFlag) {
		super();
		this.selfName = selfName;
		// check a sanity condition
		if (declTypes != null && declTypes.size()>0)
			if (declTypes.get(0) == null)
				throw new NullPointerException("invariant: decl types should not be null");
		this.declTypes = declTypes;
		this.resourceFlag = resourceFlag;
	}
	
	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		String newIndent = indent + "    ";
		if (resourceFlag)
			dest.append("resource ");
		dest.append("type { ").append(selfName).append(" =>\n");
		for (DeclType dt : declTypes) {
			dt.doPrettyPrint(dest, newIndent);
		}		
		dest.append(indent).append("  }");
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
	
	/*public void setDeclTypes(List<DeclType> declTypes) {
		this.declTypes = declTypes;
	}*/

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public StructuralType getStructuralType(TypeContext ctx) {
		return this;
	}
	
	@Override
	public boolean isSubtypeOf(Type t, TypeContext ctx) {
		if (t instanceof NominalType) {
			StructuralType st = ((NominalType) t).getStructuralType(ctx);
			if (st == null) 
				return false; // abstract type; I am not a subtype of this
			else
				return isSubtypeOf(st, ctx);
		}
		
		if (!(t instanceof StructuralType))
			return false;
		
		StructuralType st = (StructuralType) t;
		
		for (DeclType dt : st.declTypes) {
			DeclType candidateDT = findDecl(dt.getName(), ctx);
			if (candidateDT == null || !candidateDT.isSubtypeOf(dt, ctx)) {
				return false;
			}
		}
		
		// a resource type is not a subtype of a non-resource type
		if (resourceFlag && !st.resourceFlag)
			return false;
		
		return true;
	}

	@Override
	public DeclType findDecl(String declName, TypeContext ctx) {
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
		return new StructuralType(selfName, newDTs, resourceFlag);
	}
	
	@Override
	public String toString() {
		String ret = (resourceFlag?"[resource ":"[") + selfName + " => ";
		for(DeclType declt : declTypes) {
			ret += declt.toString();
			ret += ";";
		}
		return ret + "]";
	}
}
