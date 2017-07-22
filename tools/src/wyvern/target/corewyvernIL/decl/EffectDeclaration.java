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
	private Set<Effect> effectSet;
	
	public EffectDeclaration(String name, Set<Effect> effectSet, FileLocation loc) {
		super(name, loc);
		this.effectSet = effectSet;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return null; //emitILVisitor.visit(state, this);
	}

	public Set<Effect> getEffectSet() {
		return effectSet;
	}
	
	@Override
	public DeclType getDeclType() {
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) { // technically "effectCheck"
		for (Effect e : effectSet) {
			ValueType vt = ctx.lookupTypeOf(e.getPath().getName());
			if (vt == null){
				throw new RuntimeException("Path not found.");
			} else {
				if (!(vt.findDecl(e.getName(), ctx).equals(getDeclType()))) {
					throw new RuntimeException("Effect name not found in path.");
				}
			}
		}
		return getDeclType();
	}

	@Override
	public Set<String> getFreeVariables() {
		// TODO Auto-generated method stub
		return null;
	}
}