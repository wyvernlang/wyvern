package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class EffectDeclaration extends NamedDeclaration {
	HashSet<String> effectSet;
	
	public EffectDeclaration(String name, HashSet<String> effectSet, FileLocation loc) {
		super(name, loc);
		this.effectSet = effectSet;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	public HashSet<String> getEffectSet() {
		return effectSet;
	}
	
	@Override
	public DeclType getDeclType() {
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) { // actually effectCheck
		Iterator<String> iter = effectSet.iterator();
		while (iter.hasNext()) {
			String[] pathAndID = iter.next().split("\\."); // assume all effects to have format "path_name.id_name" for now
			// create Path obj from pathAndID[0]?
			// should be an effect obj with its own effectchecking method
		}
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
		
//		ValueType defType = definition.typeCheck(thisCtx); 
//		if (!defType.isSubtypeOf(getType(), thisCtx))
//			ToolError.reportError(ErrorMessage.ASSIGNMENT_SUBTYPING, this);
//		return getDeclType();
	}

	@Override
	public Set<String> getFreeVariables() {
		// TODO Auto-generated method stub
		return null;
	}
}