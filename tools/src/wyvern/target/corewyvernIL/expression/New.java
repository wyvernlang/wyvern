package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.type.ValueType;

public class New extends Expression{
	
	private List<Declaration> decls;
	private String selfName;
	
	public New(List<Declaration> decls, String selfName) {
		super();
		this.decls = decls;
		this.selfName = selfName;
	}

	public List<Declaration> getDecls() {
		return decls;
	}
	
	public void setDecls(List<Declaration> decls) {
		this.decls = decls;
	}
	
	public String getSelfName() {
		return selfName;
	}
	
	public void setSelfName(String selfName) {
		this.selfName = selfName;
	}

	@Override
	public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
