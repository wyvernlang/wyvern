package wyvern.target.corewyvernIL.expression;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class New extends Expression {
	
	@Override
	public String toString() {
		return "New [" + selfName + " : " + this.getExprType() + " => " + decls + "]";
	}

	private List<Declaration> decls;
	private String selfName;
	
	public New(List<Declaration> decls, String selfName, ValueType type) {
		super(type);
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
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		List<DeclType> dts = new LinkedList<DeclType>();
		
		// check that all decls are well-typed
		for (Declaration d : decls) {
			DeclType dt = d.typeCheck(ctx);
			dts.add(dt);
		}
		
		// check that everything in the claimed structural type was accounted for
		ValueType t = getExprType();
		StructuralType requiredT = t.getStructuralType();
		StructuralType actualT = new StructuralType(selfName, dts);
		if (!actualT.isSubtypeOf(requiredT, ctx)) {
			throw new RuntimeException("typechecking error: not a subtype");
		}
		
		return t;
	}

	@Override
	public Value interpret(EvalContext ctx) {
		// evaluate all decls
		List<Declaration> ds = new LinkedList<Declaration>();
		for (Declaration d : decls) {
			Declaration newD = d.interpret(ctx);
			ds.add(newD);
		}
		return new ObjectValue(ds, selfName, getExprType());
	}
}
