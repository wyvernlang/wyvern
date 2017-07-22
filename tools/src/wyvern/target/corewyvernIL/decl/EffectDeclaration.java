package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Effect;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class EffectDeclaration extends NamedDeclaration {
	private Effect e;
	
	public EffectDeclaration(Effect e) {
		super(e.getName(), e.getLocation()); // not sure if necessary...
		this.e = e;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return null; //emitILVisitor.visit(state, this);
	}
//
//	public HashSet<String> getEffectSet() {
//		return effectSet;
//	}
	
	@Override
	public DeclType getDeclType() {
		return new EffectDeclType(getName(), e.getEffectSet(), getLocation());
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) { // actually effectCheck
		e.effectCheck(ctx, thisCtx); // throw exception if problem	// could just not pass in thisCtx?
		return getDeclType();
	}

	@Override
	public Set<String> getFreeVariables() {
		// TODO Auto-generated method stub
		return null;
	}
}